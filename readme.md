# Quarkus JMS Example

Example project for messaging with Quarkus and Apache Artemis ActiveMQ. 

Start your local Artemis message broker:

```$xslt
$ docker run -d -e ARTEMIS_USERNAME=user -e ARTEMIS_PASSWORD=password vromero/activemq-artemis
``` 

Start the consumer / producer with: 

```$xslt
$  ./mvnw -pl quarkus-consumer compile quarkus:dev
```
