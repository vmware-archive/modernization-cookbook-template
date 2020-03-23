# Template App Migration Dojo Cookbook Site

This is a [Hugo](https://github.com/gohugoio/hugo) site. It is driven by markdown files that can be generated using the commands provided in this document.

# What makes a good recipe cookbook
0. **Make it customer agnostic.** No customer or customer project names in the recipes. Do not name recipe files with customer or customer project names.
2. Start from this repository
 https://github.com/pivotalservices/modernization-cookbook-template
3. Prefer `markdown` over `asciidoc`. Definitely don't write recipes in doc or docx.
4. Use TOML instead of `JSON` or `YAML` in the frontmatter.
4. Categorize your recipe from one of the following by adding one of the categories in the taxonomy below this in the front-matter markup like `taxonomy = ["API-FIRST"]`
5. Put all markdown files in the `content/recipes` and `content/documents`
6. Put good camel case title in `title=xxx` front matter to be automatically picked up by landing pages
7. Add `taxonomy=["CLIENT"]` to exclude the rendering of the recipe/doc or add `review_status = ["DONE"]` to get it published externally.
8. If in doubt look at the recipes in this template repo.

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
18. **IDE-HACKS** "IDE (IntelliJ, Eclipse) specific short cuts/ tricks"
19. **PKS** "Recipes to do with PKS, Lift & Shift and AppTx".
20. **CLIENT** "Internal Only - Client specific recipes"
21. **PATTERN** Buckets of Recipes
22. **GENERAL** "General uncategorized recipes"


## Getting Started

### Initial Setup

- Install `hugo`
  - With homebrew (Mac): `brew update && brew install hugo`
  - Manual download and install: https://github.com/gohugoio/hugo/releases

### Initial Setup of New Customer Cookbook
The following steps will walk you through the process of creating a new GitHub repository with the modernization template and wipe out all the commit history associated to the modernization template so you can start fresh in your new cookbook repository.

1. Create a new private GitHub repository in `pivotalservices` with the name of the customer with cookbook suffix ex. `customer-cookbook`

1. Download the `new-cookbook` script from [here](https://github.com/pivotalservices/modernization-cookbook-template/releases/download/new-cookbook/new-cookbook)

1. Run the script providing name of the customer cookbook and path to the cookbook repository 
`new-cookbook customer-cookbook https://github.com/pivotalservices/customer-cookbook.git`

1. Update the README and clean up existing content as needed

1. Validate that you can build and run the cookbook locally via `./localserver`

1. Add the new cookbook repository to `app[0]` team on the Github so that it is picked up by the cookbook aggregation tool

**NOTE:** If you do not use the setup script or you run into errors building and running locally, ensure that you have the `submodule` for the hugo template.  

*Example from setup script with `--recursive` flag:* `git clone https://github.com/pivotalservices/modernization-cookbook-template.git --recursive $COOKBOOK_NAME`

*To manually add the submodule, execute:* `git submodule update --init --recursive`

### Run locally (default: `localhost:1313`)
```
./localserver
```

### Publish Instructions
```
./publish
cf push
```

### Add New Recipe
```
hugo new recipes/(title).md
```

## Updating the theme

This site uses the `hugo-theme-docdock` theme, which is stored as a git submodule
under the `themes` directory.

To pull the latest version of the theme, update your submodules: `git submodule update --remote` and commit the change to customer cookbook repo.

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
