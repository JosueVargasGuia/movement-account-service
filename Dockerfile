FROM openjdk:11
EXPOSE  8088
WORKDIR /app
ADD   ./target/*.jar /app/movement-account-service.jar
ENTRYPOINT ["java","-jar","/app/movement-account-service.jar"] 