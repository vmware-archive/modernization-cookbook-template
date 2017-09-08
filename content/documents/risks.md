+++
categories = ["documents"]
date = "2017-06-22T14:38:17-04:00"
glyph = "fa-book"
summary = "Outlines possible risks affecting the delivery of the project."
tags = ["[risks]"]
title = "Risks"
taxonomy = ["MODERNIZATION"]
review_status = ["DONE"]
+++

1. Does the _High/Med/Low_ designation indicate impact or probability of occurring?
2. Consider adding a probability designation. For example, a high impact risk that is not likely to occur may not need to be mitigated
3. Not all risks need to be mitigated, consider adding a designation that states what action is to be taken to handle the risk: _Mitigate/Accept_
## Implementation

1. Not having isolation level (High)

   - Contract changes in enterprise API services might affect the functionality of the `application`

   - Release cycles of enterprise API services might affect `application` availability

   - Limited throughput of enterprise integration endpoints might affect `application` scalability

   - Accommodating multiple integration protocols in one application adds complexity

>Suggested mitigation: Implement UI integrations with enterprise APIs within an isolation layer


2. Absence of API documentation (High)

   - Knowledge about services functionality appears to be contained within a subset of people (tribal knowledge)

   - Hierarchical access to enterprise APIs service owners. Multiple levels of meetings need to be scheduled to get questions answered

   - Signatures and parameters can only be partially identified by debugging the `old application`.  Process is error prone, and time consuming

3. Not having automated notification about any enterprise services API outages (High)

4. Using `old application` to drive the consumption of enterprise API services (Medium)

   - Identified services might be outdated from business perspective

   - Identified services might be outdated from technical perspective

   - Identified services might not satisfy the business requirements of `application`

>Suggested mitigation: Add API signatures and argument validation rules to a SNAP analysis. Include smoke testing of API endpoints to connect with support teams Expand on existing service matrix


5. Dependency on emerging enterprise API services (High)

   - Absence of the roadmap.  Schedule of releases and features is not transparent

   - Uncertainty around ownership of enterprise services APIs

>Suggested mitigation:  Develop a strategy to make `application` feature development process and enterprise APIs change process mutually transparent.  Share the backlog, invite enterprise APIs service owners to Iteration Planning, Demos, and Retro meetings.

6. Uncertainty around environments (High)

   - Pipeline development strategy is affected

   - Resources provisioning schedule is affected

>Suggested mitigation:  Involve QA and Production release teams as well as data and service security audits.


7. Lack of strategy for future reuse of Isolation Layer services (Medium)

>Suggested mitigation: Expand on SNAP analysis of existing services to identify potential future consumers of the isolation layer.


## Test

8. Not having load tests during the implementation phase (High)

9. Not having target performance metrics for the access of enterprise APIs (Medium)

10. No tests to measure performance / edge cases of existing enterprise APIs (Medium)

11. No QA strategy will slow down delivery to production (Low)

>Suggested mitigation: Either obtain projected performance metrics, or run load tests to identify limitations of the architecture.  Involve client's QA team early and coordinate testing efforts with development team.


## Support

12. Not having potential owners of the `application` and associated enterprise APIs involved during development phase (High)

13. Lack of the process around change request management of enterprise API services (High)

14. Lack of the process to clearly identify enterprise API owners, release schedules and roadmaps (Medium)

15. Not having feature development lifecycle strategy defined  (Low)

>Suggested mitigation: Identify product owner(s), enterprise APIs owners, and potential consumers of the isolation layer to develop best possible support strategy
