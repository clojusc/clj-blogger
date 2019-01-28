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


## Before Use

There are a few things to be aware of before deciding to use this library:

* Google Authentication
* Configuration
* Alpha Status

### Google Authentication

Because this library utilizes protected Google resources, you will need to
[set up OAuth2 access with Google][oauth2-setup]; note that this library only
supports using service accounts (thus avoiding the need to introduce a web
browser into the mix).

Once created, there will be an option to download your OAuth2 credential data
in JSON format -- do so. Save this to a file (e.g.,
`~/.google/blog-publisher-oauth2-creds.json`); you will use it when creating a
client (see below).


### Configuration

Instead of remembering to enter your Google blog ID every time you want to
publish content, you can create a JSON configuration file that contains an
associative array with the key `blog-id` and whose value is the blog ID of
the blog with which you want to work.

Note that if you will be repeatedly operating on Blogger resources with the
same `user-id`, `page-id`, or `post-id`, you may make those entries in the
config file as well, and they will be used on all appropriate requests.

You can save this to a file like `~/.google/blog.json` and use it when
creating a client (see below).


### Alpha Status

This is a brand-new library. I'm using it to publish updates to severl of my
own blogs. As such, I've started with the API calls that I need the most.
You mileage may vary. That being said, if there's something you'd like to
see added, feel free to open a ticket or even submit a pull request :-)

Here are the API functions that have been implemented:

* `get-blog`
* `get-pageviews`
* `get-posts`

These are the ones remaining:

* `get-blogs`
* `get-comment`
* `get-comments`
* `approve-comment`
* `delete-comment`
* `spam-comment` (rename to `hide-spam-comment`?)
* `remove-comment-content`
* `get-page`
* `get-pages`
* `add-page`
* `delete-page`
* `update-page`
* `get-post`
* `search`
* `add-post`
* `delete-post`
* `update-post`
* `publish-post`
* `unpublish-post`
* `get-user`
* `get-user-blog`
* `get-user-post`
* `get-user-posts`


## Layout

Note that the code layout for this project does not match the Blogger REST API
URLs. However, since none of the function names collide, a flat namespace is
provided for all API calls: `clojusc.blogger.api`. The implementations for the
protocol defined there are provided in sensible library namespaces under
`clojusc.blogger.api.impl.*`.


## Client

This is essentially a very thin wrapper around
[clj-http](https://github.com/dakrone/clj-http): we don't do anything clever on
top of it. Anything you'd normally pass to clj-http functions will work the
same with clj-blogger (in most cases, this will be the third argument to the
functions in this API).


## Documentation

Versioned Clojure client documentation is available here:

* [API Reference][api-docs]
* [Marginalia][margin-docs]


## Usage

From the above notes (and a quick look at the source code), it should be fairly
clear how to use the API. First, create a client:

```clj
(requre '[clojusc.blogger.api.core :as api])

(def c (api/create-client {:creds-file "creds.json"
                           :config-file "blog.json"}))
```


```clj
(api/get-blog c {:blog-id "2632822713760719202"})
```

You can also pass options to clj-http:

```clj
(api/get-blog c {:blog-id "2632822713760719202"} {:debug true})
```


### Partial Responses

clj-blogger supports asking the API for just a subset of the response data.
Simply pass the `:fields` option (per the [API partial response spec][part-resp])
For instance, if you'd only like to see the titles of all posts:

```clj
(api/get-posts c {:fields ["items(title)"]})
```


## License

Copyright © 2018-2019, Clojure-Aided Enrichment Center

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
[oauth2-setup]: https://developers.google.com/blogger/docs/3.0/using
[part-resp]: https://developers.google.com/blogger/docs/3.0/performance#partial-response
