FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Pproduction

FROM tomcat:9.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
ENV db.url=
ENV db.user=
ENV db.password=
COPY --from=build /app/target/resumeStorage.war \
     /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]