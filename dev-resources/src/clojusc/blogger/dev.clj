(ns clojusc.blogger.dev
  (:require
    [cheshire.core :as json]
    [clj-http.client :as httpc]
    [clojure.tools.namespace.repl :refer [refresh]]
    [clojusc.blogger.api.core :as api]
    [clojusc.blogger.api.impl.blog :as blog]
    [clojusc.blogger.api.impl.comment :as comment]
    [clojusc.blogger.api.impl.page :as page]
    [clojusc.blogger.api.impl.post :as post]
    [clojusc.blogger.api.impl.user :as blog-user]
    [clojusc.blogger.auth :as auth]
    [clojusc.blogger.request :as request]
    [clojusc.blogger.routes :as routes]
    [clojusc.blogger.util :as util]))

(def home (.get (System/getenv) "HOME"))
(def config-dir (str home "/.google/starship-tools"))
(def creds-file (str config-dir "/blog-publisher-svc-creds.json"))
(def config-file (str config-dir "/blog.json"))
(def c (api/create-client {:creds-file creds-file
                           :config-file config-file}))
