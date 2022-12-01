# Kafka client side metrics playground

Goal of this project: understand the client side Kafka metrics for both consumer and producers.
With the focus on client side consumer lag metrics and understand how they should be interpreted compared to the consumer lag metrics available on the Kafka broker(s). 

This small project can be used to reproduce the difference in consumer lag metrics

## Project modules and applications

| Applications          | Port | Avro  | Topic(s)      | Description                                                                                                                               |
|-----------------------|------|-------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| spring-kafka-producer | 8080 | YES   | stock-quotes  | Simple producer of random stock quotes using Spring Kafka & Apache Avro.                                                                  |
| spring-kafka-consumer | 8082 | YES   | stock-quotes  | Simple consumer of stock quotes using using Spring Kafka & Apache Avro. This application will be a slow consumer, building up conumer lag |

| Module     | Description                                                                                                                |
|------------|----------------------------------------------------------------------------------------------------------------------------|
| avro-model | Holds the Avro schema for the Stock Quote including `avro-maven-plugin` to generate Java code based on the Avro Schema. This module is used by both the producer, consumer and Kafka streams application. |

Note Confluent Schema Registry is running on port: `8081` using Docker see: [docker-compose.yml](docker-compose.yml).

## Overview  

![](documentation/images/project-overview.png)

Including monitoring:

![](documentation/images/project-overview-including-monitoring.png)



## Version

* Confluent Kafka: 7.2.x
* Confluent Schema Registry: 7.2.x
* Java: 11
* Spring Boot: 2.7.x
* Spring for Apache Kafka: 2.8.x
* Apache Avro: 1.11

## Components running in Docker

* Kafka (port 9092)
* Zookeeper (port 2181)
* [Schema Registry](http://localhost:8081)
* [Kafka UI](http://localhost:9000)
* [Prometheus](http://localhost:9090)
  * See scrapted [targets](http://localhost:9090/targets)
* [Grafana](http://localhost:3000/)
  * [Dashboard](http://localhost:3000/dashboards) 
  * [Consumer dashboard](http://localhost:3000/d/CLUjsRFZz/kafka-consumer) (based on Kafka client side metrics)

## Build the project

```bash
./mvnw clean install
```

## Run 

### Step1: Run the infrastructure (Kafka, Zookeeper, Prometheus, Grafana and Kafka UI)

```bash
docker-compose -f docker-compose.yml up -d
```

### Step 2: Run the producer application

```bash
./mvnw spring-boot:run -pl spring-kafka-producer
```

Once started this application start producing a Stock Quote every 10ms to produce load on the topic [stock-quotes](http://localhost:9000/ui/clusters/local/topics/stock-quotes)

The producer application will expose Kafka client metrics via Micrometer ready for Prometheus to scrape.
* [Producer - Kafka client side metrics Prometheus ](http://localhost:8080/actuator/prometheus)

### Step 3: 

Now start the consumer application

```bash
./mvnw spring-boot:run -pl spring-kafka-consumer
```

This application will simulate a slow consumer. 
To intentionally build up consumer lag!

The conumer application will expose Kafka client metrics via Micrometer ready for Prometheus to scrape.
* [Producer - Kafka client side metrics Prometheus ](http://localhost:8082/actuator/prometheus)

We are in particular interested in the following metrics:
* `kafka_consumer_fetch_manager_records_lag`
  * [See kafka_consumer_fetch_manager_records_lag metric in Prometheus](http://localhost:9090/graph?g0.expr=kafka_consumer_fetch_manager_records_lag&g0.tab=1&g0.stacked=0&g0.show_exemplars=0&g0.range_input=1h)
* `kafka_consumer_fetch_manager_records_lag_avg`
  * [See kafka_consumer_fetch_manager_records_lag_avg metric in Prometheus](http://localhost:9090/graph?g0.expr=kafka_consumer_fetch_manager_records_lag_avg&g0.tab=1&g0.stacked=0&g0.show_exemplars=0&g0.range_input=1h) 

See: [StockQuoteConsumer.java](spring-kafka-consumer/src/main/java/nl/jtim/spring/kafka/consumer/StockQuoteConsumer.java)

## Shutdown 

```
docker-compose down -v
```

Press `ctrl` + `c` to stop the Spring Boot application.