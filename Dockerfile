FROM amazoncorretto:21-alpine-jdk

ARG JAR_FILE=target/products-service-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
