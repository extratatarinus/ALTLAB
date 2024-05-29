FROM openjdk

WORKDIR /usr/src/myapp
COPY /target/ECOM-0.0.1-SNAPSHOT.jar ECOM-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","ECOM-0.0.1-SNAPSHOT.jar" ]
