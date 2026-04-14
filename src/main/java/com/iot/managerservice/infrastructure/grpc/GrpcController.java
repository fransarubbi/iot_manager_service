package com.iot.managerservice.infrastructure.grpc;

import com.iot.managerservice.application.grpc.generated.*;
import com.iot.managerservice.usecase.settings.ManageSettingsUseCase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService   // Levanta el servidor gRPC en el puerto 9090
public class GrpcController extends ManagerServiceGrpc.ManagerServiceImplBase {

    private final ManageSettingsUseCase manageSettingsUseCase;

    public GrpcController(ManageSettingsUseCase manageSettingsUseCase) {
        this.manageSettingsUseCase = manageSettingsUseCase;
    }

    @Override
    public StreamObserver<FromManager> connectStream(StreamObserver<ToManager> responseObserver) {

        // Retorna un "Observer" que reacciona cada vez que llega un paquete
        return new StreamObserver<FromManager>() {

            @Override
            public void onNext(FromManager request) {

                String hubId = request.getEdgeId();

                if (request.hasSettings()) {
                    Settings settingsPayload = request.getSettings();

                    Long messageId = settingsPayload.getMetadata().getTimestamp();
                    manageSettingsUseCase.execute(hubId, messageId, settingsPayload);
                }

                // Aquí agregar más "if" para request.hasNetwork(), request.hasDeleteHub(), etc.
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error en la conexión gRPC con el Router: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("El Router cerró la conexión gRPC.");
                responseObserver.onCompleted();
            }
        };
    }
}
