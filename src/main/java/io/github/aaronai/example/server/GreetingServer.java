package io.github.aaronai.example.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GreetingServer {
    private static final Logger logger = LoggerFactory.getLogger(GreetingServer.class);
    private final Server server;
    private final int port;

    public GreetingServer(int port) {
        GreetingService service = new GreetingService();
        this.server = ServerBuilder.forPort(port).addService(service).build();
        this.port = port;
    }

    private void start() throws IOException {
        logger.info("Server: starting...");
        server.start();
        logger.info("Server: started on listen on port {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Server: stopping ...");
            GreetingServer.this.stop();
            logger.info("Server: stopped.");
        }));
    }

    private void stop() {
        server.shutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (null != server) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final GreetingServer server = new GreetingServer(18848);
        server.start();
        server.blockUntilShutdown();
    }
}
