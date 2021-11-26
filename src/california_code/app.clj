(ns california-code.app
  (:gen-class)
  (:require [california-code.util :as util]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]))

(def base "https://govt.westlaw.com/calregs/Browse/Home/California/CaliforniaCodeofRegulations?transitionType=Default&contextData=%28sc.Default%29")

(defn ^:private encode [to-encode]
  (.encodeToString (java.util.Base64/getEncoder) (.getBytes to-encode)))


(defn ^:private decode [to-decode]
  (String. (.decode (java.util.Base64/getDecoder) to-decode)))

(defn ^:private file-exists?
  "Does file exist?"
  [file]
  (.exists (io/as-file file)))

(defn ^:private -get-http
  "Get site HTML"
  [url]
  (get (client/get url) :body))

(def get-http (memoize -get-http))

(defn ^:private build-path
  "Build path for cache"
  [file]
  (str "./resources/cache/" (encode file) ".html"))

(defn ^:private write-file
  "Write func result and return result"
  [func arg]
  (util/log (str "Writing into cache " arg))
  (io/make-parents (build-path arg))
  (spit (build-path arg) (func arg))
  (func arg))

(defn ^:private -read-file
  "Read file and log it"
  [arg]
  (util/log (str "Reading file " (build-path arg)))
  (slurp (build-path arg)))

(def read-file (memoize -read-file))

(defn ^:private -call-if-miss
  "Call function with arg if cache miss"
  [func arg]
  (util/log (str "Received argument " arg))
  (if (file-exists? (build-path arg))
    (read-file arg)
    (write-file func arg)))


(def call-if-miss (memoize -call-if-miss))

(defn -snip
  "Turn string into enlive object"
  [string]
  (html/html-snippet string))

(def snip (memoize -snip))

(defn -select
  "Select a elements"
  [obj]
  (html/select obj [:a]))

(def select (memoize -select))

(defn op
  "Get HTML from URL and don't forget to cache"
  [url]
  (def var (call-if-miss get-http url))
  (println (map html/text (select var))))

(defn main
  "Main body of my business logic"
  [args]
  (util/log "Entered app")
  (op base))
