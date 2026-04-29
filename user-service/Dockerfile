# ─── Stage 1: BUILD ───────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY account-service/pom.xml account-service/pom.xml
COPY transaction-service/pom.xml transaction-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml

RUN mvn dependency:go-offline -B

COPY common/src common/src
COPY user-service/src user-service/src

RUN mvn clean package -pl common,user-service -am -DskipTests

# ─── Stage 2: RUN ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

RUN addgroup --system securebank && adduser --system securebank --ingroup securebank

WORKDIR /app

COPY --from=builder /app/user-service/target/*.jar app.jar

RUN chown securebank:securebank app.jar

USER securebank

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -q --spider http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", \
            "-Dspring.profiles.active=local", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "app.jar"]