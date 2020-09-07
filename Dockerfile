FROM openjdk:12-alpine
#VOLUME /tmp
#ENV JAVA_OPTS="-Xdebug"
RUN apk --no-cache add curl
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV PROFILE=dev
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=${PROFILE}", "/app.jar"]