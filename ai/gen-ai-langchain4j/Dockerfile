# Build and run gen-ai-langchain4j
FROM maven:3.9.6-eclipse-temurin-25 as builder
WORKDIR /workspace
COPY . /workspace
RUN mvn -pl ai/gen-ai-langchain4j -am -DskipTests clean package -T 1C -B

FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=builder /workspace/ai/gen-ai-langchain4j/target/gen-ai-langchain4j-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENTRYPOINT ["java","-jar","/app/app.jar"]
