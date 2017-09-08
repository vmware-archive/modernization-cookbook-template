+++
date = "2017-04-21T14:27:52-06:00"
draft = false
weight = 60
title = "Recipes"
pre ="<i class='fa fa-cutlery'></i>&nbsp;&nbsp;"
+++

# What makes a good recipe
0. Make it customer agnostic
1. Make it customer agnostic
2. Start from https://github.com/pivotalservices/modernization-cookbook-template
3. Prefer markdown over asciidoc or doc or docx to write recipes.
4. Categorize your recipe from one of the following by adding one of the 17 categories in the taxonomy below this in the front-matter markup `taxonomy = ["API-FIRST"]`
5. Put all markdown files in the `content/recipes` and `content/documents`
6. Put good camel case title in `title=xxx` front matter to be automatically picked up by landing pages
7. Add `taxonomy=["CLIENT"]` to exclude the rendering of the recipe/doc or add `review_status = ["DONE"]` to get it published externally

# Taxonomy
1 REPLATFORMING  fa-cloud-upload "Best practices and tips for replatforming applications"
2 JavaEE fa-coffee "Patterns and techniques for porting JavaEE and J2EE component frameworks to Spring"
3 SPRING  fa-envira "Spring specific tips and code snippets"
4 MODERNIZATION fa-cubes "Tips and tricks to move your application the cloud nativity scale. Recipes on DDD and Event Storming."
5 CODE-ORGANIZATION fa-sitemap "One codebase tracked in revision control, many deploys. Explicitly declare and isolate dependencies. Best practices around code hygiene and general Java development idioms."
6 API-FIRST fa-cog "API first design and recipes that are all about API, Consumer driven API - Swagger, API gateways, OpenAPI spec."
7 TEST-DRIVEN-DEVELOPMENT fa-bolt "Test Driven Development. Unit, Integration and other testing recipes."
8 DESIGN-BUILD-RELEASE-RUN fa-play-circle "Strictly separate build and run stages. Continuos Integration, Pipeline and release management best practices."
9 CONFIG-CREDENTIALS-CODE fa-cogs "Store Configuration and Credentials in the environment. Injection of credentials and configuration into the application environment."
10 LOGS fa-area-chart "Treat logs as event streams"
11 BACKING-SERVICES-PORT-BINDING fa-server "Treat backing services as attached resources.Export services via port binding. Connecting and consuming all the backing data and messaging stores is covered here."
12 ADMIN-PROCESSES fa-black-tie "Run admin/management tasks as one-off processes. Batch processing."
13 STATELESS-CONCURRENCY fa-tasks "Execute the app as one or more stateless processes. Scale out via the process model"
14 TELEMETRY fa-tachometer "APM, Metrics and Monitoring."
15 AUTHn-AUTHz  fa-lock "Security related topics."
16 TROUBLESHOOTING fa-bug "Debugging and Troubleshooting of apps and modules during staging and running of apps"
17 DAY2-OPS fa-life-ring "Operating an app in production."
18 GENERAL fa-cutlery "General uncategorized recipes"
19 IDE-HACKS fa-magic "IDE (IntelliJ, Eclipse) specific short cuts/ tricks"
