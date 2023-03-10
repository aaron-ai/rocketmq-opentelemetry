# 🚀 RocketMQ × 🔭 OpenTelemetry Distributed Tracing Example

This repository provides a practical demonstration of how to leverage OpenTelemetry to achieve distributed tracing across RocketMQ 5.0 🚀 and other libraries like gRPC and Apache HTTP client.

## Overview

OpenTelemetry is an open-source framework that provides a vendor-neutral API and instrumentation for distributed tracing, metrics, and logs. It enables observability and insight into complex distributed systems, allowing you to better understand how your systems are performing and identify potential issues.

This example shows how to use OpenTelemetry's automatic instrumentation approach, which involves using a Java agent to insert spans non-intrusively. By following this example, you can learn how to gain end-to-end visibility into your distributed system and improve your observability practices.

## Prerequisites

Before using this library, you should have:

* Java 8 or later installed.
* The latest version of the OpenTelemetry Java agent.

You can download the Java agent from the [here](https://github.com/open-telemetry/opentelemetry-java/releases/latest). The Java agent enables you to add instrumentation to your Java applications without modifying the source code.

## Workflow

The entire system is divided into three processes. Process A acts as a gRPC client and sends an RPC request to process B, which acts as the gRPC server. While processing the gRPC request, process B sends a message to RocketMQ 5.0 server. Process C starts a RocketMQ 5.0 push consumer and, in the message listener for consuming the message, uses Apache HTTP client to send a GET request to <https://taobao.com>.

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
