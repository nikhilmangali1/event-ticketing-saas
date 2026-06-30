# === STAGE 1: BUILD ====
# Stage 1 has Maven + JDK + source code = ~500MB
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# these layers only rebuild when pom.xml changes
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

#  this layer rebuilds on every code change, but it's FAST because deps are already cached
COPY src src

RUN ./mvnw package -DskipTests -DskipITs



# === STAGE 2: RUN ====
# Stage 2 only has JRE + final JAR = ~180MB
FROM eclipse-temurin:21-jre-alpine AS run

# creates a dedicated user with no special permission (by default container run as a root - security risk)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]