## Getting Started

### Initial Setup

- Install `hugo`
  - With homebrew (Mac): `brew update && brew install hugo`
  - Manual download: https://github.com/gohugoio/hugo/releases
- Clone this repo with the `--recursive` flag to include the theme, which is a submodule. For convenience, add the name of cookbook now to match the name of the GitHub repository in `pivotalservices` :
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

### Add New Recipe
```
hugo new recipes/(title).md
```

### Add New Document
```
hugo new documents/(title).md
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
