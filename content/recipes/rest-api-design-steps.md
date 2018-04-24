+++
categories = ["recipes"]
tags = ["[foo]"]
summary = "Recipe Summary"
title = "REST API Design Steps"
date = 2018-04-23T16:30:30-05:00
+++

## When to Use This Recipe

This recipe should be followed when collaborating with client on a project that requires designing a new RESTful API.

## Overview

 When the event arises for the implementation of a RESTful API, the following steps could be followed to help drive out an overall design strategy.  In some cases, the project may entail multiple teams implementing multiple applications that include the creation of RESTful APIs.  Before implementation of APIs begin, a representative from the various teams across the project would ideally agree upon an overall API design strategy.  The goal being that at the conclusion of the project, all RESTful APIs implemented as part of the project work in a consistent fashion.

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

### Choose Media Types

#### Goal:
Come to an agreement on what the standard media types would be for the project.  In the majority of the cases, this would simply be `application/json`.

#### Notes:
Other media types occasionally can be useful such as:

- `application/x-www-form-urlencoded`
- `multipart/form-data`

Even though spring boot has nice support for it, using Hypermedia Types such as `application/hal+json` should be avoided.

### URI Design

#### Goal:
Establish the principles that would be used to define the URI for resources.

#### Notes:

> Use '-' to separate words.  If resource format is snake case, then use '_'.

> There are two basic types of resources.  Collection Resources (ie `/customers`) and Singular Resources (ie `/customers/{id}`).

> URIs should be described using nouns.  Plural nouns to describe collection resources.  Singular resources most often will be referenced via an identifier.  However, in the case where only one resource can exist, then use a singular noun.

> Traditionally a URI for orders would potentially something like `/apiname/context/v1/customers/{id}/orders`. More recently however, it is becoming more common to split the URIs into separate URIs such as `/customers` and `/orders`.

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

> Resource content generally is provided only for `POST`, `PUT`, and `PATCH` requests.  The HTTP spec supports content to be provided for `DELETE` requests, but this practice is generally discouraged.  `GET` requests can at times be a point of contention since it is somewhat unclear in the HTTP spec if `GET`s are allowed to have request content.  This issue tends to arise for APIs that provide complex searching functionality.

> Resource content provided in the request generally should match what is returned in the response.  The only exception is for attributes that are defined in the schema as read only.  It is discouraged to define attributes for the request that would never be returned in the response. In addition, the type of resource returned should be the type as what was provided.  For example, a `POST /customers/{id}` would be provided a customer resulting in a customer to be returned instead of a collection of customers.

> The general rule to use to determine when to return response content is that if the response content would match the request content, then no response content should be returned.  In this situation, a `204` status should be returned.  However, a single URI returning multiple successful response status should be avoided.  A good rule to follow is that if in doubt, then always return response content.

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

There are certainly a number of valid ways to design error response content.  General recommendations would be to a list of errors so that multiple errors could be returned if multiple aspects of the request failed validation.  Also, it can be beneficial for each error to have a `code` and `subcode` attribute. Having a `subcode` attribute can help mitigate the creation of a new version of the API since the creation of a new code value could be considered a non-backward compatible change.  A `description` may also be needed if a localized (based on the `accept-language` header) needs to be provided.

``` json
[
  {
    "code": "error-code-1",
    "subcode": "sub-error-code-1",
    "description": "Première erreur d'exemple"
  },
  {
    "code": "error-code-2",
    "subcode": "sub-error-code-2",
    "description": "Deuxième exemple d'erreur"
  }
]
```

**Resource Formatting**

When it comes to how a resource should be formatted, either it is given little to no thought or it becomes a subject of all out holy war. In essence, there are two competing models which are camelCase or snake_case.  The format that is typically chosen is more often than not the result of the language being used to produce the API.  Important to note that there is no standard so there really isn't any wrong choice. Snake case actually seems to becoming the more popular format amongst newer APIs.

``` json
{
  "id": "7877a21a721f0fe399dc6f1086a45892",
  "first_name": "Sam",
  "last_name": "Spurlock",
  "user_name": "sspurlock",
  "email": "sspurlock@exampleorg.com",
  "locale": "en",
  "created": "2009-04-15T00:50:04Z"
}
```

### Choose Methods

#### Goal:
Come to an agreement on the strategy to implement when defining the HTTP verbs to support for URIs.

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
200 | Service accepted the request or at least some portion of the request.<br>200 status is normally used for successful
201 | Resource created.  Location response header required to be returned.<br>If the state of the resource remains the same as what was provided in the request, then it is generally acceptable to return a 201 with no response content.  Otherwise, the 201 response should have the resource content returned;
204 | Successful response when no response content needs to be returned.  Most commonly would be returned for `DELETE` requests.  Would also be valid for `PUT` or `POST`, but is normally preferable for those requests to provide the resource as part of the response.

> Requests that were _partially_ accepted by the service would return a 200 or 201 status along with errors indicating which portions of the request were rejected.

**Validation Failure Responses**

Response Code | Notes
------------ | -------------
400 | The request was not structured correctly and did not conform to the resource schema.
401 | TBD
403 | TBD
404 | TBD
409 | The request was structured correctly, but was rejected by the server as a result of business rule validation.
405<br>406<br>415 | Invalid method, accept header, or content media type.  Spring boot framework handles all these out of box and no custom logic should be needed to support these.
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

> In general, the implementation of custom request headers should be rare.

**Response Headers**

> The implementation of custom response headers should be fairly common. Some common examples of custom response headers would be for things such as a *request UUID* and *resource type*.

> If hypermedia links would be needed provided by responses, the [link header](https://www.w3.org/wiki/LinkHeader) is an option worth consideration.

### Query Parameter Design

#### Goal:
The goal of this step is to ...

#### Notes:

> Query parameters are generally defined only for `GET` requests.  Defining query parameters for other methods is generally considered bad practice.  However, `POST`s that support query parameters so that request content need not be provided can help simplify web applications that consume API responses.

> In order for an API to remain intuitive and easy to learn, it is advantageous to refrain from implementing action query parameters.

> When pagination is needed for a collection resources, generally recommended to use `limit` and `offset` query parameters.

> Query parameters are typically optional.  Defining query parameters that are required for a request is generally considered bad practice.

### Define API Evolution Strategy

#### Goal:
The goal of this step is to determine how the APIs will evolve incrementally over sprint iterations.  Typically this would involve agreement on a versioning strategy.

> First step in determining a version strategy is to come to agreement on the definition of a non-backward compatible change.
A common approach is to adopt the [tolerant reader](https://martinfowler.com/bliki/TolerantReader.html) principle.  This can help mitigate

#### non-versioning strategy

> The non-versioning evolution entails always making enhancements to an API in a backwards compatible fashion.  In the event that this cannot be done, then a temporary version of the API is created and a process is initiated that would upgrade all consuming application to the latest version.  Once the process is completed, the legacy version is then removed.

> Ideally an API could start without a versioning mechanism in place and one would not be implemented until it is determined for sure that it is needed.

#### versioning strategy
TBD - details on why a URL versioning mechanism should be avoided and why a date based custom header is advantageous.


> The following is a comment from Fielding in regards to using a url versioning mechanism. <br>
**Roy T. Fielding** <br>
_@fielding_ <br>
The reason to make a real REST API is to get evolvability … a "v1" is a middle finger to your API customers, indicating RPC/HTTP (not REST)

---

## JSON Schema

TBD - Provide recommendations regarding how JSON schema could be leveraged.

``` text
GET /schemas/customers
```
``` json
{
	"$id": "https://example.com/schemas/customers",
	"type": "object",
	"title": "Customer",
	"properties": {
		"id": {
			"type": "string",
			"readOnly": true
		},
		"firstName": {
			"$id": "/properties/firstName",
			"type": "string",
			"title": "First Name"
		},
		"lastName": {
			"type": "string",
			"title": "Last Name",
			"default": ""
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

## Caching

TBD - Research private Caching Strategies

``` text
GET /customer/123

Cache-Control: s-max-age=300;stale-while-revalidate=600
Vary: x-version
Age: 24
```

## Productionalization

TBD - This section will provide details on what is typically needed in order to ensure an API is production ready.

* API Documentation

* Logging

* Monitoring

* Alerting

* Reporting
