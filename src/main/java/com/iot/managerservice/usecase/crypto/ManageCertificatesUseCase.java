package com.iot.managerservice.usecase.crypto;

import com.iot.managerservice.domain.model.CertificateData;
import com.iot.managerservice.domain.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ManageCertificatesUseCase {

    private final CertificateRepository repository;

    public ManageCertificatesUseCase(CertificateRepository repository) {
        this.repository = repository;
    }

    public List<CertificateData> getAll() {
        return repository.findAll();
    }

    public void revoke(String id) {
        // Auditoría: Marcamos como revocado sin borrar físicamente
        repository.updateStatus(id, "REVOKED");
    }
}
