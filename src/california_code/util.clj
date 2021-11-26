(ns california-code.util
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]
            [clojure.tools.logging :as log]))

(defn log
  "Log a message"
  [message & more]
  (log/info message more))

(defn ^:private compose
  "Take two functions and return a composed function"
  [f g]
  (fn [h]
    (f (g h))))


(defn get
  "Get site HTML"
  [url]
  (client/get url))
