# ==============================================================================
# Multi-Stage Build Dockerfile for Spring Boot (Java 25 & Spring Boot 3.4.2)
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Extraction Stage (Extract JAR layers)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine AS extractor
WORKDIR /application

# The pre-built JAR file argument
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

# Extract the JAR using layertools mode
RUN java -Djarmode=layertools -jar application.jar extract

# ------------------------------------------------------------------------------
# Stage 2: Final Runner Stage (Sleek production runtime image)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine
WORKDIR /application

# Create a non-root system user for security (Principle of Least Privilege)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy each layer individually from the extractor stage
# This leverages Docker's layer caching to the absolute maximum.
# If dependencies do not change, Docker reuses the cached layer, yielding sub-second builds.
COPY --from=extractor /application/dependencies/ ./
COPY --from=extractor /application/spring-boot-loader/ ./
COPY --from=extractor /application/snapshot-dependencies/ ./
COPY --from=extractor /application/application/ ./

# Expose port (must match server.port in application.properties)
EXPOSE 8080

# Environment variables with sane defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the JarLauncher provided by Spring Boot loader (Spring Boot 3.2+ uses this class path)
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
