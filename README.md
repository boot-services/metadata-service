# Metadata Service 

## Default Spring Boot actuator endpoints

1. `/actuator/info`
2. `/actuator/health`


## Metadata Service endpoints (to POST and GET metadata objects)

1) POST to create an entry in the database

```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"group":"sunitparekh","name":"city","value":"Pune"}' \
  http://localhost:8080/metadata
```

2) GET all entres posted in step 1

`curl http://localhost:8080/metadata`


# files to chanage for connecting to actual mongodb instance
1) src/main/java/org/boot/services/metadata/InMemoryMongoDB.java
comment @Configuration on line # 8

2) src/main/resources/application.properties
comment out line 24 
uncomment line 25 (remember the name of the mongodb server or change it as you like)
