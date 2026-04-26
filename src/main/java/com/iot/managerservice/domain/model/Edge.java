package com.iot.managerservice.domain.model;

/**
 * Entidad principal de dominio que consolida la configuración y el estado esperado de un nodo Edge.
 * <p>
 * Agrupa toda la parametrización de red operativa, los umbrales de temporización,
 * y las políticas de tolerancia a fallos. Estos datos son los que el Manager compilará
 * (en un archivo TOML) para ser inyectados en el dispositivo.
 * </p>
 *
 * @param edgeId                     Identificador lógico y único asignado al dispositivo Edge.
 * @param name                       Nombre o etiqueta humana para propósitos de monitoreo.
 * @param location                   Descripción del entorno físico o lógico del despliegue (ej. "Nave Industrial B").
 * @param cn                         Common Name utilizado para correlacionar al nodo con su certificado mTLS.
 * @param hostServer                 Dirección IP o DNS del Servidor al cual el Edge debe conectarse.
 * @param hostPort                   Puerto de escucha (TCP/UDP) del servidor destino.
 * @param hostLocal                  Dirección IP pre-asignada o interfaz local del propio Edge.
 * @param dataBasePath               Ruta del sistema de archivos local (SD Card/Flash) usada para la persistencia offline de métricas.
 * @param bufferLength               Capacidad máxima en memoria asignada a la cola de mensajes antes de un posible desbordamiento.
 * @param logLevel                   Nivel de verbosidad operativa configurado para el registro local ("DEBUG", "INFO", "WARN").
 * @param maxNumberHandshakeAttempts Límite máximo de intentos fallidos antes de abortar un inicio de conexión o pasar a un modo de fallo.
 * @param frequencyMessagesPhase     Intervalo de envío de telemetría cuando el sistema opera en modo de fase normal.
 * @param frequencyMessagesSafeMode  Intervalo mitigado de envíos que se activa bajo el modo de bajo consumo o red inestable.
 * @param handshakeTimeLimit         Límite de tiempo máximo permitido para concluir el establecimiento de la conexión.
 * @param phaseTimeLimit             Tiempo máximo para considerar estancada o bloqueada una rutina principal.
 * @param safeModeTimeLimit          Tiempo máximo de vida útil operando bajo el modo de contingencia ("Safe Mode").
 * @param heartbeatBalanceModeTime   Frecuencia del pulso de vida emitido por el Edge durante un balanceo temporal de red.
 * @param heartbeatNormalTime        Frecuencia estándar del pulso de vida indicando que el dispositivo goza de buena salud en la red.
 * @param heartbeatSafeModeTime      Frecuencia del pulso de supervivencia emitido durante condiciones operativas restrictivas.
 */
public record Edge(
        String edgeId,
        String name,
        String location,
        String cn,
        String hostServer,
        Integer hostPort,
        String hostLocal,
        String dataBasePath,
        Integer bufferLength,
        String logLevel,
        Integer maxNumberHandshakeAttempts,
        Integer frequencyMessagesPhase,
        Integer frequencyMessagesSafeMode,
        Integer handshakeTimeLimit,
        Integer phaseTimeLimit,
        Integer safeModeTimeLimit,
        Integer heartbeatBalanceModeTime,
        Integer heartbeatNormalTime,
        Integer heartbeatSafeModeTime
) {}