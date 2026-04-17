package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.CertificateData;
import com.iot.managerservice.domain.model.CertificateRequest;
import com.iot.managerservice.usecase.crypto.GenerateAndZipCertificateUseCase;
import com.iot.managerservice.usecase.crypto.ManageCertificatesUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/certificates")
public class CertificateRestController {

    private final GenerateAndZipCertificateUseCase generateUseCase;
    private final ManageCertificatesUseCase manageUseCase;

    public CertificateRestController(GenerateAndZipCertificateUseCase generateUseCase, ManageCertificatesUseCase manageUseCase) {
        this.generateUseCase = generateUseCase;
        this.manageUseCase = manageUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CertificateData>> list() {
        return ResponseEntity.ok(manageUseCase.getAll());
    }

    @PatchMapping("/{id}/revoke")
    public ResponseEntity<Void> revoke(@PathVariable String id) {
        manageUseCase.revoke(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/generate", produces = "application/zip")
    public ResponseEntity<byte[]> generateCertificate(@RequestBody CertificateRequest request) {

        byte[] zipData = generateUseCase.execute(request);

        String filename = request.commonName() + "_certs.zip";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipData);
    }
}
