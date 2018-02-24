# clj-blogger

[![Build Status][travis-badge]][travis]
[![Clojars Project][clojars-badge]][clojars]
[![Tag][tag-badge]][tag]
[![Clojure version][clojure-v]](project.clj)

[![][logo]][logo-large]

*A Clojure library for the Google Blogger REST API*

## About

This is a simple REST (HTTP) client for Google's Blogger service. This library
was written against the following release of Blogger:
 * https://developers.google.com/blogger/docs/3.0/reference/


## Layout

The code layout does not match the Blogger REST API URLs. This would be a bit
awkward for a library with so few functions and a comparatively large number
of differing URLs (e.g., `/blogs/blogId/posts/postId/comments`, and
`/blogs/blogId/comments`; `/users/userId/blogs`,
`/users/userId/blogs/blogId/posts`, and `/blogs/blogId/posts`). Instead, we
have opted for a relatively flat namespace organization and have relied upon
code comments and sections to remove any ambiguity as to function and purpose.
Also, we opted simply for singular nouns for all of our namespaces, again, just
to keep things simple (allowing us to neatly avoid difficulties both semantic
and taxonomic). Were API functionality could easily have gone in one namespace,
but where we opted for another, we have left a "trail" (code comment) for the
user, letting them know where to go to get what they're looking for (ah, the
sharp corners of REST ...).


## Client

This is essentially a very thin wrapper around
[clj-http](https://github.com/dakrone/clj-http): we don't do anything clever on
top of it. Anything you'd normally pass to clj-http functions will work the
same with clj-blogger. All that's wrapped by the Clojure library are the
following:
 * the HTTP verb (which shows up in the Clojure API as a prefix to the function
   name, e.g. `get-posts` or `delete-post`; an exception is made for the `POST`
   HTTP verb: due to the contextual ambiguity, the `create-` prefix is used in this
   case)
 * the resource (URL)

All clj-blogger functions take two arguments:
 1. A map of options specific to the given Blogger API resource, and
 1. A map of options to pass to clj-http, allowing you complete control over
    the HTTP client behaviour.


## Usage

From the above notes (and a quick look at the source code), it should be fairly
clear how to use the API:

```
TBD
```


## License

Copyright © 2018, Clojure-Aided Enrichment Center

Distributed under the Apache License, Version 2.0.


<!-- Named page links below: /-->

[travis]: https://travis-ci.org/clojusc/clj-blogger
[travis-badge]: https://travis-ci.org/clojusc/clj-blogger.png?branch=master
[deps]: http://jarkeeper.com/clojusc/clj-blogger
[deps-badge]: http://jarkeeper.com/clojusc/clj-blogger/status.svg
[logo]: resources/images/Blogger-logo-small.png
[logo-large]: resources/images/Blogger-logo.png
[tag-badge]: https://img.shields.io/github/tag/clojusc/clj-blogger.svg
[tag]: https://github.com/clojusc/clj-blogger/tags
[clojure-v]: https://img.shields.io/badge/clojure-1.8.0-blue.svg
[jdk-v]: https://img.shields.io/badge/jdk-1.7+-blue.svg
[clojars]: https://clojars.org/clj-blogger
[clojars-badge]: https://img.shields.io/clojars/v/clj-blogger.svg
