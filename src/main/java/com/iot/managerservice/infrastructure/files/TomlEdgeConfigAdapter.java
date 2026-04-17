package com.iot.managerservice.infrastructure.files;

import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.domain.port.EdgeConfigExporter;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.zip.*;


@Component
public class TomlEdgeConfigAdapter implements EdgeConfigExporter {

    private static final String CONFIG_BASE_DIR = "edge_configs";

    @Override
    public void generateConfiguration(Edge edge) {
        try {
            Path edgeDir = Paths.get(CONFIG_BASE_DIR, edge.edgeId());
            Files.createDirectories(edgeDir);

            // Generar system.toml
            String systemToml = """
                id_edge = "%s"
                host_server = "%s"
                host_port = "%d"
                cn = "%s"
                host_local = "%s"
                db_path = "%s"
                buffer_size = %d
                rust_log = "%s"
                """.formatted(edge.edgeId(),
                    edge.hostServer(),
                    edge.hostPort(),
                    edge.cn(),
                    edge.hostLocal(),
                    edge.dataBasePath(),
                    edge.bufferLength(),
                    edge.logLevel());
            Files.writeString(edgeDir.resolve("system.toml"), systemToml);

            // Generar protocol.toml
            String protocolToml = """
                    max_attempts = %d
                    frequency_phase = %d
                    frequency_safe_mode = %d
                    timeout_handshake = %d
                    timeout_phase = %d
                    timeout_safe_mode = %d
                    time_between_heartbeats_balance_mode = %d
                    time_between_heartbeats_normal = %d
                    time_between_heartbeats_safe_mode = %d
                """.formatted(edge.maxNumberHandshakeAttempts(),
                    edge.frequencyMessagesPhase(),
                    edge.frequencyMessagesSafeMode(),
                    edge.handshakeTimeLimit(),
                    edge.phaseTimeLimit(),
                    edge.safeModeTimeLimit(),
                    edge.heartbeatBalanceModeTime(),
                    edge.heartbeatNormalTime(),
                    edge.heartbeatSafeModeTime());
            Files.writeString(edgeDir.resolve("protocol.toml"), protocolToml);

        } catch (IOException e) {
            throw new RuntimeException("Error al crear archivos de configuración", e);
        }
    }

    @Override
    public byte[] getZipConfiguration(String edgeId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Path edgeDir = Paths.get(CONFIG_BASE_DIR, edgeId);

            // Añadir cada archivo al ZIP
            Files.list(edgeDir).forEach(path -> {
                try {
                    ZipEntry entry = new ZipEntry(path.getFileName().toString());
                    zos.putNextEntry(entry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) { /* log error */ }
            });
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el ZIP", e);
        }
        return baos.toByteArray();
    }

    @Override
    public void deleteConfiguration(String edgeId) {
        try {
            Path edgeDir = Paths.get(CONFIG_BASE_DIR, edgeId);
            if (Files.exists(edgeDir)) {
                // Borrar carpeta y contenidos
                Files.walk(edgeDir)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(p -> p.toFile().delete());
            }
        } catch (IOException e) { /* log warn */ }
    }
}