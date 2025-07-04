FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Pproduction

FROM tomcat:11.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
RUN sed -i 's/<Server port="8005"/<Server port="-1"/' \
    /usr/local/tomcat/conf/server.xml
COPY --from=build /app/target/resumeStorage.war \
     /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]