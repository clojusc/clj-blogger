(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      (slurp "resources/text/loading.txt"))
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m λ\u001B[m=> "))

(defproject clj-blogger "0.1.0-SNAPSHOT"
  :description "A Clojure library for the Google Blogger REST API"
  :url "https://github.com/clojusc/clj-blogger"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [cheshire "5.8.0"]
    [clj-http "3.7.0"]
    [org.clojure/clojure "1.9.0"]]
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
        [org.clojure/tools.namespace "0.2.11"]]}
    :lint {
      :plugins [
        [jonase/eastwood "0.2.5"]
        [lein-bikeshed "0.5.1"]
        [lein-kibit "0.1.6"]
        [venantius/yagni "0.1.4"]]}
    :test {
      :dependencies [
        [clojusc/ltest "0.3.0"]]
      :plugins [
        [lein-ancient "0.6.15"]
        [lein-ltest "0.3.0"]]
      :test-selectors {
        :select :select}}
    :docs {
      :dependencies [
        [clojang/codox-theme "0.2.0-SNAPSHOT"]]
      :plugins [
        [lein-codox "0.10.3"]
        [lein-marginalia "0.9.1"]]
      :codox {
        :project {
          :name "clj-blogger"
          :description "Customised, Stasis-based Static Site Generator"}
        :namespaces [#"^dragon\.(?!dev)"]
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
              "--name" "sockets"]]
    "build" ["with-profile" "+test" "do"
      ;["check-deps"]
      ["lint"]
      ["docs"]
      ["ubercompile"]
      ["clean"]
      ["uberjar"]
      ["clean"]
      ["ltest"]]})
