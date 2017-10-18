+++
categories = ["recipes"]
tags = ["[foo]"]
summary = "Recipe Summary"
title = "Stored Proc Service Testing"
date = 2017-10-18T13:28:40-04:00
+++

Objective of the technique demonstrated is to verify execution of the sored procedures encapsulated in the service beans
as well as their signatures with the focus on isolation of beans being tested and performance.

##  Configuration class defines data source bean to be used in _non cloud_ environments.

```java
@Configuration
@Profile({"!gaia"})
public class DbConfiguration {

    @Value("${dbServerName}")
    private String serverName;

    @Value("${dbPort}")
    private int port;

    @Value("${dbUser: #{null}}" )
    private String user;

    @Value("${dbPassword: #{null}}")
    private String pwd;

    @Bean
    public DataSource getDatasource() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setUsername(user);
        config.setPassword(pwd);
        config.setJdbcUrl("jdbc:sybase:Tds:" + serverName +":" + port);
        config.setDriverClassName("com.sybase.jdbc3.jdbc.SybDriver");
        config.setConnectionTestQuery("select getdate()");
        return new HikariDataSource(config);
    }
}
```

## Load test context specifying properties configuration file.

```java
@TestConfiguration
@PropertySources({
        @PropertySource("classpath:application-test.yml")
})
@Import({DbConfiguration.class})
public class TestProcConfig {
}
```

## Test class

- Only load beans that are being tested.
- Consider implementing setUp and tearDown to manage test data.

```java
@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        TestProcConfig.class
        , FundLinkCall.class
        , TaxLotStatus.class
        , ThresholdViolator.class
})
@ActiveProfiles("dev")
public class ProcTestsIT {

    @Autowired
    private TaxLotStatus taxLotStatus;

    @Autowired
    private FundLinkCall fundLinkCall;

    @Autowired
    private ThresholdViolator thresholdViolator;

    @Test
    public void shouldExecuteThresholdViolatorProc() {
        try {
            thresholdViolator.callThresholdViolator(STATUS);
        } catch (Throwable e) {
            String message = "Not able to call threshold proc.";
            log.error(message, e);
            fail(message);
        }
    }

    @Test
    public void shouldExecuteFundLinkProc() {
        try {
            String cusip = "";
            String fundType = "";
            int omniClassCode = 0;
            int industryCode = 0;
            fundLinkCall.getFundLink(cusip, fundType, omniClassCode, industryCode);
        } catch (Throwable e) {
            String message = "Not able to call links proc.";
            log.error(message, e);
            fail(message);
        }
    }

    @Test
    public void shouldExecuteTaxLotProc() {
        try {
            taxLotStatus.getTaxLotUnavailable();
        } catch (Throwable e) {
            String message = "Not able to call tax lot proc.";
            log.error(message, e);
            fail(message);
        }
    }
}

```
