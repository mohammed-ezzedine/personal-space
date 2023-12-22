FROM openjdk:17-slim
COPY build/libs/*.jar target.jar
EXPOSE 8080
CMD java -jar -Dspring.profiles.active=production target.jar