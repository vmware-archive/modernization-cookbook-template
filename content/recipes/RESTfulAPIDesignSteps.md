+++
categories = ["recipes"]
tags = ["[foo]"]
summary = "Recipe Summary"
title = "RESTfulAPIDesignSteps"
date = 2018-04-23T16:30:30-05:00
+++

# When to Use This Recipe

This recipe should be followed when collaborating with client to design a new REST API.

# Overview

 When the event arises for the implementation of a RESTful API, the following steps could be followed to help drive out an overall design strategy.  In some cases, the project may entail multiple teams implementing multiple applications that include the creation of RESTful APIs.  Before implementation of APIs begin, a representative from the various teams across the project would ideally agree upon an overall API design strategy.  The goal being that at the conclusion of the project, all RESTful APIs implemented as part of the project work in a consistent fashion.

 The first walkthrough of these steps may take some additional effort.  However, the intent of the initial walk through would not be to perform a large up front API design.  Rather, the goal would be to agree on an overall API Design strategy based primarily on the requirements that are known.  As requirements are refined or added or as additional APIs stories arise, these steps could be repeated as a way to incrementally refine the overall API design strategy for the project.  Subsequent walkthrough of these steps would be easier since the purpose would be to identify the aspects of the design strategy that need refining.

1. Choose Media Types
1. URI Design
1. Resource Design
1. Choosing Methods / Verbs
1. Choosing Response Status Codes
1. Using Headers
1. Query Parameter Design
1. API Evolution Strategies


# Steps

 Below are the steps that can be used to establish an API design strategy.  Each step contains the goal of the step along with recommendations, things to avoid, and examples.

In terms of examples to help provide clarity, we will apply the following concepts to two example resources.  The first being a `customer` resource that would have a one-to-many relationship with an `order` resource.

## Choosing Media Types

### Goal:
Come to an agreement on what the standard media types would be for the project.  In the majority of the cases, this would simply be `application/json`.

### Notes:
Other media types occasionally can be useful such as:

- `application/x-www-form-urlencoded`
- `multipart/form-data`

Even though spring boot has nice support for it, using Hypermedia Types such as `application/hal+json` should be avoided.

## URI Design

### Goal:
The goal of this step is to ...

### Notes:

**Collection Resource**

**Base Resource**

### Examples:

**Traditional URI Example**
```
/apiname/context/v1/customers
/apiname/context/v1/customers/{id}
/apiname/context/v1/customers/{id}/orders
/apiname/context/v1/customers/{id}/orders/{id}
```

**Modern URI Example**
```
/customers
/customers/{id}
/orders
/orders/{id}
```

## Resource Design

### Goal:
The goal of this step is to ...

### Notes:

**Collection Resources** <br>
Consideration should be given to the potential size of a collection resource.  For large collections, a pagination solution may be needed.

**Error Responses** <br>

### Examples:

## Choosing Methods

### Goal:
The goal of this step is to …

### Notes:

**GET**

> Used to retrieve a resource.  A `GET` request should never alter the state of of the resource and should be idempotent.

**POST**

> As per the HTTP spec, the POST method should be used to create a resource.  It is not http spec compliant, but it is common for a POST to be used to perform a merge update in lieu of `PATCH`.  `POST` more often than not should not be idempotent.  The first `POST` request may create a resource while subsequent requests would resort in a 4XX error.  `POST` to perform merge updates however, would be idempotent.

**PUT**

> Used to replace an existing resource.  If a resource needs to support nulling out optional attributes, then `PUT` could be used.

**DELETE**

> Used to remove a resource.  Normally `DELETE` would not be idempotent since the first request would be successful while subsequent requests would return a 404 error.

**PATCH**

> Is intended to be used to perform a merge update on a resource.  `PATCH` can also be used to assign null to optional attributes.  However, it is considered a best practice to follow the [patch spec](https://tools.ietf.org/html/rfc5789#section-2.1) and not to [Patch Like an Idiot](http://williamdurand.fr/2014/02/14/please-do-not-patch-like-an-idiot/).

**OPTIONS**

> Used in conjunction with adding support to the API for CORS.

## Choosing Response Status Codes

### Goal:
The goal of this step is to ...

### Notes:

**Successful Responses**

It is recommended that a given URI should should only support returning one 2XX response code.  In other words, a `POST /customers` should not return a 201 status for some requests while returning a 200 status for others.

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

Response Code | Notes
------------ | -------------
500 | The service encountered unexpected errors.  In general, it is the goal of an API to never return 500 errors.  Monitoring and Alerting mechanisms should be able look specifically for these types of errors in order to trigger actions that would be needed in order to resolve.  Essentially, a 500 error should be mean that the API is down and unable to respond to requests.
502<br>503<br>504 | These statuses should not be returned in a response for the API application.  These would be provided by a gateway or proxy layer.

## Using Headers

### Goal:
The goal of this step is to ...

### Notes:

**Request Headers** <br>
In general, the implementation of custom request headers should be rare.

**Response Headers**

* The implementation of custom response headers should be fairly common.
* If hypermedia links would be needed provided by responses, the [link header](https://www.w3.org/wiki/LinkHeader) is an option worth consideration.

## Query Parameter Design

### Goal:
The goal of this step is to ...

### Notes:
In order for an API to remain intuitive and easy to learn, it is advantageous to refrain from implementing action query parameters.

## API Evolution Strategies

### Goal:
The goal of this step is to ...

### non-versioning strategy
TBD - details on why implementing a versioning mechanism should be avoided if at all possible and how that could be accomplished.

### versioning strategy
TBD - details on why a URL versioning mechanism should be avoided and why a date based custom header is advantageous.

The following is a comment from Fielding in regards to using a url versioning mechanism.

> **Roy T. Fielding** <br>
> _@fielding_ <br>
> The reason to make a real REST API is to get evolvability … a "v1" is a middle finger to your API customers, indicating RPC/HTTP (not REST)

# Productionalization

TBD - This section will provide details on what is typically needed in order to ensure an API is production ready.

* Security

* API Documentation

* Logging

* Monitoring

* Alerting

* Reporting
