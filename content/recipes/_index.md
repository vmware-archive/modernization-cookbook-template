+++
date = "2017-04-21T14:27:52-06:00"
draft = false
weight = 60
title = "Recipes"
pre ="<i class='fa fa-cutlery'></i>&nbsp;&nbsp;"
+++

# What makes a good recipe
0. Make it customer agnostic
2. Start from https://github.com/pivotalservices/modernization-cookbook-template
3. Prefer markdown over asciidoc or doc or docx to write recipes.
4. Categorize your recipe from one of the following by adding one of the 17 categories in the taxonomy below this in the front-matter markup `taxonomy = ["API-FIRST"]`
5. Put all markdown files in the `content/recipes` and `content/documents`
6. Put good camel case title in `title=xxx` front matter to be automatically picked up by landing pages
7. Add `taxonomy=["CLIENT"]` to exclude the rendering of the recipe/doc or add `review_status = ["DONE"]` to get it published externally

# Taxonomy
1. **REPLATFORMING** {{% icon fa-cloud-upload %}} "Best practices and tips for replatforming applications"
1. **JavaEE** {{% icon fa-coffee %}} "Patterns and techniques for porting JavaEE and J2EE component frameworks to Spring"
1. **SPRING** {{% icon fa-envira %}} "Spring specific tips and code snippets"
1. **MODERNIZATION** {{% icon fa-cubes %}} "Tips and tricks to move your application the cloud nativity scale. Recipes on DDD and Event Storming."
1. **CODE-ORGANIZATION** {{% icon fa-sitemap %}} "One codebase tracked in revision control, many deploys. Explicitly declare and isolate dependencies. Best practices around code hygiene and general Java development idioms."
1. **API-FIRST** {{% icon fa-cog %}} "API first design and recipes that are all about API, Consumer driven API - Swagger, API gateways, OpenAPI spec."
1. **TEST-DRIVEN-DEVELOPMENT** {{% icon fa-bolt %}} "Test Driven Development. Unit, Integration and other testing recipes."
1. **DESIGN-BUILD-RELEASE-RUN** {{% icon fa-play-circle %}} "Strictly separate build and run stages. Continuos Integration, Pipeline and release management best practices."
1. **CONFIG-CREDENTIALS-CODE** {{% icon fa-cogs %}} "Store Configuration and Credentials in the environment. Injection of credentials and configuration into the application environment."
1. **LOGS** {{% icon fa-area-chart %}} "Treat logs as event streams"
1. **BACKING-SERVICES-PORT-BINDING** {{% icon fa-server %}} "Treat backing services as attached resources.Export services via port binding. Connecting and consuming all the backing data and messaging stores is covered here."
1. **ADMIN-PROCESSES** {{% icon fa-black-tie %}} "Run admin/management tasks as one-off processes. Batch processing."
1. **STATELESS-CONCURRENCY** {{% icon fa-tasks %}} "Execute the app as one or more stateless processes. Scale out via the process model"
1. **TELEMETRY** {{% icon fa-tachometer %}} "APM, Metrics and Monitoring."
1. **AUTHn-AUTHz** {{% icon fa-lock %}} "Security related topics."
1. **TROUBLESHOOTING** {{% icon fa-bug %}} "Debugging and Troubleshooting of apps and modules during staging and running of apps"
1. **DAY2-OPS** {{% icon fa-life-ring %}} "Operating an app in production."
1. **GENERAL** {{% icon fa-cutlery %}} "General uncategorized recipes"
1. **IDE-HACKS** {{% icon fa-magic %}} "IDE (IntelliJ, Eclipse) specific short cuts/ tricks"
