package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.CertificateData;
import java.util.List;


public interface CertificateRepository {
    void save(CertificateData certificate);
    List<CertificateData> findAll();
    void updateStatus(String id, String status);
}
