(ns clojusc.blogger.dev
  (:require
    [cheshire.core :as json]
    [clj-http.client :as httpc]
    [clojure.data.xml :as xml]
    [clojure.data.zip.xml :as zip-xml]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojure.tools.namespace.repl :refer [refresh]]
    [clojure.zip :as zip]
    [clojusc.blogger.api.core :as api]
    [clojusc.blogger.api.impl.blog :as blog]
    [clojusc.blogger.api.impl.comment :as comment]
    [clojusc.blogger.api.impl.page :as page]
    [clojusc.blogger.api.impl.post :as post]
    [clojusc.blogger.api.impl.user :as blog-user]
    [clojusc.blogger.auth :as auth]
    [clojusc.blogger.request :as request]
    [clojusc.blogger.routes :as routes]
    [clojusc.blogger.util :as util]
    [clojusc.blogger.xml.parser.export :as export]
    [clojusc.twig :as logger]))

(logger/set-level! '[clojusc.blogger] :info)

(def home (.get (System/getenv) "HOME"))
(def config-dir (str home "/.google/starship-tools"))
(def creds-file (str config-dir "/blog-publisher-svc-creds.json"))
(def config-file (str config-dir "/blog.json"))

(alter-var-root #'request/*print-errors* (constantly true))

(defn get-default-client
  []
  (if (and (.exists (io/file creds-file))
             (.exists (io/file config-file)))
    (api/create-client {:creds-file creds-file
                        :config-file config-file})
    (api/create-client)))

(comment
    (def x (export/xml-resource->zip "import/blog-01-22-2019.xml"))
    )
