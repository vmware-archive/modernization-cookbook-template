# Template App Migration Dojo Cookbook Site

This is a [Hugo](https://github.com/spf13/hugo) site. It is driven by markdown files that can be generated using the commands provided in this document.


## Linux / Mac Instructions

### Run locally
```
./localserver
```

### Publish Instructions
```
./publish
cf push
```

### Add New inception document
```
hugo new inception/(title).md
```


### Add New pre-migration recipe
```
hugo new pre-migration/(title).md
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
