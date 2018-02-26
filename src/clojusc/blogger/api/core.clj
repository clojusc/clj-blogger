(ns clojusc.blogger.api.core
  (:require
    [clojusc.blogger.api.impl.blog :as blog]
    [clojusc.blogger.api.impl.post :as post]
    [clojusc.blogger.auth :as auth]
    [clojusc.blogger.util :as util]))

(defrecord BloggerClient
  [endpoint
   config-file
   creds-file
   defaults
   token])

(defprotocol BloggerAPI
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  Blogs Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-blog [this] [this args] [this args httpc-opts]
    "Retrieves a blog. Either a `:blog-id` or a `:url` needs to be
    provided in the `args` map.")
  (get-blogs [this] [this args] [this args httpc-opts]
    "Retrieves a list of blogs. Requires `:user-id` be provided in the `args`
    map.")
  ;;------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  Comments Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-comments [this] [this args] [this args httpc-opts]
    "Retrieves comments. Requires `:blog-id` be provided in the `args` map. If
    a `:post-id` is also provided, returns just the comments for that post. If
    not, returns all comments for the blog.")
  (get-comment [this] [this args] [this args httpc-opts]
    "Retrieves one comment resource. Requires `:blog-id`, `:post-id`, and
    `:comment-id` be provided in the `args` map.")
  (approve-comment [this] [this args] [this args httpc-opts]
    "Marks a comment as approved. Requires `:blog-id`, `:post-id`, and
    `:comment-id` be provided in the `args` map.")
  (delete-comment [this] [this args] [this args httpc-opts]
    "Deletes a comment. Requires `:blog-id`, `:post-id`, and `:comment-id` be
    provided in the `args` map.")
  (spam-comment [this] [this args] [this args httpc-opts]
    "Marks a comment as spam, hiding it from the default comment rendering.
    Requires `:blog-id`, `:post-id`, and `:comment-id` be provided in the
    `args` map.")
  (remove-comment-content [this] [this args] [this args httpc-opts]
    "Removes the content of a comment. Requires `:blog-id`, `:post-id`, and
    `:comment-id` be provided in the `args` map.")
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  Pages Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-pages [this] [this args] [this args httpc-opts]
    "Retrieves the list of pages for a blog. Requires `:blog-id` be provided
    in the `args` map.")
  (get-page [this] [this args] [this args httpc-opts]
    "Retrieves one pages resource. Requires `:blog-id` and `:page-id` be
    provided in the `args` map.")
  (delete-page [this] [this args] [this args httpc-opts]
    "Deletes a page. Requires `:blog-id` and `:page-id` be provided in the
    `args` map.")
  (add-page [this] [this args] [this args httpc-opts]
    "Adds a page. Requires `:blog-id` be provided in the `args` map.")
  (update-page [this] [this args] [this args httpc-opts]
    "Updates a page. `:blog-id` and `:page-id` be provided in the `args` map.")
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  Posts Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-posts [this] [this args] [this args httpc-opts]
    "Retrieves a list of posts. Requires `:blog-id` be provided in the `args`
    map.")
  (get-post [this] [this args] [this args httpc-opts]
    "Retrieves one post. Requires `:blog-id`. Additionally, either `:post-id`
    or `:path` must be provided in the `args` map.")
  (search [this] [this args] [this args httpc-opts]
    "Searches for a post that matches the given query terms. Requires
    `:blod-id` and `:query` be provided in the `args` map. The vallue for
    `:query` is simply a search string, as you would type it in the search
    box for a Blogger-hosted site.")
  (add-post [this] [this args] [this args httpc-opts]
    "Adds a post. Requires `:blog-id` be provided in the `args` map.")
  (delete-post [this] [this args] [this args httpc-opts]
    "Deletes a post. Requires `:blog-id` and `:post-id` be provided in the
    `args` map.")
  (update-post [this] [this args] [this args httpc-opts]
    "Updates a post. Requires `:blog-id` and `:post-id` be provided in the
    `args` map.")
  (publish-post [this] [this args] [this args httpc-opts]
    "Publishes a post. Requires `:blog-id` and `:post-id` be provided in the
    `args` map.")
  (unpublish-post [this] [this args] [this args httpc-opts]
    "Moves a post back to the draft state. Requires `:blog-id` and `:post-id`
    be provided in the `args` map.")
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  Users Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;---------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-user [this] [this args] [this args httpc-opts]
    "Retrieves a user. Requires `:user-id` be provided in the `args` map.")
  ;;-----------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  BlogUserInfos Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;-----------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-user-blog [this] [this args] [this args httpc-opts]
    "Gets one blog and user info pair. Requires `:user-id` and `:blog-id` be
    provided in the `args` map.")
  ;;-------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  PageViews Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;-------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-pageviews [this] [this args] [this args httpc-opts]
    "Retrieve pageview stats for a Blog. Requires `:blog-id` be provided in the
    `args` map.")
  ;;-----------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;--  PostUserInfos Section  --;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;-----------------------------;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (get-user-post [this] [this args] [this args httpc-opts]
    "Gets one post and user info pair. Requires `:user-id`, `:blog-id`, and
    `:post-id` be provided in the `args` map.")
  (get-user-posts [this] [this args] [this args httpc-opts]
    "Retrieves a list of post and post user info pairs, possibly filtered. The
    post user info contains per-user information about the post, such as access
    rights, specific to the user. Requires `:user-id` and `:blog-id` `:post-id`
    be provided in the `args` map."))

(extend BloggerClient
        BloggerAPI
        (merge blog/behaviour
               post/behaviour))

(defn create-client
  "Constructor for the Blogger client. Optionally takes a map that may contain
  any of the `:blog-id`, `:post-id`, or `:user-id` keys and associated values.
  Values of these keys will be used in API calls if the call itself does not
  provide them."
  ([]
    (create-client {}))
  ([opts]
    (let [client (map->BloggerClient opts)]
      (assoc client :endpoint "https://www.googleapis.com/blogger/v3"
                    :defaults (util/read-json (:config-file opts))
                    :token (auth/get-token client)))))
