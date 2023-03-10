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

import io.github.aaronai.http.HttpClientUtil;
import io.github.aaronai.mq.RocketMqClients;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entry2 {
    private static final Logger logger = LoggerFactory.getLogger(Entry2.class);

    @SuppressWarnings("resource")
    public static void main(String[] args) throws ClientException {
        RocketMqClients.CreatePushConsumer(messageView -> {
            logger.info("Receive message, messageId={}", messageView.getMessageId());
            HttpClientUtil.sendGetRequest();

            final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
            final Tracer tracer = openTelemetry.getTracer("io.github.aaronai");
            final Span span = tracer.spanBuilder("ExampleDownstreamSpan").startSpan();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            span.end();

            return ConsumeResult.SUCCESS;
        });
    }
}
