# Build and run gen-ai-spring-ai
FROM maven:3.9.6-eclipse-temurin-25 as builder
WORKDIR /workspace
COPY . /workspace
RUN mvn -pl ai/gen-ai-spring-ai -am -DskipTests clean package -T 1C -B

FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=builder /workspace/ai/gen-ai-spring-ai/target/gen-ai-spring-ai-1.0-SNAPSHOT.jar app.jar

# Download OpenTelemetry Java agent for auto-instrumentation (no code changes required)
ARG OTEL_AGENT_VERSION=1.29.0
RUN apt-get update \
  && apt-get install -y --no-install-recommends curl ca-certificates \
  && curl -fsSL -o /app/opentelemetry-javaagent.jar \
     https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar \
  && apt-get remove -y curl \
  && apt-get autoremove -y \
  && rm -rf /var/lib/apt/lists/*

EXPOSE 8081
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_SERVICE_NAME=gen-ai-spring-ai
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

ENTRYPOINT ["java","-jar","/app/app.jar"]
