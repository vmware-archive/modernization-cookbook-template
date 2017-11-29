+++
categories = ["recipes"]
tags = ["[performance]", "[iBatis]", "[maven]"]
summary = "Techniques to improve startup time of Spring/iBatis application."
title = "Fix iBatis Startup and maven compile performance."
date = 2017-11-28T09:02:35-05:00
+++

Slow startup times affect development cycle and 12 factor compliance.

## Guidelines to address slow startup.

Turning off cashing, and turning on lazy load for local development and test environments.

### Spring application context file.

```xml
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean" lazy-init="true">
    <property name="dataSource" ref="dataSource" />
    <property name="mapperLocations" ref="mapperLocations" />
    <property name="transactionFactory" ref="jdbcTransactionFactory" />
</bean>
```

### iBatis configuration file.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE sqlMapConfig
        PUBLIC "-//ibatis.apache.org//DTD SQL Map Config 2.0//EN"
        "http://ibatis.apache.org/dtd/sql-map-config-2.dtd">
<sqlMapConfig>

    <!--  Lazy loading is globally enabled here -->
    <settings useStatementNamespaces="true" cacheModelsEnabled="false" lazyLoadingEnabled="true"/>
```

### Bump up maven thread count.

In this example, we run 2 threads per cpu core.

```bash
mvn -T 2C clean install
```
