FROM openjdk:17

WORKDIR /app

COPY ./build/libs/springboot-demo-0.0.1-SNAPSHOT.jar springboot-demo.jar

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "springboot-demo.jar"]