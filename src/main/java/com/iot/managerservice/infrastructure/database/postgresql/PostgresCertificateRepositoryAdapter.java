package com.iot.managerservice.infrastructure.database.postgresql;

import com.iot.managerservice.domain.model.CertificateData;
import com.iot.managerservice.domain.repository.CertificateRepository;
import com.iot.managerservice.infrastructure.database.postgresql.entities.CertificateEntity;
import com.iot.managerservice.infrastructure.database.postgresql.repositories.JpaCertificateRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class PostgresCertificateRepositoryAdapter implements CertificateRepository {

    private final JpaCertificateRepository jpaRepository;

    public PostgresCertificateRepositoryAdapter(JpaCertificateRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(CertificateData c) {
        CertificateEntity entity = new CertificateEntity(
                c.id(), c.displayName(), c.deviceType(), c.status(),
                c.emissionDate(), c.expirationDate(), c.privateKeyPem(), c.certificatePem()
        );
        jpaRepository.save(entity);
    }

    @Override
    public List<CertificateData> findAll() {
        return jpaRepository.findByStatus("VALID").stream()
                .map(e -> new CertificateData(e.getId(), e.getDisplayName(),
                        e.getDeviceType(), e.getStatus(), e.getEmissionDate(),
                        e.getExpirationDate(), e.getPrivateKeyPem(), e.getCertificatePem()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String id, String status) {
        jpaRepository.findById(id).ifPresent(e -> {
            e.setStatus(status);
            jpaRepository.save(e);
        });
    }
}
