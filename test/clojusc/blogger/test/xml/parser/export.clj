(ns clojusc.blogger.test.xml.parser.export
  (:require
    [cheshire.core :as json]
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    [clojusc.blogger.xml.parser.export :as export]
    [clojusc.twig :as logger]))

(logger/set-level! '[clojusc.blogger] :fatal)

(def sample-export "samples/export.xml")
(def draft-post "tag:blogger.com,1999:blog-8825992.post-2878203379063759109")
(def published-post "tag:blogger.com,1999:blog-8825992.post-2491940924950122045")
(def blog-zip (export/xml-resource->zip sample-export))

(deftest atom-ns
  (is (= :xmlns.http%3A%2F%2Fwww.w3.org%2F2005%2FAtom/thing
         (export/ns-tag :thing)))
  (is (= :xmlns.http%3A%2F%2Fpurl.org%2Fatom%2Fapp%23/thing
         (export/ns-tag export/atom-app-ns :thing))))

(deftest extract-xml-text
  (let [blog-post (export/get-entry blog-zip published-post)]
    (is (= published-post
           (export/extract-xml-text blog-post :id)))
    (is (= "Ubuntu 7.04 on iMac Core 2 Duo, 2G"
           (export/extract-xml-text blog-post :title)))
    (is (= 1344
           (count (export/extract-xml-text blog-post :content)))))
  (let [blog-post (export/get-entry blog-zip draft-post)]
    (is (= draft-post
           (export/extract-xml-text blog-post :id)))
    (is (= "Hello, Clojure: LFE nuzzling up to the JVM"
           (export/extract-xml-text blog-post :title)))
    (is (= 0
           (count (export/extract-xml-text blog-post :content))))))

(deftest extract-author
  (let [blog-post (export/get-entry blog-zip published-post)]
    (is (= "Duncan McGreggor"
           (:name (export/extract-author blog-post)))))
  (let [blog-post (export/get-entry blog-zip draft-post)]
    (is (= "Duncan McGreggor"
           (:name (export/extract-author blog-post))))))

(deftest extract-tags
  (let [blog-post (export/get-entry blog-zip published-post)]
    (is (= ["ubuntu" "apple" "hardware" "mac os x" "canonical"]
           (export/extract-tags blog-post))))
  (let [blog-post (export/get-entry blog-zip draft-post)]
    (is (= ["erjang" "erlang" "lfe" "clojure" "jvm" "lisp" "java"]
           (export/extract-tags blog-post)))))

(deftest extract-draft-bool
  (let [blog-post (export/get-entry blog-zip published-post)]
    (is (= false
           (export/extract-draft-bool blog-post))))
  (let [blog-post (export/get-entry blog-zip draft-post)]
    (is (= true
           (export/extract-draft-bool blog-post)))))

(deftest extract-url
  (let [blog-post (export/get-entry blog-zip published-post)]
    (is (= "https://oubiwann.blogspot.com/2007/08/ubuntu-704-on-imac-core-2-duo-2g.html"
           (export/extract-url
            blog-post "Ubuntu 7.04 on iMac Core 2 Duo, 2G"))))
  (let [blog-post (export/get-entry blog-zip draft-post)]
    (is (= nil
           (export/extract-url
            blog-post "Hello, Clojure: LFE nuzzling up to the JVM")))))
