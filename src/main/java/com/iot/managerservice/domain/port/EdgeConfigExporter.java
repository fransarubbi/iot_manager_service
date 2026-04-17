package com.iot.managerservice.domain.port;

import com.iot.managerservice.domain.model.Edge;

public interface EdgeConfigExporter {
    void generateConfiguration(Edge edge);
    byte[] getZipConfiguration(String edgeId);
    void deleteConfiguration(String edgeId);
}