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

package io.github.aaronai.mq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RocketMqClients {
    private static final ClientServiceProvider provider = ClientServiceProvider.loadService();

    private static final String ACCESS_KEY = "yourAccessKey";
    private static final String SECRET_KEY = "yourSecretKey";
    private static final String ENDPOINTS = "rmq-cn-5yd34ft5y02.cn-hangzhou.rmq.aliyuncs.com:8080";

    private static final String NORMAL_TOPIC = "lingchu_normal_topic";
    private static final String FIFO_TOPIC = "lingchu_fifo_topic";
    private static final String DELAY_TOPIC = "lingchu_delay_topic";
    private static final String TRANSACTION_TOPIC = "lingchu_transaction_topic";

    private static final String MESSAGE_TAG = "yourMessageTagA";

    public static Producer CreateProducer() throws ClientException {
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(ACCESS_KEY, SECRET_KEY);
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(ENDPOINTS)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();
        // In most case, you don't need to create too many producers, singleton pattern is recommended.
        return provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the topic name(s), which is optional but recommended. It makes producer could prefetch the
                // topic route before message publishing.
                .setTopics(NORMAL_TOPIC, FIFO_TOPIC, DELAY_TOPIC, TRANSACTION_TOPIC)
                .setTransactionChecker(messageView -> TransactionResolution.COMMIT)
                // May throw {@link ClientException} if the producer is not initialized.
                .build();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static PushConsumer CreatePushConsumer(MessageListener listener) throws ClientException {
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(ACCESS_KEY, SECRET_KEY);
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(ENDPOINTS)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();

        String consumerGroup = "GID_lingchu";
        FilterExpression filterExpression = new FilterExpression(MESSAGE_TAG, FilterExpressionType.TAG);
        Map<String, FilterExpression> subscriptionExpressions = new HashMap<>();
        subscriptionExpressions.put(NORMAL_TOPIC, filterExpression);
        subscriptionExpressions.put(FIFO_TOPIC, filterExpression);
        subscriptionExpressions.put(DELAY_TOPIC, filterExpression);
        subscriptionExpressions.put(TRANSACTION_TOPIC, filterExpression);

        return provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the consumer group name.
                .setConsumerGroup(consumerGroup)
                // Set the subscription for the consumer.
                .setSubscriptionExpressions(subscriptionExpressions)
                .setMessageListener(listener)
                // May throw {@link ClientException} if the push consumer is not initialized.
                .build();
    }


    public static SendReceipt sendNormalMessage(Producer producer) throws ClientException {
        byte[] body = "This is a normal message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(NORMAL_TOPIC)
                // Message secondary classifier of message besides topic.
                .setTag(MESSAGE_TAG)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-1c151062f96e")
                .setBody(body)
                .build();
        return producer.send(message);
    }

    public static SendReceipt sendFifoMessage(Producer producer) throws ClientException {
        byte[] body = "This is a fifo message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(FIFO_TOPIC)
                // Message secondary classifier of message besides topic.
                .setTag(MESSAGE_TAG)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-1c151062f96e")
                .setMessageGroup("yourMessageGroup")
                .setBody(body)
                .build();
        return producer.send(message);
    }

    public static SendReceipt sendDelayMessage(Producer producer) throws ClientException {
        byte[] body = "This is a delay message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(DELAY_TOPIC)
                // Message secondary classifier of message besides topic.
                .setTag(MESSAGE_TAG)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-1c151062f96e")
                .setDeliveryTimestamp(System.currentTimeMillis())
                .setMessageGroup("yourMessageGroup")
                .setBody(body)
                .build();
        return producer.send(message);
    }

    public static SendReceipt sendTransactionMessage(Producer producer) throws ClientException {
        byte[] body = "This is a transaction message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        String tag = "yourMessageTagA";
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(TRANSACTION_TOPIC)
                // Message secondary classifier of message besides topic.
                .setTag(tag)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-565ef26f5727")
                .setBody(body)
                .build();
        final Transaction transaction = producer.beginTransaction();
        try {
            return producer.send(message, transaction);
        } finally {
            transaction.commit();
        }
    }
}
