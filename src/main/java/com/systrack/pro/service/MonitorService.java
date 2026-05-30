package com.systrack.pro.service;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import com.systrack.pro.concurrency.DiagnosticsTask;
import com.systrack.pro.concurrency.DeviceStatusReport;
import com.systrack.pro.exception.DeviceOverloadException;
import com.systrack.pro.model.Device;
import com.systrack.pro.model.NetworkSwitch;
import com.systrack.pro.model.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;

@Service
public class MonitorService {

    public Map<String, Object> getSystemHealth() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        Map<String, Object> stats = new HashMap<>();

        double cpuLoad = osBean.getCpuLoad() * 100;
        stats.put("cpuUsage", String.format("%.2f%%", cpuLoad));

        long totalMem = osBean.getTotalMemorySize();
        long freeMem = osBean.getFreeMemorySize();
        long usedMem = totalMem - freeMem;
        stats.put("usedMemory", (usedMem / 1024 / 1024) + " MB");
        stats.put("totalMemory", (totalMem / 1024 / 1024) + " MB");

        boolean isDocker = new File("/.dockerenv").exists();
        stats.put("deploymentMode", isDocker ? "Docker Container" : "Standalone Tomcat");

        return stats;
    }

    public List<Device> parseTelemetryManifest(String fileName) throws Exception {
        List<Device> parsedDevices = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(fileName);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            parsedDevices = br.lines() 
                    .skip(1)           
                    .map(csvLine -> csvLine.split(",")) 
                    .filter(columns -> columns.length >= 6) 
                    .map(columns -> {
                        try {
                        	String id = columns[0].strip();
                        	String type = columns[1].strip();
                        	String ip = columns[2].strip();
                        	double cpu = Double.parseDouble(columns[3].strip());
                        	double mem = Double.parseDouble(columns[4].strip());
                        	int latency = Integer.parseInt(columns[5].strip());
                        	
                            Device deviceInstance;
                            if ("Server".equalsIgnoreCase(type)) {
                                deviceInstance = new Server(id, type, ip, cpu, mem, latency, 4, "Linux (Alpine)");
                            } else if ("NetworkSwitch".equalsIgnoreCase(type)) {
                                deviceInstance = new NetworkSwitch(id, type, ip, cpu, mem, latency, 48, 22);
                            } else {
                                return null; 
                            }

                            if (!deviceInstance.verifyHealthStatus()) {
                                deviceInstance.setMonitoringStatus("CRITICAL_OVERLOAD");
                            } else {
                                deviceInstance.setMonitoringStatus("HEALTHY");
                            }

                            return deviceInstance;

                        } catch (DeviceOverloadException e) {
                            System.err.println("[EXHAUSTION ERROR] Dropping invalid metrics: " + e.getMessage());
                            return null;
                        } catch (NumberFormatException e) {
                            System.err.println("[PARSING ERROR] Failed casting string token: " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull) 
                    .toList();
        }

        return parsedDevices;
    }

    public List<DeviceStatusReport> pollDevicesConcurrently(List<Device> devicesToPoll) {
        List<DeviceStatusReport> outputTelemetryBatch = new ArrayList<>();
        ExecutorService workerPool = Executors.newFixedThreadPool(4);
        CompletionService<DeviceStatusReport> activeCompletionService = new ExecutorCompletionService<>(workerPool);

        System.out.println(">>> Submitting task payloads to background execution pool...");
        
        for (Device device : devicesToPoll) {
            activeCompletionService.submit(new DiagnosticsTask(device));
        }

        for (int i = 0; i < devicesToPoll.size(); i++) {
            try {
                Future<DeviceStatusReport> completedFuture = activeCompletionService.take();
                DeviceStatusReport individualReport = completedFuture.get(); 
                
                System.out.println("[ENGINE MONITOR] Received response: " + individualReport);
                outputTelemetryBatch.add(individualReport);
                
            } catch (InterruptedException e) {
                System.err.println("Thread processing loop interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); 
                break;
            } catch (ExecutionException e) {
                System.err.println("Exception caught inside worker task: " + e.getMessage());
            }
        }

        workerPool.shutdown();
        return outputTelemetryBatch;
    }
}