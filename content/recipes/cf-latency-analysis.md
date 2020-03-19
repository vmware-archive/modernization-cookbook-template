+++
categories = ["recipes"]
tags = ["latency analysis","noisy neighbor"]
summary = ""
title = "Latency analysis of spring boot app in PCF"
date = "2020-03-19T010:00:00-05:00"
taxonomy = ["TROUBLESHOOTING"]
review_status = ["MERGED"]
+++

## Overview

We outline techniques to troubleshoot _slow requests_ and diagnose what might be causing _latency_ for a request from the client to the spring boot application deployed in PCF.

![](/images/cf-latency-analysis-latency-flow.png)

Any of the components in the diagram above can cause latency, delays can also come from the network itself (_network latency_). We collect, calculate and analyze the _latencies_ through all the components from client to the app.

| Request Path  | Potential Bottlenecks |
| ------------- | ------------- |
| __Client to Gorouter__ | Load balancer latency; Network  latency  |
| __Gorouter to Backend__ |  Gorouter latency; Network latency between Gorouter and Diego Cells.
| __Backend Execution__ | Backend application is taking a long time to process requests.  |

__Client to Backend (RT) <sub>time</sub>__ =    __Client to Gorouter <sub>time</sub>__ +  __Gorouter to Backend <sub>time</sub>__ + __Backend Execution <sub>time</sub>__  
`R` = (`A + B`) + (`C + D + E + F`) + `G`

## Tools
* Apache Bench for Simple Load Testing `ab`
* Cloud Foundry CLI `cf CLI`

## Steps

1. Deploy **latency-troubleshooter-app** in PCF
  * git clone [latency-troubleshooter-app](https://github.com/pbelathur/latency/tree/master/troubleshooter-app)

  * build and deploy `latency-troubleshooter-app` in PCF

2. Determine __network latency__ `ping -a https://APP-NAME`

3. Determine __Client__ to __Backend App__ <sub>time</sub> using Apache Bench for Simple Load Testing `ab`

      `ab -n 1000 -c 500 https://APP-NAME/api/generate/100000`

     ```
      Concurrency Level:      2
      Time taken for tests:   3.955 seconds
      Complete requests:      1000
      Failed requests:        0
      Total transferred:      4625489 bytes
      HTML transferred:       4556300 bytes
      Requests per second:    25.29 [#/sec] (mean)
      Time per request:       79.094 [ms] (mean)
      Time per request:       39.547 [ms] (mean, across all concurrent requests)
      Transfer rate:          1142.21 [Kbytes/sec] received

      Connection Times (ms)
                    min  mean[+/-sd] median   max
      Connect:       40   53   8.1     51      99
      Processing:    12   24   9.4     23      98
      Waiting:        5   14  10.6     12      95
      Total:         57   77  15.0     75     197

      Percentage of the requests served within a certain time (ms)
        50%     75
        66%     77
        75%     80
        80%     81
        90%     85
        95%     92
        98%    116
        99%    197
       100%    197 (longest request)
     ```

       The output shows how long requests took to complete, including latency for different parts of the request-response cycle. The `Connection Times (ms)` provides _aggregated_ connection times of each stage in milliseconds.

   * **Connect:** How long it takes `ab` to establish a TCP connection with the target server before writing the request to the connection.

   * **Processing:** How long the connection was open after being created.

   * **Waiting:** How long `ab` waits after sending the request before     beginning to read a response from the connection.

   * **Total:** The time elapsed from the moment `ab` attempts to make the connection to the time the connection closes.

    ```
      Connection Times (ms)
                    min  mean[+/-sd] median   max
      Connect:       42   71  24.3     63     198
      Processing:    13   32  11.5     33      80
      Waiting:        5   19  10.7     15      40
      Total:         65  103  30.9     95     233
    ```

    In the above example, we can see that **Connect** was on average (_both mean and median_) the longest part of the cycle and, one with the highest standard deviation. Since the **Connect** metric depends on client latency as well as server latency, we could investigate each of these, determining which side of the connection is responsible for the variation/latency.

    Finally, _percentiles_ tell you how much variation there is between the _fastest_ and _slowest_ requests, so you can address any long-tail latencies.

    * __Client__ to __Backend App__ <sub>time</sub> = mean `Total` from `Connection Times (ms)`

4.  Determine __Gorouter__ to __Backend App__ <sub>time</sub>

    * `cf logs APP_NAME` in a command window
    * execute `ab -n 1000 -c 1 https://APP-NAME/api/generate/100000` in a _separate_ command window.

    * after `ab` returns the response, enter Ctrl-C in the _other_ command window to stop streaming cf app logs.

        ```
          2019-12-14T00:33:32.35-0800 [RTR/0] OUT app1.app_domain.com -
          [14/12/2019:00:31:32.348 +0000] "GET /hello HTTP/1.1" 200 0 60 "-"
          "HTTPClient/1.0 (2.7.1, ruby 2.3.3 (2019-11-21))" "10.0.4.207:20810"
          "10.0.48.67:61555" x_forwarded_for:"52.3.107.171"
          x_forwarded_proto:"http" vcap_request_id:"01144146-1e7a-4c77-77ab
          -49ae3e286fe9" response_time:120.00641734
          app_id:"13ee085e-bdf5-4a48-aaaf-e854a8a975df"
          app_index:"0" x_b3_traceid:"3595985e7c34536a" x_b3_spanid:"3595985e7c34536a"
          x_b3_parentspanid:"
        ```
    - `response_time` is the __Gorouter__ to __Backend App <sub>time</sub>__ in seconds.


5.  Determine __Client__ to __Gorouter__ <sub>time</sub>

    * __Client__ to __Gorouter__ <sub>time</sub> = __Client__ to __Backend App <sub>time</sub>__ - __Gorouter__ to __Backend App <sub>time</sub>__


6.   Determine __Backend Execution__ <sub>time</sub>

     * `ab -n 1000 -c 1 https://APP-NAME/api/generate/100000`

     * after the backend app returns a response, access `https://APP-NAME/actuator/prometheus` using the browser and search for string: `generateJSON` in the browser and, record the `generateJSON_seconds_max` value.

           ```
           # HELP generateJSON_seconds_max  
           # TYPE generateJSON_seconds_max gauge
           generateJSON_seconds_max{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/api/generate/{size}",} 0.069610055
           # HELP generateJSON_seconds  
           # TYPE generateJSON_seconds summary
           generateJSON_seconds_count{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/api/generate/{size}",} 1.0
           generateJSON_seconds_sum{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/api/generate/{size}",} 0.069610055
           ```
       In above example: __Backend Execution__ <sub>time</sub> = `0.069610055s`

### Record Datapoints

| DATAPOINT | TIME (ms) |
|:--- | ------------- |
| __Client__ to __Backend App__ | |
| __Client__ to __Gorouter__ | |
| __Gorouter__ to __Backend App__ | |
| __Backend App Execution__ | |
| __Network Latency__ | |
| __Load Balancer Latency__<br> _obtained from the firewall/network team_ | |
| __Gorouter Latency__<br> _obtained from Platform Architect using TCP Dump Analysis, PCF Metrics_ or _component logs_ | |
| __Network Latency between Gorouter and the Diego Cell__<br> _obtained from Platform Architect using TCP Dump Analysis, PCF Metrics_ or _component logs_  | |
| Connect | |
| Process | |
| Waiting | |
| Total | |

### **Rule of Thumb** Analysis:

1. If  __Load Balancer Latency__  >>  __Network Latency__  then investigate if load balancer is configured for optimum operation.

2. If __Client__ to __Gorouter__ <sub>time</sub> > __Network Latency__  + __Load Balancer Latency__, then consider deep-dive investigation of the Client which is contributing to the latency.

3. If __Network Latency between Gorouter and the Diego Cell__ > 3 x __Gorouter Latency__ then investigate with Platform Architect if there are any network performance issues within PCF.

4. If __Gorouter Latency__ > __Network Latency between Gorouter and the Diego Cell__ OR
__Backend App Execution__ is taking a _long time_, then potential causes are:

  * Routers are under heavy load from incoming client requests.

  * App is taking a long time to process requests. This increases the number of concurrent threads held open by Gorouter, reducing capacity to handle new requests.

  * Monitor CPU load for Gorouters. At high CPU (70%+), latency increases. If the Gorouter CPU reaches this threshold, consider adding another Gorouter instance.

5. The Apache Bench `ab` **Connect**, **Processing**, **Waiting** values can also be used in conjunction with the above data points for drill down investigations.

6. If all the metrics derived from the data points above are within the permissible limits, then consider investigating the __Backend App__ for potential _memory leaks, non optimal configuration_ etc.

### Notes
* [troubleshooting slow requests in PCF](https://docs.cloudfoundry.org/adminguide/troubleshooting_slow_requests.html)
* [Apache Bench - making sense of the results](https://www.datadoghq.com/blog/apachebench/)
