In this file, record your progress and think aloud about
the challenge.
## Ordering
I see that Cards are going in the order, but it is better to make some checks on the target adapters side to be sure that we are not publishing outdated data. I haven't implemented it, but if I did that I would use some in-memory storage that allows me to check it fast

## Eventual consistency
I realize that users can be processed later than cards for it. In my solution, just for PoC, I made a workaround that just inserts an id for a user, and eventually, his or her info will be updated, but if this is not acceptable from a business point of view it is possible to make eventual consistency from another end. A possible solution here is to do an additional retry topic where all messages that were failed due to the absence of users will be placed and reprocessed after some time

## Validation
I implemented validation based on JSON schema as the easiest way to do it.
Users always generated validly, but I implemented validation for it too just to show that we have two options where we can validate it and two options for what we can do with failed messages. For users: failed events will be saved as files in the 'error' folder and we don't need validation on the target adapter side.
For cards, validation is made on the target adapter side, and in case of error, the failed message will be sent to DLQ (Dead letter queue). And we can easily set up our monitoring system to check the error topic or folder (for the topic I think it is easier) if there are new entries and raise an alert if yes.
Another possible level of validation can be beased on DB schema, but i haven't implemented it in my solution for simplicity

## Messaging
I introduced messaging in the middle because of the following reasons:
- we can scale independently source and a target part (of course for it we have to split the ETL module into different containers)
- it is a more extensible solution. Eventually, if there will be other consumers of those events they can easily connect to the topic and consume events

## Aggregation
If we want to reduce the load on the DB side we can introduce an additional layer of aggregation. For this specific case, it will be useful only for cards events. It could be based for example on Kafka streams technology that will aggregate cards events using some time window and send the aggregated event to the DB.

## Other solutions
There are several other solutions that I can propose from the top of my head in the case of Java+kafka for some reasons is not suitable. Some ETL tools like Apache NiFi or Apache Airflow if we need an open-source, Talend or Informatica if we need an enterprise-level solution.
Also, we can use the same tech stack, but  implement [Event Sourcing](https://microservices.io/patterns/data/event-sourcing.html) pattern, that could help resolve possible issues with ordering, aggregation, eliminate need of caching.

## Reliability
The solution is fault tolerant and provides ay-least-once delivery guaranty. Failed events are not lost and stored in dedicated folder or kafka topic.

## Scalability
Solution is highly scalable especialy if we break down ETL app in four different parts. Source adpaters can also be scale if needed, Camel provides capabilities to run file adapter component in cluster mode. Also if we want to scale target adapters part we need to introduce more partitions into kafka topics

## Performance
For that particular load my solution is handle it successfuly, but as I mentioned in sections above we could improve performance on different levels of the solutions. For example, currently the most load is on DB and target adapter because to process each record it has to do 2 queries: 1. select by id to check if record already exists and then 2. insert or update. We could eliminate it by introducing cashing or event sourcing pattern

## Observability
A usual, here we have some options. For PoC and with all logic inside one component approach that I implemented is more than enough:
- UI tool for Apache Camel that provides good monitoring on routes with the numbering of successful and failed runs and the time that every step took to implement. Good for PoC, but not enough for production.
After application is started UI console is available by link http://DOCKER_HOST:8080/actuator/hawtio/. Then we should connect to application (unfortunatelly it is not possible to configure it in advance) by pushing button 'Add connection' and fill out there 'name' field with some value, for 'host' put 'localhost', leave for the rest fields default values and push 'add'. After that we wii be able to connect to application and it should show high level statistics for all routes. ß

For production I would use classic solutions:
- ELK for logging + Prometheus and Kibana for metrics. 
- Jager for distributed tracing that especially useful when we have a lot of dedicated parts of our application that run in different containers (like if we split the ETL application into 4 parts it could be much more suitable)
- Transaction id - that logs in each log message and using it we can easily get log history for every event processed. I haven't added it, but it could be easily done.

## DB structure
In my solution DB tables are created automatically by Hibernate, but for the real and more complex solution, it is better to have a dedicated tool responsible for it. The first that comes into my mind is [Liquibase](https://www.liquibase.org/) which allows to store and manage DB structure as a code, use it in CI/CD pipelines, and quickly do rollbacks if needed.

