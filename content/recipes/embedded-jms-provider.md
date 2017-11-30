+++
categories = ["recipes"]
tags = ["[java]", "[jms]", "[embedded]"]
summary = "Run embedded JMS provider."
title = "Embedded Jms Provider."
date = 2017-11-30T12:11:35-05:00
+++

Sometimes legacy applications rely on JMS to implement _in-app_ asynchronous processing.  This requires JMS Provider that is available in most of JEE servers.

There are several options available when replatforming these applications to run on Cloud Foundry.

Note that the common use of JMS and other messaging technologies implies communication _between_ applications / systems.  Recommended approach for _in-app_ asynchronous calls is either use of Spring's [@Async,](https://spring.io/guides/gs/async-method/) or [Java Executor Service](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html).

## Run embedded Service Broker as a JMS Provider

TO minimize code change impact during the application replatforming, we could consider embedding JMS Broker.
Following snipped demonstrates the configuration bean that defines ActiveMQ service broker as a Spring @Configuration.

```Java
@Configuration
@Slf4j
public class ActiveMqConfiguration {
//mq.brokerUrl=tcp://localhost:61616
        @Value("${mq.brokerUrl}")
        private String brokerUrl;

        @Bean
        public BrokerService broker() throws Exception {
            BrokerService brokerService = new BrokerService();
            brokerService.addConnector(new URI(brokerUrl));
            brokerService.setPersistent(false);
            log.info("Initialized ActiveMq Broker {}", brokerUrl);
            return brokerService;
        }
}
```

Required Dependencies

```xml
<dependency>
   <groupId>javax.jms</groupId>
   <artifactId>javax.jms-api</artifactId>
   <version>2.0.1</version>
</dependency>

<dependency>
   <groupId>org.springframework</groupId>
   <artifactId>spring-jms</artifactId>
   <version>${spring.version}</version>
</dependency>

<!-- ActiveMQ Artifacts -->
<dependency>
   <groupId>org.apache.activemq</groupId>
   <artifactId>activemq-spring</artifactId>
   <version>${activemq.version}</version>
</dependency>
<dependency>
   <groupId>org.apache.activemq</groupId>
   <artifactId>activemq-all</artifactId>
   <version>${activemq.version}</version>
</dependency>
```

[This application](https://github.com/poprygun/activemq) is a working example of discussed topic.
