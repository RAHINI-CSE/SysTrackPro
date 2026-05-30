package com.systrack.pro.concurrency;

public class DeviceStatusReport {

    private final String deviceId;
    private final String ipAddress;
    private final String pingStatus; 
    private final long processingDurationMs;
    private final String diagnosticNotes;

    public DeviceStatusReport(String deviceId, String ipAddress, String pingStatus, 
                              long processingDurationMs, String diagnosticNotes) {
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.pingStatus = pingStatus;
        this.processingDurationMs = processingDurationMs;
        this.diagnosticNotes = diagnosticNotes;
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public long getProcessingDurationMs() {
        return processingDurationMs;
    }

    public String getDiagnosticNotes() {
        return diagnosticNotes;
    }
    
    @Override
    public String toString() {
        return String.format("[TELEMETRY] Device %s (%s) -> Ping: %s | Overhead: %dms | Notes: %s",
                deviceId, ipAddress, pingStatus, processingDurationMs, diagnosticNotes);
    }
}