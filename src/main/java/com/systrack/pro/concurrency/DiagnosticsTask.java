package com.systrack.pro.concurrency;

import com.systrack.pro.model.Device;
import java.util.Random;
import java.util.concurrent.Callable;

public class DiagnosticsTask implements Callable<DeviceStatusReport> {

    private final Device targetDevice;
    private final Random randomEngine = new Random();
    
    public DiagnosticsTask(Device targetDevice) {
        this.targetDevice = targetDevice;
    }

    @Override
    public DeviceStatusReport call() throws Exception {
        // Simulate dynamic network latency
        long startMetricTime = System.currentTimeMillis();
        long executionSimulatedLatency = randomEngine.nextLong(400, 2000); 
        Thread.sleep(executionSimulatedLatency); 
        
        long endMetricTime = System.currentTimeMillis();
        long totalDuration = endMetricTime - startMetricTime;

        //device-specific status reporting logic
        String connectionState;
        String diagnosticSummary;
        if (targetDevice.verifyHealthStatus()) {
            connectionState = "ONLINE";
            diagnosticSummary = String.format("Device checks passed. Resource baseline stable.");
        } else {
            connectionState = "ALERT_TRIGGERED";
            diagnosticSummary = String.format("Hardware threshold breach verified. CPU at %.1f%%, Memory at %.1f%%.",
                    targetDevice.getCpuUsage(), targetDevice.getMemoryUsage());
        }
        
        // Handle Network Timeouts
        if (totalDuration > 1600) {
            connectionState = "DEGRADED_TIMEOUT";
            diagnosticSummary += " Warning: Network link indicates severe data transmission latency.";
        }
        
        return new DeviceStatusReport(
                targetDevice.getId(),
                targetDevice.getIpAddress(),
                connectionState,
                totalDuration,
                diagnosticSummary
        );
    }
}


/*
How the Logic Works (Step-by-Step)

When a thread from the ExecutorService picks up this task, it executes the call() method in three steps:

*   **Step 1: Simulate Network Latency:** It picks a random number between 400ms and 2000ms. It forces the current thread to sleep for that long to simulate the time it takes to ping a real hardware device. It measures exactly how long this took (totalDuration).
*   **Step 2: Check Device Health:** It runs targetDevice.verifyHealthStatus(). If the device is healthy, it marks it ONLINE. If it fails (e.g., CPU/Memory thresholds are too high), it changes the status to ALERT_TRIGGERED and logs the bad metrics.
*   **Step 3: Handle Network Timeouts:** If the simulated network delay took too long (greater than 1600ms), it overrides the status to DEGRADED_TIMEOUT and appends a warning message.
*   **Final Output:** It packages all these results into a brand new DeviceStatusReport object and returns it to the application queue.
*/