## fhir-audit-prototype

This is a prototype project that illustrates how to generate AuditEvents that will be sent to a messaging service. 

## Implementation

Add a dependency to the following project in common-libraries:
```yml
<dependency>
  <groupId>gov.samhsa.c2s</groupId>
  <artifactId>fhir-audit</artifactId>
  <version>${c2s.common-libraries.version}</version>
</dependency>
```

Also add a dependency to `spring-cloud-starter-stream-rabbit`:
```yml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

To enable as a Sring Cloud Stream project, add a simple `@EnableBinding` annonation. This is used in `TestServiceImpl`. Next `org.springframework.cloud.stream.messaging.Source` is autowired into the class. Once the object to be sent to the queue is constructed, it is sent as shown below: 

`mySource.output().send(MessageBuilder.withPayload(auditEvent).build()); //auditEvent is the object to be sent to the queue.`

The properties for RabbitMQ are specified in `application.yml`. It tells Spring Cloud Stream how to connect to the broker. We do not need to tell Spring Cloud explicitly to use RabbitMQ; it happens automatically by having that dependency in the classpath.

## Prerequisites

+ Oracle Java JDK 8
+ Apache Maven
+ RabbitMQ. This service can be run in docker using `docker run -d -p 15672:15672 -p 5672:5672 rabbitmq:latest`.

## Build

This project requires [Apache Maven](https://maven.apache.org) to build it. To build the project, navigate to the folder that contains `pom.xml` file using the terminal/command line.

+ To build a JAR:
    + Run `mvn clean package`

