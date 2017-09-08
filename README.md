[![wings.concourse.ci](https://wings.concourse.ci/api/v1/teams/PCFS/pipelines/modernization-template/jobs/compile-site/badge)](https://wings.concourse.ci/teams/PCFS/pipelines/modernization-template)

# Template App Migration Dojo Cookbook Site

This is a [Hugo](https://github.com/gohugoio/hugo) site. It is driven by markdown files that can be generated using the commands provided in this document.

Hosted on PWS: https://modernization-template.cfapps.io/

# What makes a good recipe
0. Make it customer agnostic
1. Make it customer agnostic
2. Start from this repository https://github.com/pivotalservices/modernization-cookbook-template
3. Prefer markdown over asciidoc or doc or docx to write recipes.
4. Categorize your recipe from one of the following by adding one of the categories in the taxonomy below this in the front-matter markup `taxonomy = ["API-FIRST"]`
5. Put all markdown files in the `content/recipes` and `content/documents`
6. Put good camel case title in `title=xxx` front matter to be automatically picked up by landing pages
7. Add `taxonomy=["CLIENT"]` to exclude the rendering of the recipe/doc or add `review_status = ["DONE"]` to get it published externally.

## Taxonomy
1. **REPLATFORMING**  fa-cloud-upload "Best practices and tips for replatforming applications"
2. **JavaEE** "Patterns and techniques for porting JavaEE and J2EE component frameworks to Spring"
3. **SPRING** "Spring specific tips and code snippets"
4. **MODERNIZATION** "Tips and tricks to move your application the cloud nativity scale. Recipes on DDD and Event Storming."
5. **CODE-ORGANIZATION** "One codebase tracked in revision control, many deploys. Explicitly declare and isolate dependencies. Best practices around code hygiene and general Java development idioms."
6. **API-FIRST** "API first design and recipes that are all about API, Consumer driven API - Swagger, API gateways, OpenAPI spec."
7. **TEST-DRIVEN-DEVELOPMENT**  "Test Driven Development. Unit, Integration and other testing recipes."
8. **DESIGN-BUILD-RELEASE-RUN** "Strictly separate build and run stages. Continous Integration, Pipeline and release management best practices."
9. **CONFIG-CREDENTIALS-CODE**  "Store Configuration and Credentials in the environment. Injection of credentials and configuration into the application environment."
10. **LOGS** "Treat logs as event streams"
11. **BACKING-SERVICES-PORT-BINDING** "Treat backing services as attached resources.Export services via port binding. Connecting and consuming all the backing data and messaging stores is covered here."
12. **ADMIN-PROCESSES** "Run admin/management tasks as one-off processes. Batch processing."
13. **STATELESS-CONCURRENCY** "Execute the app as one or more stateless processes. Scale out via the process model"
14. **TELEMETRY** "APM, Metrics and Monitoring."
15. **AUTHn-AUTHz**  "Security related topics."
16. **TROUBLESHOOTING** "Debugging and Troubleshooting of apps and modules during staging and running of apps"
17. **DAY2-OPS** "Operating an app in production."
18. **GENERAL** "General uncategorized recipes"
19. **IDE-HACKS** "IDE (IntelliJ, Eclipse) specific short cuts/ tricks"
20. **CLIENT** Do NOT publish this recipe internally or externally in any aggregated cookbook. 

## Getting Started

### Initial Setup

- Install `hugo`
  - With homebrew (Mac): `brew update && brew install hugo`
  - Manual download: https://github.com/gohugoio/hugo/releases
- Clone this repo with the `--recursive` flag to include the theme, which is a submodule:
```
git clone https://github.com/pivotalservices/modernization-cookbook-template.git --recursive
```
Clone this branch:
```
git clone -b canonical-cookbook-template https://github.com/pivotalservices/modernization-cookbook-template.git --recursive
```
  - Alternatively, fetch the theme manually: `git submodule update --init --recursive`

### Run locally (default: `localhost:1313`)
```
./localserver
```

### Publish Instructions
```
./publish
cf push
```

### Add New Inception document
```
hugo new inception/(title).md
```

### Add New Application
```
hugo new applications/(title).md
```
### Add New Blocker
```
hugo new blockers/(title).md
```

Add `resolved=true` to metadata header to mark a blocker resolved.

### Add New Recipe
```
hugo new recipes/(title).md
```
### Add New Document
```
hugo new documents/(title).md
```

### Add New Lightweight Architecture Decision Record
```
hugo new decisions/(title).md
```

## Updating the theme

This site uses the `hugo-theme-docdock` theme, which is stored as a git submodule
under the `themes` directory.

To pull the latest version of the theme, simply update your submodules: `git submodule update --init --recursive`.

## Disable Home Page Icon
To disable the homepage and remove icon from sidebar, set the `noHomeIcon` param in `/config.toml`:

``` toml
[params]
noHomeIcon = false
```

## Redirect to Different URL
To redirect to the `/recipes` route by default, uncomment the following in `/layouts/partials/index.html`:
``` html
<meta http-equiv="refresh" content="0; url=/recipes" />
```
