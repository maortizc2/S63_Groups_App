package com.groupsapp.presence.grpc;

import com.groupsapp.presence.model.UserPresence;
import com.groupsapp.presence.service.PresenceBusinessService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter gRPC del presence-service.
 *
 * Extiende la clase base generada por protobuf (PresenceServiceGrpc.PresenceServiceImplBase).
 * Su único trabajo es traducir entre el mundo gRPC (PresenceRequest / PresenceResponse)
 * y el mundo Java puro (PresenceBusinessService).
 *
 * No tiene lógica de negocio propia. Eso es intencional.
 */
@GrpcService
public class PresenceServiceGrpcImpl extends PresenceServiceGrpc.PresenceServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(PresenceServiceGrpcImpl.class);

    private final PresenceBusinessService businessService;

    public PresenceServiceGrpcImpl(PresenceBusinessService businessService) {
        this.businessService = businessService;
    }

    @Override
    public void setUserOnline(PresenceRequest request,
                              StreamObserver<PresenceResponse> responseObserver) {
        log.info("gRPC setUserOnline called for userId={}", request.getUserId());
        UserPresence presence = businessService.markOnline(request.getUserId());
        responseObserver.onNext(toResponse(presence));
        responseObserver.onCompleted();
    }

    @Override
    public void setUserOffline(PresenceRequest request,
                               StreamObserver<PresenceResponse> responseObserver) {
        log.info("gRPC setUserOffline called for userId={}", request.getUserId());
        UserPresence presence = businessService.markOffline(request.getUserId());
        responseObserver.onNext(toResponse(presence));
        responseObserver.onCompleted();
    }

    @Override
    public void checkUserPresence(PresenceRequest request,
                                  StreamObserver<PresenceResponse> responseObserver) {
        log.info("gRPC checkUserPresence called for userId={}", request.getUserId());
        UserPresence presence = businessService.getPresence(request.getUserId());
        responseObserver.onNext(toResponse(presence));
        responseObserver.onCompleted();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Convierte la entidad JPA al DTO protobuf.
     * lastSeen puede ser null (usuario nunca visto) → enviamos 0.
     */
    private PresenceResponse toResponse(UserPresence presence) {
        long lastSeenMillis = presence.getLastSeen() != null
                ? presence.getLastSeen().toEpochMilli()
                : 0L;

        return PresenceResponse.newBuilder()
                .setUserId(presence.getUserId())
                .setOnline(presence.isOnline())
                .setLastSeenUnixMillis(lastSeenMillis)
                .build();
    }
}