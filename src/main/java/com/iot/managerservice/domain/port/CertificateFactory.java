package com.iot.managerservice.domain.port;

import com.iot.managerservice.domain.model.CertificateRequest;
import com.iot.managerservice.domain.model.GeneratedCertificate;

public interface CertificateFactory {
    GeneratedCertificate createCertificate(CertificateRequest request);
}