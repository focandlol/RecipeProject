docker run -d \
--name recipe-mysql \
-e MYSQL_ROOT_PASSWORD="recipe" \
-e MYSQL_USER="recipe" \
-e MYSQL_PASSWORD="recipe" \
-e MYSQL_DATABASE="recipe" \
-p 3306:3306 \
--network docker_recipe mysql:latest