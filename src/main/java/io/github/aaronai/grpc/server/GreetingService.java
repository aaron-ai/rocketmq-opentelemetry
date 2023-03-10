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

package io.github.aaronai.grpc.server;

import io.github.aaronai.mq.RocketMqClients;
import io.github.aaronai.proto.GreetingGrpc;
import io.github.aaronai.proto.GreetingOuterClass;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetingService extends GreetingGrpc.GreetingImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GreetingService.class);

    @Override
    public void sayHello(io.github.aaronai.proto.GreetingOuterClass.SayHelloRequest request,
                         io.grpc.stub.StreamObserver<io.github.aaronai.proto.GreetingOuterClass.SayHelloResponse> responseObserver) {
        logger.info("Received request={}", request);
        try {
            Producer producer = RocketMqClients.CreateProducer();
            SendReceipt sendReceipt = RocketMqClients.sendNormalMessage(producer);
            logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            logger.error("Failed to send normal message", e);
        }
        final GreetingOuterClass.SayHelloResponse response =
                GreetingOuterClass.SayHelloResponse.newBuilder().setResponseContent("This is an unary request").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
