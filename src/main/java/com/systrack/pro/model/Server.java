package com.systrack.pro.model;

import com.systrack.pro.exception.DeviceOverloadException;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity 
@DiscriminatorValue("Server") //Matches the string identifier in the database 
public class Server extends Device {
    
    private int activeContainersCount;
    private String operatingSystem;
    public Server() {
        super();
    }

    // Primary Constructor
    public Server(String id, String type, String ipAddress, double cpuUsage, double memoryUsage, int latencyMs, 
                  int activeContainersCount, String operatingSystem) throws DeviceOverloadException {
        super(id, type, ipAddress, cpuUsage, memoryUsage, latencyMs);
        this.activeContainersCount = activeContainersCount;
        this.operatingSystem = operatingSystem;
    }

    // Creating a server configuration with default OS details
    public Server(String id, String ipAddress, double cpuUsage, double memoryUsage) throws DeviceOverloadException {
        this(id, "Server", ipAddress, cpuUsage, memoryUsage, 10, 0, "Linux (Ubuntu 22.04)");
    }
    
    //abstracted method created inside device class
    @Override
    public boolean verifyHealthStatus() {
        return this.getCpuUsage() < 90.0 && this.getMemoryUsage() < 95.0;
    }
    
    @Override
    public String getStatusReport() {
        String basicStatus = super.getStatusReport();
        return String.format("%s | OS: %s | Active Workloads: %d", 
                basicStatus, operatingSystem, activeContainersCount);
    }
    
    public int getActiveContainersCount() {
        return activeContainersCount;
    }

    public void setActiveContainersCount(int activeContainersCount) {
        this.activeContainersCount = activeContainersCount;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
}