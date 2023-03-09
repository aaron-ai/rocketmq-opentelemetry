/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.aaronai.example.client;

import io.github.aaronai.proto.GreetingGrpc;
import io.github.aaronai.proto.GreetingOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class GreetingClient {
    private static final Logger logger = LoggerFactory.getLogger(GreetingClient.class);

    public static void main(String[] args) {
        ManagedChannel channel = NettyChannelBuilder.forTarget("127.0.0.1:18848").usePlaintext().build();
        final GreetingGrpc.GreetingBlockingStub stub = GreetingGrpc.newBlockingStub(channel);
        GreetingOuterClass.SayHelloRequest request =
                GreetingOuterClass.SayHelloRequest.newBuilder().setRequestContent("request").build();
        final GreetingOuterClass.SayHelloResponse response = stub.sayHello(request);
        logger.info("received response={}", response);

        // Define your message body.
        byte[] body = "This is a normal message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
    }
}
