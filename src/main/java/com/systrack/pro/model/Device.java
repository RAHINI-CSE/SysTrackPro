package com.systrack.pro.model;

import com.systrack.pro.exception.DeviceOverloadException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@NoArgsConstructor 
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Device implements Comparable<Device> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalDbId;
    private String id; 
    private String type;   
    private String ipAddress;
    private double cpuUsage;
    private double memoryUsage;
    private int latencyMs;
    private String monitoringStatus; 
    
    public Device(String id, String type, String ipAddress, double cpuUsage, double memoryUsage, int latencyMs) 
            throws DeviceOverloadException {
        this.id = id;
        this.type = type;
        this.ipAddress = ipAddress;
        this.cpuUsage = (cpuUsage < 0) ? 0.0 : cpuUsage;
        this.memoryUsage = (memoryUsage < 0) ? 0.0 : memoryUsage;
        this.latencyMs = (latencyMs < 0) ? 0 : latencyMs;        
        this.monitoringStatus = "UNCHECKED";
    }
    // Abstract methods 
    public abstract boolean verifyHealthStatus();

    public String getStatusReport() {
        return String.format("[%s] IP: %s | CPU: %.1f%% | Mem: %.1f%% | Latency: %dms -> Status: %s",
                id, ipAddress, cpuUsage, memoryUsage, latencyMs, monitoringStatus);
    }
    
    @Override
    public int compareTo(Device other) {
        return Double.compare(this.cpuUsage, other.cpuUsage);
    }
}