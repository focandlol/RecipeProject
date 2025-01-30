FROM openjdk:17
RUN mkdir -p deploy
WORKDIR /deploy
COPY ./build/libs/recipe-app-0.0.1.jar recipe.jar
ENTRYPOINT ["java", "-jar", "/deploy/recipe.jar"]