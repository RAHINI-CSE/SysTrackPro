package com.systrack.pro.model;

import com.systrack.pro.exception.DeviceOverloadException;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity 
@DiscriminatorValue("NetworkSwitch") 
public class NetworkSwitch extends Device {

    private int totalPorts;
    private int activePortsCount;
    
    public NetworkSwitch() {
        super();
    }

    // Primary Constructor
    public NetworkSwitch(String id, String type, String ipAddress, double cpuUsage, double memoryUsage, int latencyMs, 
                         int totalPorts, int activePortsCount) throws DeviceOverloadException {
        super(id, type, ipAddress, cpuUsage, memoryUsage, latencyMs);
        this.totalPorts = totalPorts;
        this.activePortsCount = activePortsCount;
    }

    // Method Overloading
    public NetworkSwitch(String id, String ipAddress, double cpuUsage, double memoryUsage) throws DeviceOverloadException {
        this(id, "NetworkSwitch", ipAddress, cpuUsage, memoryUsage, 2, 24, 12);
    }
    
    @Override
    public boolean verifyHealthStatus() {
    	double portUtilization = ((double) activePortsCount / totalPorts) * 100;
        return this.getLatencyMs() <= 50 && portUtilization < 100.0;
    }
    
    @Override
    public String getStatusReport() {
        String basicStatus = super.getStatusReport();
        return String.format("%s | Port Capacity: %d/%d (%.1f%% Utilized)", 
                basicStatus, activePortsCount, totalPorts, (((double) activePortsCount / totalPorts) * 100));
    }
    
    public int getTotalPorts() {
        return totalPorts;
    }

    public void setTotalPorts(int totalPorts) {
        this.totalPorts = totalPorts;
    }

    public int getActivePortsCount() {
        return activePortsCount;
    }

    public void setActivePortsCount(int activePortsCount) {
        this.activePortsCount = activePortsCount;
    }
}


/*
[Your Code Calls] ──> Overloaded Constructor (4 parameters)
│
▼  (Hits 'this(...)')
Primary Constructor (8 parameters)
│
▼  (Hits 'super(...)')
Base/Parent Class Constructor
│
▼  [Executes & Finishes]
Primary Constructor finishes setting ports
│
▼  [Executes & Finishes]
Overloaded Constructor finishes
*/