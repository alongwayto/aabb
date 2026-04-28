package cn.coderstory.springboot.device.service;

import java.util.Map;

public interface DeviceStatusService {
    void updateDeviceStatus(String deviceNo, Map<String, Object> status);
    Map<String, Object> getDeviceStatus(String deviceNo);
    Map<String, Object> getAllDeviceStatusSnapshot();
    void recordHeartbeat(String deviceNo);
    boolean isDeviceOnline(String deviceNo);
}
