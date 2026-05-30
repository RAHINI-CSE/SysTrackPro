package com.systrack.pro.repository;

import com.systrack.pro.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
	
    List<Device> findByMonitoringStatus(String monitoringStatus);
    
    Device findByIpAddress(String ipAddress);
}