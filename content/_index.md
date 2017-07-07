+++
date = "2017-04-21T15:28:50-06:00"
title = "Home Page"
description = "Default Home Page"

+++

## Default Home Page

To disable the homepage and remove icon from sidebar, set the `noHomeIcon` param in `/config.toml`:

``` toml
[params]
noHomeIcon = false
```


To redirect to the `/recipes` route by default, uncomment the following in `/layouts/partials/index.html`:
``` html
<meta http-equiv="refresh" content="0; url=/recipes" />
```