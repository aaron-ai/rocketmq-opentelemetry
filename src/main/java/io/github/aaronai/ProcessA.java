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

import io.github.aaronai.grpc.client.GreetingClient;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class ProcessA {
    public static void main(String[] args) throws InterruptedException {
        final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
        final Tracer tracer = openTelemetry.getTracer("io.github.aaronai");
        final Span span = tracer.spanBuilder("ExampleUpstreamSpan").startSpan();
        try (Scope ignored = span.makeCurrent()) {
            GreetingClient.start();
            // do something here.
            Thread.sleep(1000);
        } finally {
            span.end();
        }
        Thread.sleep(99999999999L);
    }
}
