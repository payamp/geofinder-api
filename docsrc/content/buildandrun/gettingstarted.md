---
date: 2017-09-07T22:35:05-04:00
title: Getting Started
menu:
  main:
      parent: Build and Run
      identifier: Getting Started
      weight: 101
---

Example for using DSE Streaming analytics for product recommendations

## Startup Script

This Asset leverages
[simple-startup](https://github.com/jshook/simple-startup). To start the entire
asset run `./startup all` for other options run `./startup`

## Manual Usage:

### Prereqs

Prepare S3, Zookeeper, and Kafka

### Setup c* DDL

    mvn clean compile exec:java -Dexec.mainClass=com.datastax.utils.SchemaSetup

### Run the Producer

    mvn clean compile exec:java -Dexec.mainClass=com.datastax.tickdata.producer.TickProducer

### Run the Consumer

    mvn clean compile exec:java -Dexec.mainClass=com.datastax.tickdata.consumer.TickConsumer

### Setup the graph

    dse gremlin-console -e schema.groovy

### Run the join

    ./dfjoin
