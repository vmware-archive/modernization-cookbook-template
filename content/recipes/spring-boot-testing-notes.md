+++
categories = ["recipes"]
date = "2017-04-25T14:03:10-04:00"
glyph = "fa-file-text-o"
summary = "Testing strategies recommendations."
tags = ["testing", "junit", "spring boot"]
title = "Spring Boot Testing Notes"
taxonomy = ["TEST-DRIVEN-DEVELOPMENT"]
review_status = ["MERGED"]
+++

## F.I.R.S.T Principles apply.
1. F - Fast
2. I - Independent
3. R - Repeatable
4. S - Self Validating
5. T - Timely

## Isolate the functionality to be tested by limiting the context of loaded frameworks/components.

Often times, it is sufficient to use jUnit without loading any additional frameworks.  **You only need to annotate your test with @Test**

> In the very naive code snipped below, there is no database interactions, and MapRepository loads data from the classpath.

```java
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapRepositoryTest {

    private MapRepository mapRepository = new MapRepository();

    @Test
    public void shouldReturnJurisdictionForZip() {
        final String expectedJurisdiction = "NJ";
        assertEquals(expectedJurisdiction, mapRepository.findByZip("07677"));
    }
}
```

> As a next step up in complexity, consider adding mock frameworks, like mockito if you have some interactions with external resources.

```java
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    private CarService carService;

    @Mock
    private RateFinder rateFinder;

    @Before
    public void init() {
        carService = new CarService(rateFinder);
    }

    @Test
    public void shouldInteractWithRateFinderToFindBestRate() {
        carService.schedulePickup(new Date(), new Route());
        verify(rateFinder, times(1)).findBestRate(any(Route.class));
    }
}
```

## Only load slices of functionality when [testing spring boot applications](https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4).

> **@SpringBootTest** annotation loads whole application, but it is better to limit Application Context only to a set of spring components that participate in test scenario, by listing them in annotation declaration.

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MapRepository.class, CarService.class})
public class CarServiceWithRepoTest {

    @Autowired
    private CarService carService;

    @Test
    public void shouldReturnValidDateInTheFuture() {
        Date date = carService.schedulePickup(new Date(), new Route());
        assertTrue(date.getTime() > new Date().getTime());
    }
}
```

> **@DataJpaTest** only loads @Repository spring components, and will greatly improve performance by not loading @Service, @Controller, etc.

```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class MapTests {

    @Autowired
    private MapRepository repository;

    @Test
    public void findByUsernameShouldReturnUser() {
        final String expected = "NJ";
        String actual = repository.findByZip("07677")

        assertThat(expected).isEqualTo(actual);
    }
}
```

## Running Database related tests gotchas.

Sometimes, Table Already Exist exception is thrown when testing with H2 database.  This is an indication that H2 is not cleared between test invocations (because Application Context is Cached?).  This behaviour was observed when combining db tests with initialization of Wiremock.  Also could occur if multiple qualifying schema-.sql files are located in the classpath.

It is a good practice to mock the beans that are involved in db interactions, and turn off spring boot test db initialization for the spring profile that tests runs.  Please strongly consider this when testing Controllers.  Alternatively, you can try to declare your table creation DDL in schema.sql files as `CREATE TABLE IF NOT EXISTS`.

```properties
spring.datasource.initialize=false

spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
    org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration

```

## API/Controller tier testing.

>Use **@WebMvcTest** to test rest APIs exposed through Controllers.  Only list controllers that are being tested.
_Note: It looks like spring beans used by controller need to be mocked._

```java
@RunWith(SpringRunner.class)
@WebMvcTest(CarServiceController.class)
public class CarServiceControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CarService carService;

    @Test
    public void getCarShouldReturnCarDetails() {
        given(this.carService.schedulePickup(new Date(), new Route());)
            .willReturn(new Date());

        this.mvc.perform(get("/schedulePickup")
            .accept(MediaType.JSON)
            .andExpect(status().isOk());
    }
}
```

## References
- [The up to date documentation on Spring Boot testing](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html)
