package com.systrack.pro.controller;

import com.systrack.pro.concurrency.DeviceStatusReport;
import com.systrack.pro.model.Device;
import com.systrack.pro.repository.DeviceRepository;
import com.systrack.pro.service.MonitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class SystemController {

    private final MonitorService monitorService;
    private final DeviceRepository deviceRepository;

    public SystemController(MonitorService monitorService, DeviceRepository deviceRepository) {
        this.monitorService = monitorService;
        this.deviceRepository = deviceRepository;
    }
    
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return monitorService.getSystemHealth();
    }
    
    @GetMapping("/devices")
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    
    @PostMapping("/devices")
    public Device addDevice(@RequestBody Device device) {
        return deviceRepository.save(device);
    }
    
    @GetMapping("/devices/ingest")
    public ResponseEntity<String> ingestTelemetryData() {
        try {
        	List<Device> ingestedDevices = monitorService.parseTelemetryManifest("performance_dump.csv");
            
            deviceRepository.saveAll(ingestedDevices);
            
            return ResponseEntity.ok(String.format(
                    "Successfully parsed and ingested %d valid device profiles into the database.", 
                    ingestedDevices.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error parsing telemetry file: " + e.getMessage());
        }
    }

    @GetMapping("/devices/diagnose")
    public ResponseEntity<List<DeviceStatusReport>> executeConcurrentDiagnostics() {
        List<Device> devicesInInventory = deviceRepository.findAll();
        
        if (devicesInInventory.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        System.out.println("\n>>> REST Request Received: Booting Multi-threaded Diagnostic Pipeline...");
        
        List<DeviceStatusReport> telemetryReport = monitorService.pollDevicesConcurrently(devicesInInventory);
        
        return ResponseEntity.ok(telemetryReport);
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Device> activeInventory = deviceRepository.findAll();
        model.addAttribute("devices", activeInventory);
        
        return "dashboard";
    }
}