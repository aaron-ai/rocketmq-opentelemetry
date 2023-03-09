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

package io.github.aaronai;

import io.github.aaronai.example.client.GreetingClient;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RocketMqClients implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(GreetingClient.class);
    private static final String NORMAL_TOPIC = "normalTopic";
    private static final String FIFO_TOPIC = "fifoTopic";
    private static final String DELAY_TOPIC = "delayTopic";
    private static final String TRANSACTION_TOPIC = "transactionTopic";

    private final Producer producer;
    private final PushConsumer pushConsumer;

    public static final RocketMqClients INSTANCE;

    static {
        try {
            INSTANCE = new RocketMqClients();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private RocketMqClients() throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        String accessKey = "yourAccessKey";
        String secretKey = "yourSecretKey";
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(accessKey, secretKey);
        String endpoints = "foobar.com:8080";
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();

        String consumerGroup = "yourConsumerGroup";
        String tag = "yourMessageTagA";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        Map<String, FilterExpression> subscriptionExpressions = new HashMap<>();
        subscriptionExpressions.put(NORMAL_TOPIC, filterExpression);
        subscriptionExpressions.put(FIFO_TOPIC, filterExpression);
        subscriptionExpressions.put(DELAY_TOPIC, filterExpression);
        subscriptionExpressions.put(TRANSACTION_TOPIC, filterExpression);

        pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the consumer group name.
                .setConsumerGroup(consumerGroup)
                // Set the subscription for the consumer.
                .setSubscriptionExpressions(subscriptionExpressions)
                .setMessageListener(messageView -> {
                    // Handle the received message and return consume result.
                    logger.info("Consume message={}", messageView);
                    return ConsumeResult.SUCCESS;
                })
                // May throw {@link ClientException} if the push consumer is not initialized.
                .build();

        // In most case, you don't need to create too many producers, singleton pattern is recommended.
        producer = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the topic name(s), which is optional but recommended. It makes producer could prefetch the
                // topic route before message publishing.
                .setTopics(NORMAL_TOPIC, FIFO_TOPIC, DELAY_TOPIC, TRANSACTION_TOPIC)
                // May throw {@link ClientException} if the producer is not initialized.
                .build();
    }

    @Override
    public void close() throws IOException {
        producer.close();
        pushConsumer.close();
    }
}
