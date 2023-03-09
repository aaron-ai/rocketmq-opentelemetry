package io.github.aaronai.example.client;

import io.github.aaronai.proto.GreetingGrpc;
import io.github.aaronai.proto.GreetingOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetingClient {
    private static final Logger logger = LoggerFactory.getLogger(GreetingClient.class);

    public static void main(String[] args) {
        ManagedChannel channel = NettyChannelBuilder.forTarget("127.0.0.1:18848").usePlaintext().build();
        final GreetingGrpc.GreetingBlockingStub stub = GreetingGrpc.newBlockingStub(channel);
        GreetingOuterClass.SayHelloRequest request =
                GreetingOuterClass.SayHelloRequest.newBuilder().setRequestContent("request").build();
        final GreetingOuterClass.SayHelloResponse response = stub.sayHello(request);
        logger.info("received response={}", response);
    }
}
