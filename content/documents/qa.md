+++
categories = ["documents"]
date = "2017-04-05T16:11:11-04:00"
glyph = "fa-book"
summary = "Document summary"
tags = ["[qa]"]
title = "Modernization Questions"
taxonomy = ["MODERNIZATION"]
review_status = ["DONE"]

+++
### New vs Existing
Q. _When do we modify an existing service for a new consumer vs. build a new service for that consumer?  For example, if a service is returning back data a certain way and a new consumer comes along and wants to see the data in a slightly different way (with potentially more data), do we modify the existing service or build a completely new service?_

R. When a consumer for a existing service comes along we build an adapter for the new consumer that adapts the data for the new consumer. We  modify an existing service aggregates and entities based on the business invariants of the domain. We never modify our internal model for an external service.

### Versioning Strategies
Q. _What are our service versioning strategies when making changes to an existing service that supports multiple clients?_

R. When a service is used by multiple clients ideally ALL changes to the service need to be backwards compatible. If this is not possible then implement [parallel change](https://martinfowler.com/bliki/ParallelChange.html). Semantically version All changes and evolutions to the service schema and APIs.

### Service Rollout to Multiple Teams
Q. _How do we coordinate multiple teams switching over to a modernized service at different times?_

R. The key here is to insert [consumer driven contracts](https://martinfowler.com/articles/consumerDrivenContracts.html). Each team onboarded  establishes a consumer driven contract with the supplier service. This give us the fine-grained insight and rapid feedback when the modernized service requires to plan changes and assess its impact on applications currently in production. The contracts established here serve as insurance when new teams onboard or when the modernized service evolves.

### Maintaining Legacy Service
Q. _As we are in the process of modernizing a service (this process could take multiple years) it’s possible that new requirements come along that need to be implemented.  How do we effectively identify that these requirements need to be implemented in both the legacy service and the modernized service._

R. You could follow a couple  of policies here
  1. Never  modify the legacy service. All new function ONLY gets added to the modernized service with suitable bridges, adapters and anti-corruption layers to the legacy service.
  2. First modify the modernized service and then take the lessons and apply them to the legacy code ideally as a standalone component or module of the legacy system.
  3. Leverage feature flags allowing you to turn off features in the legacy service once the feature is completely migrated to the modernized service.

### Migration Patterns
Q. _What is the migration strategy for cutting over clients to the modernized service?  For example, today we usually incrementally switch clients over to a new service, usually by jurisdiction (ME, NH, TX, etc.).  Is this an effective strategy?_

R. Introduce a layer of abstraction. Have both services implement the [facade](/recipes/greenfield-modernization)	. Gradually switch clients to the modernized service that implements the same facade as the old code. Clients could be migrated by any grouping criteria. Use techniques like  dynamic routing with API Gateways, Blue/Green, Context Path Routing  and canary releases to reduce the impact of cutover to the modernized service. Use feature flags to control the flow of inbound clients.

### Data Migration
Q. _How do we manage the migration of data from the legacy services to the modernized services?  Some of our tables have millions of records and hundreds of columns.  Migrating data for zip code or VIN does not help us address this concern._

R. [Branch by abstraction](https://continuousdelivery.com/2011/05/make-large-scale-changes-incrementally-with-branch-by-abstraction/) enables rapid deployment with feature development that requires large changes to the codebase. For example, consider the delicate issue of migrating data from an existing store to a new one. This can be broken down as follows:

0. Require a transition period during which both the original and new schemas exist in production
1. Encapsulate access to the data in an appropriate data type. Expose a Facade service to  encapsulate DB changes.            
2. Modify the implementation to store data in both the old and the new stores. Move logic and constraints to the edge aka services
3. Bulk migrate existing data from the old store to the new store.
4. This is done in the background in parallel to writing new data to both stores.
5. Modify the implementation to read from both stores and compare the obtained data. Implement retry and compensations. Database Transformation Patterns cataloged like Data sync, data replication and migrating data.
6. Leverage techniques like TCP Proxy for JDBC to understand the flow of data and transparently intercept traffic. Use Change Data Capture tooling to populate alternate datastores.
7. When convinced that the new store is operating as intended, switch to using the new store exclusively (the old store may be maintained for some time to safeguard against unforeseen problems).

### Rollout to Multiple Teams
Q. _What happens when a journey team has a service that multiple teams want to use?_

R. Establish appropriate provider and consumer contracts with downstream consumers and expose a consumable API. The downstream consumers will conform to the model exposed by the desired Journey services.

### Identifying Users of the Service
Q. _What is our strategy for figuring out who the existing clients are?_

R. Insert transparent proxies in the routing flow to determine all the downstream consumers. Leverage edge entry controller patterns like bridge, router, proxy, facade and backends 4 frontends.

### Communicating to a New Service from Legacy Code
Q. _What are some technical issues we may run into when a legacy service tries consuming a DNA service?_

R. Model mismatch, Mapping and translation, data duplication, unnecessary hops, data consistency.

### Identifying Candidates for Modernization
Q. _Why are we only talking about modernizing services in the context of Market Services?_

R. Modernization has to start from some point. There are various starting points. You should avoid analysis-paralysis and quickly start learning to inform the refactoring of the rest of the code. Perhaps a core domain that is upstream to a number of services would be a better starting point.

### Deployment Strategy
Q. _We currently operate on a monthly release cycle.  At any given time, we will have 4 different environments to support 4 different monthly releases (i.e. January, February, March and April environments).  These environments are denoted by A, B, C, D.  We will not be able to completely break away from this release schedule for years._

R. Understood this is more of a DevOps issue. You need to transform the value chain following this playbook.  Microservices versioning and deployment strategy is outlined [here](https://opencredo.com/versioning-a-microservice-system-with-git/).

1. Identify a single product to work with / go after
2. Put all the people responsible for the thing together (design, dev, qa, arch, pm etc), permanently
3. Identify the thing that 1. is done most often and 2. is repeated most often (use a 2x2)
4. Fix it, solution can totally be a one off as long as you learn from it
repeat 3&4.

### Benefits of Modernization
Q. _What does the dialog look like with the current consumers of legacy services when we are trying to move them to a modernized a capability?_

R. Surface the pain first. Talk to them about existing pain points and integration down the road. Provide a roadmap of expected changes to the API and policies for evolving the service. Establish provider and supplier contracts and a protocol for communication that will survive schema evolution.

### Use DDD to Identify Bounded Contexts
Q. _We have over 1500 different service operations today.  So far our strategy has been to increment over each one of these operations based off of a very focused isolated use case and eventually reach out to other clients to understand their needs._

R. You should take the time and examine these discrete operations and find opportunities to consolidate them. Consumers need to call Car.start() and not Car.getEngine().start(). Tell the API to carry out a capability rather than orchestrate discrete flows with data.

### Testing Strategies
Q. _Performance testing strategies across the entire ecosystem._

R. Unit tests, WireMock Tests, Service Virtualization with Hoverfly, Microservices testbed,  Synthetic tests in production, SOAP-UI tests, Selenium web driver tests, IntegrationTests, Functional Tests,  Stress tests, Chaos tests, PEN Tests, User acceptance Tests, A/B tests. [see](https://martinfowler.com/articles/microservice-testing/)
