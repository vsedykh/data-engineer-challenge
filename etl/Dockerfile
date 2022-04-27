FROM maven:3.8-jdk-11 as app-builder
WORKDIR /src
COPY . .
RUN mvn -DskipTests clean package && mv target/*.jar target/app.jar

FROM adoptopenjdk:11-jre-hotspot
COPY --from=app-builder /src/target/app.jar /app
COPY ./wait-for-it.sh /app/wait-for-it.sh