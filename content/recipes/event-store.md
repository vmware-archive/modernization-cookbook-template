+++
categories = ["recipes"]
date = "2017-06-30T14:36:56-04:00"
glyph = "fa-file-text-o"
summary = "Event Store"
tags = ["cqrs"]
title = "Event Store"
taxonomy = ["MODERNIZATION"]
review_status = ["DONE"]
+++

## Components

Event Store is implemented as a Client Library, added as dependency to the applications that send / receive events,
and Server application that orchestrates the event flow.

### Emitting Events

- Add Event Store Framework dependency

```
<dependency>
  <groupId>ip.pivotal.apptx</groupId>
  <artifactId>event-framework</artifactId>
  <version>2.2.0-SNAPSHOT</version>
</dependency>
```
- Add configuration for event posting

```
@SpringBootApplication
@EnableDiscoveryClient
@EnableEventPost
public class EventsEmmitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsEmmitterApplication.class, args);
	}
}
```

- Posts the request to create a new event message to `Event Store` API endpoint.  _EventStoreRESTClient_ shipped with the client event store library, and performs actual post to Event Store API.

```
@SpringBootTest
@RunWith(SpringRunner.class)
public class EventsTests {
    private static final String NEW_ASSOCIATED_ID = "newAssociatedId";

    private static final String NEW_AGGREGATE_ID = "newAggregateId";
    private static final String AGGREGATE_ID = "aggregateId";

    @Autowired
    private EventStoreRESTClient eventStoreService;

    @Test
    public void serviceShouldExist() {
        assertNotNull(eventStoreService);
    }

    @Test
    public void shouldAddEvent() throws Exception {
        List<Event> events = new ArrayList<>();

        events.add(eventToEmmit(new Date().getTime(), 1, 1));
        events.add(eventToEmmit(new Date().getTime(), 2, 1));
        events.add(eventToEmmit(new Date().getTime(), 3, 1));
        events.add(eventToEmmit(new Date().getTime(), 4, 1));

        ResponseEntity<List<Event>> savedEventsResponse = eventStoreService.addEvents(events);

        assertEquals(HttpStatus.ACCEPTED, savedEventsResponse.getStatusCode());

        ResponseEntity<List<Event>> listResponseEntity = eventStoreService.readByAggregateIdOrType(NEW_AGGREGATE);

        assertEquals(HttpStatus.OK, listResponseEntity.getStatusCode());
    }

    private Event eventToEmmit(long time, int version, int aggregateVersion) {
        return new Event(null
                , NEW_AGGREGATE, UUID.randomUUID().toString()
                , NEW_ASSOCIATED_ID, "RATEABLE_QUOTE"
                , time
                , time
                , true
                , "RATEABLE-QUOTE"
                , version
                , aggregateVersion
                , "{data: \"value\" }");
    }
}
```


### Listening To Events

```
@Configuration
@EnableBinding(Sink.class)
class MyEventHandler  {
	public boolean handled = false;

	@EventHandler(eventType="PolicyCreated")
	public void handle(Event event) {

		System.out.println("Policy Created Event |->" + event);
		handled = true;
	}
}
```

### Event Store

Records event to the Event Database, and relays to `Cloud Stream` destinations defined in `spring.cloud.stream.bindings.output.destination`.  Event that failed to post to the queue are recorded in Failed Events database table.


## API

API Documentation using Spring REST Docs can be found at `{URL}/docs/index.html` once deployed:


## Data Flow Diagram

![event-store](/documents/event-store/event-store-diagram.png)
