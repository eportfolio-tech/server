FROM openjdk:12-alpine
#VOLUME /tmp
#ENV JAVA_OPTS="-Xdebug"
RUN apk --no-cache add curl
RUN addgroup -S spring && adduser -S spring -G spring
# Create directory to store Hibernate Search Index and grant Spring permission to access Index directory
#RUN mkdir -p /data/index/default && chown -R spring:spring /data
#RUN chmod 775 /data/index/default
USER spring:spring
ARG JAR_FILE=target/*.jar
ARG PROFILE
WORKDIR "/home/spring/"
COPY ${JAR_FILE} app.jar
ENV PROFILE=$PROFILE
EXPOSE 8090
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=${PROFILE}", "app.jar"]