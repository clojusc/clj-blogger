(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      ; (slurp "resources/text/loading.txt")
      )
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m =>\u001B[m "))

(defproject clj-blogger "0.2.0"
  :description "A Clojure library for the Google Blogger REST API"
  :url "https://github.com/clojusc/clj-blogger"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :exclusions [
    [com.amazonaws/aws-java-sdk-s3]]
  :dependencies [
    [buddy/buddy-sign "3.0.0"]
    [cheshire "5.8.1"]
    [clj-http "3.9.1"]
    [clojusc/twig "0.4.1"]
    [com.taoensso/timbre "4.10.0"]
    [org.clojure/clojure "1.10.0"]
    [org.clojure/data.xml "0.2.0-alpha6"]
    [org.clojure/data.zip "0.1.2"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :custom-repl {
      :repl-options {
        :init-ns clojusc.blogger.dev
        :prompt ~get-prompt
        :init ~(println (get-banner))}}
    :cli {}
    :dev {
      :source-paths ["dev-resources/src"]
      :dependencies [
        [org.clojure/tools.namespace "0.2.11"]]
      :plugins [
        [oubiwann/venantius-ultra "0.5.4-SNAPSHOT" :exclusions [org.clojure/clojure]]]}
    :lint {
      :plugins [
        [jonase/eastwood "0.3.5"]
        [lein-kibit "0.1.6"]]}
    :test {
      :plugins [
        [lein-ancient "0.6.15"]
        [lein-ltest "0.4.0-SNAPSHOT"]]
      :test-selectors {
        :select :select}}
    :docs {
      :dependencies [
        [clojang/codox-theme "0.2.0-SNAPSHOT"]]
      :plugins [
        [lein-codox "0.10.5"]
        [lein-marginalia "0.9.1"]]
      :codox {
        :project {
          :name "clj-blogger"
          :description "Customised, Stasis-based Static Site Generator"}
        :namespaces [#"^clojusc\.blogger\.(?!dev)"]
        :themes [:clojang]
        :output-path "docs/current"
        :doc-paths ["resources/docs"]
        :metadata {
          :doc/format :markdown
		  :doc "Documentation forthcoming"}}}}
  :aliases {
    "repl" ["with-profile" "+custom-repl,+test" "repl"]
    "ubercompile" ["with-profile" "+ubercompile" "compile"]
    "check-vers" ["with-profile" "+test" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+test" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    "lint" ["do"
      ["kibit"]
      ;["eastwood"]
      ]
    "ltest" ["with-profile" "+test" "ltest"]
    "docs" ["with-profile" "+docs,+test" "do"
      ["codox"]
      ["marg" "--dir" "docs/current"
              "--file" "marginalia.html"
              "--name" "clj-blogger"]]
    "build" ["with-profile" "+test" "do"
      ;["check-deps"]
      ["clean"]
      ["ubercompile"]
      ["lint"]
      ["uberjar"]
      ["clean"]
      ["ltest"]
      ["docs"]]})
