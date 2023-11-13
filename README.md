## Spring Boot reactive web application with Spring WebFlux
### Prerequisites
- Java 21 (JDK)
- Docker (optional): only if you plan to run it inside a container
- MongoDB if you don't want to use Docker

### Configurations
Activate the maven `docker` profile if you plan to run it inside a container.

Otherwise, make sure there is no active profile.
Then set up your self-hosted Mongo Server:
- Enable access control in your Mongo instance by creating an admin user (refer to `src/main/script/mongo-admin.js` as a template)
- Then restart your instance to enable authentication by passing the `--auth` parameter to the `mongo` shell command
- Connect to the instance using your newly created admin user
- Run the script within `src/main/script/mongo-init.js` which will create a user for the `sfg` database

### Run the application
**NOTE:** All the commands below must be run from the root folder of the project
#### Using Spring Boot Maven plugin
**This method doesn't work as per the latest test, the environment variables passed to the JVM are not set**
You can use an installed maven on your local machine :
```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dmongo.username={your username} -Dmongo.password={your password}"
```
Or use the maven wrapper bundled in this project :
```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dmongo.username={your username} -Dmongo.password={your password}"
```

#### Using the Java command-line tools
If you want to run the webapp using java, you have to package the source code into a jar beforehand:
```
mvn package -Dmongo.username={your username} -Dmongo.password={your password}
```
This will create an executable jar in the `target` directory.
To run the app, enter:
```
cd target
java -jar spring-6-reactive-app-0.0.1-SNAPSHOT.jar --mongo.username={your username} --mongo.password={your password}
```

#### With Docker
Package the app with the `docker` profile active:
```
mvn package -P docker -Dmongo.username={your username} -Dmongo.password={your password}
```
The `Dockerfile` is located at `src/main/docker`
To create the image, run:
```
docker build -f ./src/main/docker/Dockerfile -t tommyrabes/spring-6-reactive-app .
```
Generate an image of MongoDB using the `Dockerfile-mongo`:
```
docker build -f ./src/main/docker/Dockerfile-mongo -t tommyrabes/beer-mongo .
```
Before you run the container of the web server, you must set up networking
within which the web server and the database containers will interact.
First, create a network:
```
docker network create --driver bridge {network name}
```
(the `--driver bridge` parameter is optional as it's the default)
Now, we have to create all the containers and connect them to that network
to ensure the entire stack run seamlessly
Create a docker container for MongoDB:
```
docker run -d --name beer-mongo --network <network name> -e MONGO_INITDB_ROOT_USERNAME=<root username> -e MONGO_INITDB_ROOT_PASSWORD=<root password> tommyrabes/beer-mongo
```
Create a docker container for the authorization server as well:
```
docker run -d -p 9000:9000 --name oauth2-authorization-server --network <network name> tommyrabes/spring-authorization-server
```
Create the docker container for web server:
```
docker run -d -p 8082:8082 --name <container name> --network <network name> tommyrabes/spring-6-reactive-app
```

#### Using Docker Compose
From the root folder, run the command:
```
docker compose up
```
To bring down and clear all the containers, run:
```
docker compose down
```

#### Using Kubernetes
You must have Kubernetes installed on your machine (including kubectl) for this method.
From the `k8s` folder, run the commands:
```
kubectl apply -f beer-mongo-deployment.yml
kubectl apply -f beer-mongo-service.yml

kubectl apply -f oauth2-authorization-server-deployment.yml
kubectl apply -f oauth2-authorization-server-service.yml

kubectl apply -f spring-6-reactive-app-deployment.yml
kubectl apply -f spring-6-reactive-app-service.yml
```

To clear everything that has been deployed, run:
```
kubectl delete -f beer-mongo-deployment.yml
kubectl delete -f beer-mongo-service.yml

kubectl delete -f oauth2-authorization-server-deployment.yml
kubectl delete -f oauth2-authorization-server-service.yml

kubectl delete -f spring-6-reactive-app-deployment.yml
kubectl delete -f spring-6-reactive-app-service.yml
```