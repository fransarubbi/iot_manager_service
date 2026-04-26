package com.iot.managerservice.domain.port;

import com.iot.managerservice.domain.model.Edge;

/**
 * Puerto de salida encargado de traducir y exportar la configuración de un dispositivo Edge.
 * <p>
 * Permite al dominio delegar la creación de los archivos de configuración
 * que posteriormente serán descargados en el Edge para que este pueda iniciar
 * su operación en la red.
 * </p>
 */
public interface EdgeConfigExporter {

    /**
     * Genera y escribe los artefactos de configuración estática basados en los parámetros del dominio.
     *
     * @param edge La entidad Edge que contiene los parámetros de red, tiempos de espera y políticas a exportar.
     */
    void generateConfiguration(Edge edge);

    /**
     * Empaqueta la configuración generada en un archivo comprimido listo para su distribución.
     *
     * @param edgeId Identificador único del dispositivo Edge cuya configuración se desea empaquetar.
     * @return Un arreglo de bytes que representa el contenido del archivo ZIP generado.
     */
    byte[] getZipConfiguration(String edgeId);

    /**
     * Elimina del sistema de archivos o del medio de almacenamiento temporal todos
     * los artefactos de configuración previamente generados para un Edge.
     *
     * @param edgeId Identificador único del dispositivo Edge cuyos artefactos serán eliminados.
     */
    void deleteConfiguration(String edgeId);
}