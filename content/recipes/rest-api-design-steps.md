+++
categories = ["recipes"]
tags = ["[REST]"]
summary = "Recipe Summary"
title = "REST API Design Steps"
date = 2018-04-23T16:30:30-05:00
+++

## When to Use This Recipe

This recipe should be followed when collaborating on a project that requires designing new RESTful APIs.

## Overview

 When the event arises for the implementation of a RESTful API, the following steps could be followed to help drive out an overall design strategy.  In some cases, the project may entail multiple teams implementing multiple applications that include the creation of RESTful APIs.  Before implementation of APIs begin, a representative from the various teams across the project would ideally agree upon an overall API design strategy.  The goal being that at the conclusion of the project, all RESTful APIs implemented as part of the project work in a consistent fashion and the client is enabled to evolve the APIs independently.

 The first walkthrough of the steps listed below may take some additional effort.  However, the intent of the initial walk through would not be to perform a large up front API design.  Rather, the goal would be to agree on an overall API Design strategy based primarily on the requirements that are known.  As requirements are refined or added or as additional APIs stories arise, these steps could be repeated as a way to incrementally refine the overall API design strategy for the project.  Subsequent walkthrough of these steps would be easier since the purpose would be to identify the aspects of the design strategy needing refinement.

1. Choose Media Types
1. URI Design
1. Resource Design
1. Choose Methods
1. Select Response Status Codes
1. Define Headers
1. Query Parameter Design
1. Define API Evolution strategy

---

## Steps

Following are details pertaining to the above steps that can be used to establish an API design strategy.  Each step defines the goal and provides some notes regarding recommendations, things to avoid, and examples.  The recommendations could be used as a starting point for the design strategy which then could be refined to meet the teams personal preference and project requirements.

In terms of examples to help provide clarity, we will apply the following concepts to two example resources.  The first being a `customer` resource that would have a one-to-many relationship with an `order` resource.

The following terms will be used going forward to describe two types of resources.

- Collection Resources (ie `/customers`)
- Singular Resources (ie `/customers/{id}`)

### Choose Media Types

#### Goal:
Come to an agreement on what the standard media types would be for the project.

#### Notes:
In the majority of the cases, this would simply be `application/json`, but occasionally other media types occasionally can be useful such as:

- `application/x-www-form-urlencoded`
- `multipart/form-data`

Even though spring boot has nice support for it, using Hypermedia Types such as `application/hal+json` should be avoided.

### URI Design

#### Goal:
Establish the principles that would be used to define the URIs for resources.

#### Notes:

> URIs should be described using nouns.  Plural nouns to describe collection resources.  Singular resources most often will be referenced via an identifier.  However, in the case where only one resource can exist, then use a singular noun.

> Traditionally a URI for orders would potentially look something like `/apiname/context/v1/customers/{id}/orders`. More recently however, it is becoming more common to attempt to simplify the URI as much as possible and to split into separate URIs such as `/customers` and `/orders`.

> URIs are not case sensitive.  As a result, if the resource name contains multiple words, then a '-' would typically be used to separate the words.  However, if the format of the resource is snake case, then '\_' tends to be more common.

> URI collisions should attempt to be avoided if possible.  For example, defining two URIs such as /orders/{id} and /orders/count.

#### Examples:

``` text
/customers
/customers/{id}
/orders
/orders/{id}
```

### Resource Design

#### Goal:
The goal of this step is to establish how request and response content should be structured and when it should be provided and returned.

#### Notes:

> Resource content generally is provided only for `POST`, `PUT`, and `PATCH` requests.  The HTTP spec supports content to be provided for `DELETE` requests, but this practice is generally discouraged.  `GET` requests can at times be a point of contention since it is somewhat unclear in the HTTP spec if `GET`s are allowed to have request content, but this would be discouraged as well.  This issue tends to arise for APIs that provide complex searching functionality.

> Resource content provided in the request generally should match what is returned in the response.  The only exception is for attributes that are defined in the schema as read only.  It is discouraged to define attributes for the request that would never be returned in the response. In addition, the type of resource returned should be the same type as what was provided.  For example, a `POST /customers/{id}` would be provided a customer resulting in a customer to be returned instead of a collection of customers.

> The general rule to use to determine when to return response content is that if the response content would match the request content, then no  content would need to be returned.  In this situation, a `204` status should be returned.  However, a single URI returning multiple successful response status should be avoided.

> It is strongly discouraged to provide a `links` attribute as part of resource response content for `application/json` media types.

**Singular Resources** <br>
Short and concise naming of attributes resource.

``` json
{
  "id": "7877a21a721f0fe399dc6f1086a45892",
  "firstName": "Sam",
  "lastName": "Spurlock",
  "username": "sspurlock",
  "email": "sspurlock@exampleorg.com",
  "locale": "en",
  "created": "2009-04-15T00:50:04Z"
}
```

**Collection Resources** <br>
Consideration should be given to the potential size of a collection resource.  For large collections, a pagination solution may be needed.  The following example shows the basic structure for a collection resource without pagination.
``` json
[
	{ ... },
	{ ... }
]
```

The following example shows the basic structure for a collection resource with pagination.  It is considered good practice to include `limit`, `offset`, and `count` as part of the response for a collection resource where `offset` and `limit` would have corresponding query parameters.
``` json
{
  "customers": [
    { ... },
    { ... }
  ],
  "offset": 0,
  "limit": 10,
  "count": 20
}
```

**Error Responses** <br>

There are certainly a number of valid ways to design error response content.  General recommendations would be to ensure that all errors have a unified format and provide errors in a way that would accommodate multiple errors to be returned in case multiple aspects of the request failed validation.

It can be beneficial for each error to have a `code` and `subcode` attribute. Having a `subcode` attribute can help mitigate the creation of a new version of the API since the creation of a new code value could be considered a non-backward compatible change.  A `localizedMessage` may also be needed if a localized (based on the `accept-language` header) needs to be provided.

**Example**
``` json
[
  {
    "code": "error-code-1",
    "subcode": "sub-error-code-1",
    "description": "short reason for the cause of error code 1",
    "localizedMessage": "Première erreur d'exemple"
  },
  {
    "code": "error-code-2",
    "subcode": "sub-error-code-2",
    "description": "short reason for the cause of error code 2",
    "localizedMessage": "Deuxième exemple d'erreur"
  }
]
```

**Resource Formatting**

When it comes to how a resource should be formatted, either it is given little to no thought or it becomes a subject of all out holy war. In essence, there are two competing models which are `camelCase` or `snake_case`.  The format that is typically chosen is more often than not the result of the language being used to produce the API.  However, it would be good to consider the consuming application(s) preference.  Important to note that there is no standard so there really isn't any wrong choice although snake case seems to becoming the more popular format amongst newer APIs.

### Choose Methods

#### Goal:
Come to an agreement on the strategy to use when determining the HTTP verbs to support for URIs.

#### Notes:

**GET**

Used to retrieve a resource.  A `GET` request should never alter the state of of the resource and should be idempotent.

**POST**

As per the HTTP spec, the POST method should be used to create a resource.  It is not http spec compliant, but it is common for a POST to be used to perform a merge update in lieu of `PATCH`.  `POST` more often than not should not be idempotent.  The first `POST` request may create a resource while subsequent requests would resort in a 4XX error.  `POST` to perform merge updates however, would be idempotent.

**PUT**

Used to replace an existing resource.  If a resource needs to support nulling out optional attributes, then `PUT` could be used.

**DELETE**

Used to remove a resource.  Normally `DELETE` would not be idempotent since the first request would be successful while subsequent requests would return a 404 error.

**PATCH**

Is intended to be used to perform a merge update on a resource.  `PATCH` can also be used to assign null to optional attributes.  However, it is considered a best practice to follow the [patch spec](https://tools.ietf.org/html/rfc5789#section-2.1) and not to [Patch Like an Idiot](http://williamdurand.fr/2014/02/14/please-do-not-patch-like-an-idiot/).

**OPTIONS**

Spring boot provides support for OPTIONS so no additional coding is needed unless the API needs support for [CORS](https://spring.io/guides/gs/rest-service-cors/ "Spring CORS Tutorial").

> Using `GET` to retrieve, `POST` to create, `PUT` to update, and `DELETE` to remove is commonly agreed upon.  How to perform partial updates  is where there tends to be some contention.  However, there are two general strategies that are most commonly used. <br>
1) Use `POST` to collection resource to create, `POST` to a singular resource for partial updates, and `PUT` to singular resources for complete replace. <br>
2) Only use `POST` to create and `PATCH` for updates.  While this strategy is more compliant with the HTTP spec, the former strategy tends to be more popular due to the complexity around using `PATCH`.

### Select Response Status Codes

#### Goal:
Come to agreement on the HTTP Status Codes to use for successful, validation failure, and error responses.

#### Notes:

> When choosing HTTP Response Status Codes, a prevalent strategy is to choose codes from the HTTP1.1 specification and to avoid those defined as part of the WebDAV HTTP extensions.

**Successful Responses**

> It is recommended that a given URI should should only support returning one 2XX response code.  In other words, a `POST /customers` should not return a 201 status for some requests while returning a 200 status for others.

Response Code | Notes
------------ | -------------
200 | Server accepted the request or at least some portion of the request.<br>200 status is normally used for successful
201 | Resource created.  Location response header required to be returned.<br>If the state of the resource remains the same as what was provided in the request, then it is generally acceptable to return a 201 with no response content.  Otherwise, the 201 response should have the resource content returned;
204 | Successful response when no response content needs to be returned.  Most commonly would be returned for `DELETE` requests.  Would also be valid for `PUT` or `POST`, but is normally preferable for those requests to provide the resource as part of the response.

> Requests that were _partially_ accepted by the service would return a 200 or 201 status along with errors indicating which portions of the request were rejected.

**Validation Failure Responses**

Response Code | Notes
------------ | -------------
400 | The request was not structured correctly and did not conform to the resource schema.
401 | Request failed authorization validation.
403 | Request failed authentication validation.
404 | Resource could not be found for the request.
409 | The request was structured correctly, but was rejected by the server as a result of business rule validation.
405<br>406<br>415 | Invalid method, accept, or content media type.  Spring boot framework handles all these out of box and no custom logic should be needed to support these.
429 | Status returned for throttling or rate limiting errors detected by a gateway proxy application.

**Error Response**

> The service encountered unexpected errors.  In general, it is the goal of an API to never return 5XX errors.  Monitoring and Alerting mechanisms should be able look specifically for these types of errors in order to trigger actions that would be needed in order to resolve. Essentially, a 500 error should be mean that the API is down and unable to respond to requests.

Response Code | Notes
------------ | -------------
500 | Status to return as part of handling unexpected exceptions.
502<br>503<br>504 | These statuses should not be returned in a response for the API application.  These would be provided by a gateway or proxy layer.

**Redirect Statuses**

> Redirect response status are typically leveraged when transitioning control from the API Application to a Web Application.  Generally a `302` status code would be used for these situations.

### Define Headers

#### Goal:
Determine the custom headers to define for requests and responses.

#### Notes:

> Common convention dictates that Cookies should not be leveraged by RESTful APIs.

**Request Headers**

> In general, the implementation of custom request headers should be rare with the exception of things such as versioning.

**Response Headers**

> The implementation of custom response headers should be fairly common. Some common examples of custom response headers would be for things such as a *request UUID* and *resource type*.

> If hypermedia links would be needed provided by responses, the [link header](https://www.w3.org/wiki/LinkHeader) is an option worth consideration.

### Query Parameter Design

#### Goal:
Define a strategy for naming of query parameters, guidelines around how and when they would be used, and determine names for commonly used query parameters for things such as sorting.

#### Notes:

> Query parameters are generally defined only for `GET` requests and tend to be used more predominately with collection resources. Defining query parameters for other methods is generally considered bad practice. However, `POST`s that support query parameters so that request content need not be provided can help simplify web applications that consume API responses.

> In order for an API to remain intuitive and easy to learn, it is advantageous to refrain from implementing action query parameters.

> A common approach is to have query parameters where the name matches one of the attributes in the resource in order to provide collection resource filtering.

> When pagination is needed for a collection resources, generally recommended to use `limit` and `offset` query parameters.

> Query parameters are typically optional.  Defining query parameters that are required for a request is generally considered bad practice.

### Examples

Example request to check if a customer exists for a given `username`.  The response would always be a collection resource.  An empty collection would be returned if no customers were found.  A `404` error should not occur in that case.
``` text
GET /customers?username={name}
```

Example request to pull a given number of orders over a specified amount
``` text
GET /orders?amount>{amount}&limit={limit}
```

### Define API Evolution Strategy

#### Goal:
The goal of this step is to determine how the APIs will evolve incrementally over sprint iterations.  Typically this would involve agreement on a versioning strategy.

> First step in determining a version strategy is to come to agreement on the definition of a non-backward compatible change.
A common approach is to adopt the [tolerant reader](https://martinfowler.com/bliki/TolerantReader.html) principle.  This can help mitigate the creation of new versions.

#### Non-versioning strategy

> The non-versioning strategy entails always making enhancements to an API in a backwards compatible fashion.  In the event that this cannot be done, then a temporary version of the API is created and a process is initiated that would upgrade all consuming application to the latest version.  Once the process is completed, the legacy version is then removed.

> Ideally an API could start without a versioning mechanism in place and one would not be implemented until the time one is actually needed.

#### Versioning strategy

> There are number of common API versioning strategies that can found.  One of these is a strategy based on a custom header approach where the value is a date.  How the date is determined can be done a few ways.  

> One approach is to have the consuming application always provide it as part of every request.  Another approach is to have a gateway application (such as Spring Cloud Gateway) that captures the event of the first request and store the date with the associated principal.

> Regardless of how the header is determined, the version header approach works nicely since it allows for specific aspects of the API to be versioned rather than having to version the entire API.  It also plays well with gateway applications that perform orchestration since the gateway would not need to know anything about what versions exists for each origin service.  Lastly, this versioning strategy would need to be implemented until the time a versioning mechanism is needed.

> Using a URL based versioning strategy is losing popularity and should be avoided. The following is a comment from Fielding reflects his view on this approach. <br>
**Roy T. Fielding** <br>
_@fielding_ <br>
The reason to make a real REST API is to get evolvability … a "v1" is a middle finger to your API customers, indicating RPC/HTTP (not REST)

---

## JSON Schema

> JSON Schema can be leveraged far differently than the old days of XML Schema.  Rather than the schema being a static file manually provided to consuming application, it can instead by provided via an API of its own which can dynamically vary between requests.  

> Implementing JSON Schema APIs can involve a fairly low level of effort.  One way it can be defined is to first define the schema using swagger.  The Java POJO can the be generated from the swagger and the JSON Schema can be generated from the POJO to be returned by the schema API.  Before being returned, the API Application can choose to dynamically alter the schema based on information provided in the request.

``` text
GET /schemas/customers
```
``` json
{
	"$id": "https://example.pcf.io/schemas/customer",
	"type": "object",
	"title": "Customer",
	"properties": {
		"id": {
			"type": "string",
			"readOnly": true
		},
		"firstName": {
			"type": "string",
			"title": "First Name"
		},
		"lastName": {
			"type": "string",
			"title": "Last Name"
		},
		"username": {
			"type": "string",
			"title": "Username"
		},
		"email": {
			"type": "string",
			"title": "Email Address",
			"pattern": "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$"
		},
		"locale": {
			"type": "string",
			"title": "Locale",
			"minLength": 2,
			"maxLength": 2,
			"default": "en"
		},
		"created": {
			"type": "string",
			"title": "Created Date",
			"readOnly": true
		}
	},
	"required": [
		"firstName",
		"lastName",
		"username",
		"email"
	]
}
```

> Applying the above JSON Schema example into a [JSON Editor](http://jeremydorn.com/json-editor/) will show how a web application could leverage this content.


## Productionalization

This section will provide details on what is typically needed in order to ensure an API is production ready.

1. Logging
1. Monitoring & Alerting
1. Reporting
1. API Documentation

Monitoring, alerting, and reporting can be built upon the logging solution.  Using Spring Cloud Gateway as a proxy for all APIs, the gateway can provide the logging solution.

### Logging

Example JSON log record that would pipe into logstash.
``` json
{
	"@timestamp": "2017-05-24T12:03:27.926Z",
	"cache": "VALIDATED",
	"took": 55,
	"request": {
		"path": "/customers/123456789",
		"method": "GET",
		"clientip": "92.168.124.59",
		"headers": {
			"Accept": "application/json",
			"host": "example.pcfbeta.io",
			"User-Agent": "curl/7.54.0"
		}
	},
	"response": {
		"code": 200,
		"headers": {
			"Content-Length": "100",
			"Content-Type": "application/json; charset=UTF-8",
			"Cache-Control": "s-max-age=300;stale-while-revalidate=600",
			"Vary": "x-version",
			"Age": "0",
			"Date": "Mon, 01 May 2017 13:08:38 GMT",
			"X-Application-Context": "spring-app:cloud:0",
			"x-vcap-request-id": "3f532d170112fc5b2a0b94fcbd6493b3",
			"x-resource": "CUSTOMER"
		}
	}
}
```

### Monitoring & Alerting

The following is a list of possible features that a Monitoring and Alerting solution would provide.

- Spikes in 4XX errors
- 5XX error count exceed tolerable thresholds
- Average response times exceed tolerable thresholds

### Reporting

Below is a table that contains an example of the data that would included as part of a report for an API.

Resource | Count | Method | Status | Cache | Average Response Time (ms) | 90th Percentile (ms)
--- | --- | --- | --- | --- | --- | ---
CUSTOMER | 1000 | GET | 200 | HIT | 5 | 10
CUSTOMER | 200 | GET | 200 | VALIDATED | 100 | 200
CUSTOMER | 100 | GET | 200 | MISS | 100 | 200
CUSTOMER | 5 | GET | 503 | MISS | 5 | 10
CUSTOMER | 1000 | POST | 200 | MISS | 100 | 200
CUSTOMER | 100 | POST | 409 | MISS | 50 | 100
CUSTOMER | 5 | POST | 500 | MISS | 50 | 100

### API Documentation

Generally defined using swagger or RAML.  "Try it out" features should be sent to a mock service rather than the actual service.  The API docs from each of the applications across the project ideally would be pulled together to be provided by a single dev portal.

---

The [sample spring boot application](https://github.com/pivotalservices/sample-rest-api-application.git) contains coding examples showing how some of these design aspects (such as error handling, JSON Schema, and versioning) can be implemented.
