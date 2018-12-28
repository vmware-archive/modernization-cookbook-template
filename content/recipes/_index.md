+++
date = "2017-04-21T14:27:52-06:00"
draft = false
weight = 60
title = "Recipes"
pre ="<i class='fa fa-cutlery'></i>&nbsp;&nbsp;"
+++

## Adding a new recipe
A new recipe could be added just by creating a Markdown file with the recipe content under `content/recipes` directory. However, this is not a recommended method as it requires manual creation of the recipe header, e.g.

```
+++
categories = ["recipes"]
tags = ["foo"]
summary = "Recipe Summary"
title = "Distributed Tracing"
date = 2018-12-28T09:58:55-05:00
+++
```

It is easier to use the `hugo` command line tool to generate a new recipe with the header pre-filled:

```bash
hugo new recipes/a-new-recipe.md
```

It is important to specify `*.md` extension for the recipe file.

Review the header in the generated recipe to adjust the tags, summary and title.

If you need to add images to a recipe, put them to the `static/images` directory of the cookbook.

## Tips to write a good recipe
- Provide the context in which the recipe is applicable. People in your organization might face similar but not exactly same issue and providing a proper context will help them to understand applicability of the recipe.
- Clearly state the problem the recipe is trying to solve.
- If there are multiple similar problems consider putting solution to each of them to a separate recipe so that it is easier to consume.
- Check if there is already similar recipe in the cookbook and consider updating it instead of creating a new one.
- Avoid using project specific names, values and credentials in the code blocks. Remember that the recipe should make sense for other people in organization who might have never heard about your project.
- Specify language of the code block to enable color coding.
- If the recipe is project specific and is unlikely to be used by anybody else in the organization, consider tagging it as such and putting the disclaimer in the context section of the recipe.
- Avoid putting title of the recipe in its body as Hugo generates one from the *title* tag.
- Be consistent with tagging. Less is more in this case. Check the existing tags by opening `See All Tags` link from the sidebar.
- Stick to the letters and numbers in the tag names as Hugo uses them to generate URLs.
- Consider adding external references to the applicable documentation at the end of a recipe.