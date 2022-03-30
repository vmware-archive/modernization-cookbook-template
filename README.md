# Cookbook Template

## Getting Started

Cookbooks are deployed as [Hugo](https://github.com/gohugoio/hugo) sites. They are driven by a hierarchical structure of markdown files.

This cookbook is additionally backed by a Spring Boot backend to provide search functionality and to serve the Hugo-generated static files.

### Initial Setup

* Install `hugo`
  - With homebrew (Mac): `brew update && brew install hugo`
  - Manual download and install: https://github.com/gohugoio/hugo/releases

* Clone this repo

* Run locally
``` 
$ ./localserver
``` 

* Access your cookbook here: http://localhost:1313/

* To use the Spring Boot-backed search locally, start the app via `./gradlew bootRun` and open it at http://localhost:8080.

### Add a new recipe

Create a new Markdown file inside the `content` folder. Use existing files for examples. 
