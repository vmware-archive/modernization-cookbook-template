# pDocs

**pDocs** is a simple [hugo](http://gohugo.io/) theme for documentation.  
It is a modification of BootieDocs.

## CONTENTS

* [QUICKSTART](#quickstart)
* [OPTIONS](#options)
* [LIMITATION](#limitation)
* [DEPENDENCIES](#dependencies)
* [LICENSE](#license)

## QUICKSTART

1. `hugo new _index.md`
1. Edit `content/_index.md`

Then the content appears on top page.

## OPTIONS

You can customize the menu items in the header navigation bar by configuring `params.mainMenu` in your _config.toml_ (or _config.yaml_).

```
# example of config.toml
[params]
  mainMenu = ["about", "usage"]
```

All other options and usages are described at the documentation site -- http://key-amb.github.io/bootie-docs-demo/ .

## DEPENDENCIES

**pDocs** includes following libraries:

* [Bootstrap](http://getbootstrap.com/) v3.3.4 ... Well-known CSS framework.
* [jQuery](https://jquery.com/) v1.11.2 ... Requried by _Bootstrap_.
* [highlight.js](https://highlightjs.org/) v8.5 ... For syntax highlighting.

