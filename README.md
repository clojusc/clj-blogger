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


## Google Authentication

Because this library utilizes protected Google resources, you will need to
[set up access with Google][oauth2-setup]. Their site and docs change
regularly, but look in their APIs & Services section for where you can create
credentials -- in particular, creating an OAuth client ID. Once created, there
will be an option to download in JSON format -- do so. Save this to a file
(e.g., `~/.google/blog-publisher-oauth2-creds.json`); you will use it when
creating a client.


## Configuration

Instead of remembering you enter you Google blog ID every time you want to
publish content, you can create a JSON configuration file that contains an
associative array with the key `blog-id` and whose value is the blog ID of
the blog with which you want to work.


## Layout

The code layout does not match the Blogger REST API URLs. However, since none of
the function names overlap, a flat namespace is provided for all API calls:
`clojusc.blogger.api`. The implementations for the protocol defined there are
provided in sensible library namespaces.


## Client

This is essentially a very thin wrapper around
[clj-http](https://github.com/dakrone/clj-http): we don't do anything clever on
top of it. Anything you'd normally pass to clj-http functions will work the
same with clj-blogger. All that's wrapped by the Clojure library are the
following:
 * the HTTP verb (which shows up in the Clojure API as a prefix to the function
   name, e.g. `get-posts` or `delete-post`; an exception is made for the `POST`
   HTTP verb: due to the contextual ambiguity, the `create-` prefix is used in this
   case), and
 * the resource (URL).

All clj-blogger functions take two arguments:
 1. A map of options specific to the given Blogger API resource, and
 1. A map of options to pass to clj-http, allowing you complete control over
    the HTTP client behaviour.


## Documentation

Versioned Clojure client documentation is available here:

* [API Reference][api-docs]
* [Marginalia][margin-docs]


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
[api-docs]: http://clojusc.github.io/clj-blogger/current/
[margin-docs]: http://clojusc.github.io/clj-blogger/current/marginalia.html
[oauth2-setup]: XXX
