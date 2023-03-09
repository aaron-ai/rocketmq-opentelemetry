package io.github.aaronai.example.server;

import io.github.aaronai.proto.GreetingGrpc;
import io.github.aaronai.proto.GreetingOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetingService extends GreetingGrpc.GreetingImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GreetingService.class);

    @Override
    public void sayHello(io.github.aaronai.proto.GreetingOuterClass.SayHelloRequest request,
                         io.grpc.stub.StreamObserver<io.github.aaronai.proto.GreetingOuterClass.SayHelloResponse> responseObserver) {
        logger.info("Received request={}", request);
        final GreetingOuterClass.SayHelloResponse response =
                GreetingOuterClass.SayHelloResponse.newBuilder().setResponseContent("This is an unary request").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
