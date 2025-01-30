docker run --name recipe-redis \
             -p 6379:6379 \
             --network docker_recipe \
             -d redis:latest
