FROM amazoncorretto:21-alpine-jdk as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

FROM amazoncorretto:21-alpine-jre as runtime
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 9090 9091
ENTRYPOINT ["java", "-jar", "/app.jar"]