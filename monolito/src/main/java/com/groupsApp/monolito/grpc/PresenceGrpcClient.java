package com.groupsapp.monolito.grpc;

import com.groupsapp.presence.grpc.PresenceRequest;
import com.groupsapp.presence.grpc.PresenceResponse;
import com.groupsapp.presence.grpc.PresenceServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cliente gRPC del monolito para hablar con el presence-service.
 *
 * Los servicios del monolito inyectan esta clase.
 * Ella encapsula los detalles de la comunicación gRPC.
 */
@Component
public class PresenceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(PresenceGrpcClient.class);

    // El nombre "presence-service" debe coincidir con la config en application.properties
    @GrpcClient("presence-service")
    private PresenceServiceGrpc.PresenceServiceBlockingStub presenceStub;

    public PresenceResponse setOnline(Long userId) {
        PresenceRequest req = buildRequest(userId);
        log.info("Calling presence-service setUserOnline for userId={}", userId);
        return presenceStub.setUserOnline(req);
    }

    public PresenceResponse setOffline(Long userId) {
        PresenceRequest req = buildRequest(userId);
        log.info("Calling presence-service setUserOffline for userId={}", userId);
        return presenceStub.setUserOffline(req);
    }

    public PresenceResponse checkPresence(Long userId) {
        PresenceRequest req = buildRequest(userId);
        return presenceStub.checkUserPresence(req);
    }

    private PresenceRequest buildRequest(Long userId) {
        return PresenceRequest.newBuilder().setUserId(userId).build();
    }
}