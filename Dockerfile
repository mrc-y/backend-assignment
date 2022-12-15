FROM gcr.io/distroless/java17:latest

COPY ./target/api-impl-1.0-SNAPSHOT.jar api.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/api.jar"]

