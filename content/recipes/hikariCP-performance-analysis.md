+++
categories = ["recipes"]
tags = ["HikariCP","database connection pooling","performance tuning","noisy neighbor"]
summary = ""
title = "HikariCP Performance Tuning"
date = 2018-12-28T09:58:55-05:00
+++

In this recipe, you will learn to analyze/tune performance of a spring boot app that uses `HikariCP` for database connection pooling.

### Tools
* **Micrometer**: _expose_ the metrics from the spring boot application
* **Prometheus**: _store_ and _time-series aggregation_ of metric data
* **Grafana**: _visualize_ the aggregated metric data from Prometheus
* **JMeter**: for load tests


### Setup

- For **_non docker_** environments you can download `Prometheus`and `Grafana` binaries and, run them locally on the laptop. All other steps except those involving Docker shall apply.

#### Configure Spring Boot 2.x app
- Add `Actuator` and `Prometheus Registry` dependencies to `POM.xml`

  ```xml
  <dependencies>
      ...
          <dependency>
          	<groupId>org.springframework.boot</groupId>
          	<artifactId>spring-boot-starter-web</artifactId>
          </dependency>

  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>

          <dependency>
          	<groupId>io.micrometer</groupId>
          	<artifactId>micrometer-registry-prometheus</artifactId>
          </dependency>

          <!-- pulls in HikariCP -->
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
         </dependency>
    ...
    <dependencies>
   ```
- Update `application.properties`in the Spring Boot app we are trying to analyze.

  ```
  spring.datasource.url=<DB_URL>
  spring.datasource.username=<USER_NAME>
  spring.datasource.password=<PASSWORD>

  spring.datasource.hikari.maximum-pool-size=10
  spring.datasource.hikari.minimum-idle=3
  spring.datasource.hikari.idle-timeout=10000  # 10s

  spring.datasource.hikari.connection-timeout=30000 # 30s
  spring.datasource.hikari.max-lifetime=120000 # 2m
  spring.datasource.hikari.auto-commit=true
  ```

#### Setup/run Prometheus in Docker

1. Download Prometheus Docker image: `docker pull prom/prometheus`

2. Configure Prometheus to retrieve metrics from Spring Boot Actuator `/prometheus` endpoint by updating the `prometheus.yml`

    ```yml
      # my global config
      global:
        scrape_interval: 15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
        evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
        # scrape_timeout is set to the global default (10s).

      # Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
      rule_files:
        # - "first_rules.yml"
        # - "second_rules.yml"

      # A scrape configuration containing exactly one endpoint to scrape:
      # Here it's Prometheus itself.
      scrape_configs:
        # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
        - job_name: 'prometheus'
          # metrics_path defaults to '/metrics'
          # scheme defaults to 'http'.
          static_configs:
          - targets: ['127.0.0.1:9090']

        - job_name: 'spring-boot-actuator'
          metrics_path: '/actuator/prometheus'
          scrape_interval: 5s
          static_configs:
          - targets: ['HOST_IP:8080']
      ```
      * replace `HOST_IP` with your machine IP, **not** `localhost` when using docker.
      * the url of the spring boot app in PCF or the `local machine IP` (_with docker_) or `localhost:port` (_without docker_)

3. Run Prometheus

    ```
     docker run -d --name prometheus -p 9090:9090 -v <PATH_TO_prometheus.yml_FILE>:/etc/prometheus/prometheus.yml prom/prometheus --config.file=/etc/prometheus/prometheus.yml
    ```  
  * verify Prometheus is running in the container using `docker container ls` and, by navigating to Prometheus dashboard at `http://localhost:9090`.

#### Setup/run Grafana in Docker

   1. download and run Grafana: `docker run -d --name grafana -p 3000:3000 grafana/grafana`
   2. navigate to http://localhost:3000 and, log into Grafana with the default username `admin` and password `admin`
   3. click on `Add Data Source` and select `Prometheus`
   4. add HTTP URL you defined in `prometheus.yml`
   5. import [Spring Boot 2.1 Statistics Grafana Dashboard](https://grafana.com/grafana/dashboards/10280) and assign to the data source.   

#### Setup JMeter

1. Setup `JMeter` load test with REST API endpoint with `number-of-threads=240`, `ramp-up-period=30s` and `loop-count=25`  
Refer to https://octoperf.com/blog/2018/04/23/jmeter-rest-api-testing/ for JMeter setup for REST endpoint load tests.

### Analysis

1. Start the Spring Boot application (_system under test_)

2. Start JMeter load test.

3. View the Grafana `Spring Boot 2.1 Statistics` dashboard using http://localhost:3000 **during** the execution of the JMeter load test.

4. View the **HikariCP Statistics** in **Grafana** dashboard.
      ![HikariCP Statistics](/images/hikaricp-grafana.png)

  - **Connection Size** total connections in DB connection pool (`active + idle + pending`).
  - **Connections** count of `active`+ `idle` + `pending` connections over a rolling time window.
  - **Connection Usage Time** approximately equal to `db query execution time`.
  - **Connection Acquire Time**
  - **Connection Creation Time**

### Observations

| Situation | active | idle | pending | Notes |
| :--- | :--- | :--- |:--- | :--- |
| _noisy neighbor_  | 0 | `> maximumPoolSize / 2` and `> minimumIdle` | 0 | if this condition is observed under _no-request scenario_ and after considerable time after the last request, then the spring boot app is a potential _noisy neighbor_, as idle connections are **not** returned to the pool and, they consume system resources on the database server which increase connection times, decrease throughput for other applications using the same database server.|
| _sweetspot_ | `maximumPoolSize` | `<=minimumIdle` | `< 2 x maximumPoolSize` | best possible in terms of database performance, utilization and minimize chance for app to be a noisy neighbor.|
| _inadequate connections_ | `maximumPoolSize` | `<= minimumIdle` | `>3 x maximumPoolSize` | if _consistent_ spike is noticed in `Connection Usage Time`, then increase connection pool size in steps of 2 until you see performance improvement.

- If **Connections** < `active + idle + pending`
there is a _potential memory leak_ which needs further investigation through _thread/memory dump analysis_ using JDK VisualVM.

## Best Practices
A spring boot application with a service taking 50ms to complete a database query using a single connection is used to provide insights in calculating the connection pool size, idle pool size and timeouts.

#### Connection Pool Size
  * `spring.datasource.hikari.maximum-pool-size`
  * `50ms/database query` => `200 database queries/sec` per connection
  * If `pool size = 10 connections` on a single app instance, then we can handle `200 X 10 = 2000 queries/sec` per instance.
  * if we scale the apps instance to 20, we can handle `2000 x 20 = 40,000 queries/sec` among 20 instances, by using `10 x 20 = 200 connections`  

    **Recommendation**  
    Keep `pool size <= 10 connections` per app instance and _sensible_ app instance scaling to keep the `total db connections < 1000` across all app instances (_especially for Oracle_) will result in minimizing the _noisy neighbour effects_ in PCF.

#### Idle Timeout
  * `spring.datasource.hikari.idle-timeout`
  * if the database queries are fast < 250ms, then idle timeout to 10s, so that the connections are reclaimed faster preventing too many idle connections in pool.
  * for long running database queries set this value just slightly higher than the average query time.

#### Maximum lifetime
  * `spring.datasource.hikari.max-lifetime`
  * this should be set several seconds shorter than any database or infrastructure imposed connection time limit. The main idea here is the application needs to timeout _before_ the infrastructure imposed connection time limit.

#### Connection Timeout
  * `spring.datasource.hikari.connection-timeout`
  * the 30s default might be high for time critical apps, hence set the value based on the `time criticality` of the app. With 5s-10s for time critical applications. Making this value too small will result in SQLExceptions flooding the logs.
