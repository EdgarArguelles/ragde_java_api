FROM openjdk:14 as builder

WORKDIR /app

# Copy and build src.
COPY build.gradle gradlew ./
COPY gradle/wrapper/* ./gradle/wrapper/
COPY src ./src/
RUN ./gradlew build -x test

FROM openjdk:14

# Run the web service on container startup.
COPY --from=builder /app/build/libs/*.jar /app.jar

# Run the web service on container startup.
CMD ["java","-Dserver.port=${PORT}","-Dspring.profiles.active=${PROFILE}","-jar","/app.jar"]