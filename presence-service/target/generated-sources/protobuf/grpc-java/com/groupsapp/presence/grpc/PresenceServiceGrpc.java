package com.groupsapp.presence.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * ═══════════════════════════════════════════════════════════════
 *   Servicio de Presencia (quién está online)
 *   Comunicación interna entre monolito (cliente) y presence-service (servidor).
 * ═══════════════════════════════════════════════════════════════
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: presence.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PresenceServiceGrpc {

  private PresenceServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.groupsapp.presence.PresenceService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest,
      com.groupsapp.presence.grpc.PresenceResponse> getSetUserOnlineMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetUserOnline",
      requestType = com.groupsapp.presence.grpc.PresenceRequest.class,
      responseType = com.groupsapp.presence.grpc.PresenceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest,
      com.groupsapp.presence.grpc.PresenceResponse> getSetUserOnlineMethod() {
    io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest, com.groupsapp.presence.grpc.PresenceResponse> getSetUserOnlineMethod;
    if ((getSetUserOnlineMethod = PresenceServiceGrpc.getSetUserOnlineMethod) == null) {
      synchronized (PresenceServiceGrpc.class) {
        if ((getSetUserOnlineMethod = PresenceServiceGrpc.getSetUserOnlineMethod) == null) {
          PresenceServiceGrpc.getSetUserOnlineMethod = getSetUserOnlineMethod =
              io.grpc.MethodDescriptor.<com.groupsapp.presence.grpc.PresenceRequest, com.groupsapp.presence.grpc.PresenceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetUserOnline"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.groupsapp.presence.grpc.PresenceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.groupsapp.presence.grpc.PresenceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PresenceServiceMethodDescriptorSupplier("SetUserOnline"))
              .build();
        }
      }
    }
    return getSetUserOnlineMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest,
      com.groupsapp.presence.grpc.PresenceResponse> getSetUserOfflineMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetUserOffline",
      requestType = com.groupsapp.presence.grpc.PresenceRequest.class,
      responseType = com.groupsapp.presence.grpc.PresenceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest,
      com.groupsapp.presence.grpc.PresenceResponse> getSetUserOfflineMethod() {
    io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest, com.groupsapp.presence.grpc.PresenceResponse> getSetUserOfflineMethod;
    if ((getSetUserOfflineMethod = PresenceServiceGrpc.getSetUserOfflineMethod) == null) {
      synchronized (PresenceServiceGrpc.class) {
        if ((getSetUserOfflineMethod = PresenceServiceGrpc.getSetUserOfflineMethod) == null) {
          PresenceServiceGrpc.getSetUserOfflineMethod = getSetUserOfflineMethod =
              io.grpc.MethodDescriptor.<com.groupsapp.presence.grpc.PresenceRequest, com.groupsapp.presence.grpc.PresenceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetUserOffline"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.groupsapp.presence.grpc.PresenceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.groupsapp.presence.grpc.PresenceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PresenceServiceMethodDescriptorSupplier("SetUserOffline"))
              .build();
        }
      }
    }
    return getSetUserOfflineMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest,
      com.groupsapp.presence.grpc.PresenceResponse> getCheckUserPresenceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckUserPresence",
      requestType = com.groupsapp.presence.grpc.PresenceRequest.class,
      responseType = com.groupsapp.presence.grpc.PresenceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest,
      com.groupsapp.presence.grpc.PresenceResponse> getCheckUserPresenceMethod() {
    io.grpc.MethodDescriptor<com.groupsapp.presence.grpc.PresenceRequest, com.groupsapp.presence.grpc.PresenceResponse> getCheckUserPresenceMethod;
    if ((getCheckUserPresenceMethod = PresenceServiceGrpc.getCheckUserPresenceMethod) == null) {
      synchronized (PresenceServiceGrpc.class) {
        if ((getCheckUserPresenceMethod = PresenceServiceGrpc.getCheckUserPresenceMethod) == null) {
          PresenceServiceGrpc.getCheckUserPresenceMethod = getCheckUserPresenceMethod =
              io.grpc.MethodDescriptor.<com.groupsapp.presence.grpc.PresenceRequest, com.groupsapp.presence.grpc.PresenceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckUserPresence"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.groupsapp.presence.grpc.PresenceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.groupsapp.presence.grpc.PresenceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PresenceServiceMethodDescriptorSupplier("CheckUserPresence"))
              .build();
        }
      }
    }
    return getCheckUserPresenceMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PresenceServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PresenceServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PresenceServiceStub>() {
        @java.lang.Override
        public PresenceServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PresenceServiceStub(channel, callOptions);
        }
      };
    return PresenceServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PresenceServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PresenceServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PresenceServiceBlockingStub>() {
        @java.lang.Override
        public PresenceServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PresenceServiceBlockingStub(channel, callOptions);
        }
      };
    return PresenceServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PresenceServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PresenceServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PresenceServiceFutureStub>() {
        @java.lang.Override
        public PresenceServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PresenceServiceFutureStub(channel, callOptions);
        }
      };
    return PresenceServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * ═══════════════════════════════════════════════════════════════
   *   Servicio de Presencia (quién está online)
   *   Comunicación interna entre monolito (cliente) y presence-service (servidor).
   * ═══════════════════════════════════════════════════════════════
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Marca un usuario como online. Lo llama el monolito en login.
     * </pre>
     */
    default void setUserOnline(com.groupsapp.presence.grpc.PresenceRequest request,
        io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSetUserOnlineMethod(), responseObserver);
    }

    /**
     * <pre>
     * Marca un usuario como offline. Lo llama el monolito en logout.
     * </pre>
     */
    default void setUserOffline(com.groupsapp.presence.grpc.PresenceRequest request,
        io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSetUserOfflineMethod(), responseObserver);
    }

    /**
     * <pre>
     * Consulta el estado actual de un usuario. Lo llama el monolito
     * antes de crear un MessageStatus para decidir si deliveredAt va ya o queda pendiente.
     * </pre>
     */
    default void checkUserPresence(com.groupsapp.presence.grpc.PresenceRequest request,
        io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckUserPresenceMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PresenceService.
   * <pre>
   * ═══════════════════════════════════════════════════════════════
   *   Servicio de Presencia (quién está online)
   *   Comunicación interna entre monolito (cliente) y presence-service (servidor).
   * ═══════════════════════════════════════════════════════════════
   * </pre>
   */
  public static abstract class PresenceServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PresenceServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PresenceService.
   * <pre>
   * ═══════════════════════════════════════════════════════════════
   *   Servicio de Presencia (quién está online)
   *   Comunicación interna entre monolito (cliente) y presence-service (servidor).
   * ═══════════════════════════════════════════════════════════════
   * </pre>
   */
  public static final class PresenceServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PresenceServiceStub> {
    private PresenceServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PresenceServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PresenceServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Marca un usuario como online. Lo llama el monolito en login.
     * </pre>
     */
    public void setUserOnline(com.groupsapp.presence.grpc.PresenceRequest request,
        io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSetUserOnlineMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Marca un usuario como offline. Lo llama el monolito en logout.
     * </pre>
     */
    public void setUserOffline(com.groupsapp.presence.grpc.PresenceRequest request,
        io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSetUserOfflineMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Consulta el estado actual de un usuario. Lo llama el monolito
     * antes de crear un MessageStatus para decidir si deliveredAt va ya o queda pendiente.
     * </pre>
     */
    public void checkUserPresence(com.groupsapp.presence.grpc.PresenceRequest request,
        io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckUserPresenceMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PresenceService.
   * <pre>
   * ═══════════════════════════════════════════════════════════════
   *   Servicio de Presencia (quién está online)
   *   Comunicación interna entre monolito (cliente) y presence-service (servidor).
   * ═══════════════════════════════════════════════════════════════
   * </pre>
   */
  public static final class PresenceServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PresenceServiceBlockingStub> {
    private PresenceServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PresenceServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PresenceServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Marca un usuario como online. Lo llama el monolito en login.
     * </pre>
     */
    public com.groupsapp.presence.grpc.PresenceResponse setUserOnline(com.groupsapp.presence.grpc.PresenceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSetUserOnlineMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Marca un usuario como offline. Lo llama el monolito en logout.
     * </pre>
     */
    public com.groupsapp.presence.grpc.PresenceResponse setUserOffline(com.groupsapp.presence.grpc.PresenceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSetUserOfflineMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Consulta el estado actual de un usuario. Lo llama el monolito
     * antes de crear un MessageStatus para decidir si deliveredAt va ya o queda pendiente.
     * </pre>
     */
    public com.groupsapp.presence.grpc.PresenceResponse checkUserPresence(com.groupsapp.presence.grpc.PresenceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckUserPresenceMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PresenceService.
   * <pre>
   * ═══════════════════════════════════════════════════════════════
   *   Servicio de Presencia (quién está online)
   *   Comunicación interna entre monolito (cliente) y presence-service (servidor).
   * ═══════════════════════════════════════════════════════════════
   * </pre>
   */
  public static final class PresenceServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PresenceServiceFutureStub> {
    private PresenceServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PresenceServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PresenceServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Marca un usuario como online. Lo llama el monolito en login.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.groupsapp.presence.grpc.PresenceResponse> setUserOnline(
        com.groupsapp.presence.grpc.PresenceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSetUserOnlineMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Marca un usuario como offline. Lo llama el monolito en logout.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.groupsapp.presence.grpc.PresenceResponse> setUserOffline(
        com.groupsapp.presence.grpc.PresenceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSetUserOfflineMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Consulta el estado actual de un usuario. Lo llama el monolito
     * antes de crear un MessageStatus para decidir si deliveredAt va ya o queda pendiente.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.groupsapp.presence.grpc.PresenceResponse> checkUserPresence(
        com.groupsapp.presence.grpc.PresenceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckUserPresenceMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SET_USER_ONLINE = 0;
  private static final int METHODID_SET_USER_OFFLINE = 1;
  private static final int METHODID_CHECK_USER_PRESENCE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SET_USER_ONLINE:
          serviceImpl.setUserOnline((com.groupsapp.presence.grpc.PresenceRequest) request,
              (io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse>) responseObserver);
          break;
        case METHODID_SET_USER_OFFLINE:
          serviceImpl.setUserOffline((com.groupsapp.presence.grpc.PresenceRequest) request,
              (io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse>) responseObserver);
          break;
        case METHODID_CHECK_USER_PRESENCE:
          serviceImpl.checkUserPresence((com.groupsapp.presence.grpc.PresenceRequest) request,
              (io.grpc.stub.StreamObserver<com.groupsapp.presence.grpc.PresenceResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSetUserOnlineMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.groupsapp.presence.grpc.PresenceRequest,
              com.groupsapp.presence.grpc.PresenceResponse>(
                service, METHODID_SET_USER_ONLINE)))
        .addMethod(
          getSetUserOfflineMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.groupsapp.presence.grpc.PresenceRequest,
              com.groupsapp.presence.grpc.PresenceResponse>(
                service, METHODID_SET_USER_OFFLINE)))
        .addMethod(
          getCheckUserPresenceMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.groupsapp.presence.grpc.PresenceRequest,
              com.groupsapp.presence.grpc.PresenceResponse>(
                service, METHODID_CHECK_USER_PRESENCE)))
        .build();
  }

  private static abstract class PresenceServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PresenceServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.groupsapp.presence.grpc.PresenceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PresenceService");
    }
  }

  private static final class PresenceServiceFileDescriptorSupplier
      extends PresenceServiceBaseDescriptorSupplier {
    PresenceServiceFileDescriptorSupplier() {}
  }

  private static final class PresenceServiceMethodDescriptorSupplier
      extends PresenceServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PresenceServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PresenceServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PresenceServiceFileDescriptorSupplier())
              .addMethod(getSetUserOnlineMethod())
              .addMethod(getSetUserOfflineMethod())
              .addMethod(getCheckUserPresenceMethod())
              .build();
        }
      }
    }
    return result;
  }
}
