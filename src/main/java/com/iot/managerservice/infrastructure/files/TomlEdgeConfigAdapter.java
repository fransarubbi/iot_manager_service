package com.iot.managerservice.infrastructure.files;

import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.domain.port.EdgeConfigExporter;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

/**
 * Adaptador de infraestructura (Salida) responsable de la exportación de configuraciones estáticas.
 * <p>
 * Implementa el puerto {@link EdgeConfigExporter}. Su función principal es traducir los parámetros
 * operativos de un dispositivo Edge (dictados por la capa de dominio) hacia archivos físicos
 * en formato TOML (generando {@code system.toml} y {@code protocol.toml}).
 * </p>
 */
@Component
public class TomlEdgeConfigAdapter implements EdgeConfigExporter {

    private static final String CONFIG_BASE_DIR = "edge_configs";

    /**
     * Escribe la configuración en disco creando una estructura de directorios aislada para el Edge.
     * Transforma los atributos del modelo de dominio a formato TOML.
     *
     * @param edge Objeto de dominio con la parametrización de red y comportamiento.
     * @throws RuntimeException Si ocurren problemas de permisos o escritura en el disco.
     */
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

    /**
     * Comprime la carpeta de configuración generada previamente en un archivo ZIP.
     * Este artefacto es el que finalmente se descargará.
     *
     * @param edgeId Identificador único del dispositivo Edge.
     * @return Arreglo de bytes que representa el contenido del archivo ZIP.
     * @throws RuntimeException Si ocurre un error al leer los archivos o construir el ZIP.
     */
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

    /**
     * Elimina recursivamente el directorio temporal de configuración de un Edge
     * para liberar espacio en el sistema de archivos tras una descarga o eliminación exitosa.
     *
     * @param edgeId Identificador lógico del Edge cuyos archivos temporales serán borrados.
     */
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