(ns california-code.app
  (:gen-class)
  (:require [california-code.util :as util]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]))

(def base "https://govt.westlaw.com")
(def arg "https://govt.westlaw.com/calregs/Browse/Home/California/CaliforniaCodeofRegulations?transitionType=Default&contextData=%28sc.Default%29")

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
  [arg]
  (util/log (str "Writing into cache " arg))
  (io/make-parents (build-path arg))
  (spit (build-path arg) (get-http arg))
  (get-http arg))

(defn ^:private -read-file
  "Read file and log it"
  [arg]
  (util/log (str "Reading file " (build-path arg)))
  (slurp (build-path arg)))

(def read-file (memoize -read-file))

(defn ^:private -call-if-miss
  "Call function with arg if cache miss"
  [arg]
  (util/log (str "Received argument " arg))
  (if (file-exists? (build-path arg))
    (read-file arg)
    (write-file arg)))


(def call-if-miss (memoize -call-if-miss))

(defn -snip
  "Turn string into enlive object"
  [string]
  (html/html-snippet string))

(def snip (memoize -snip))

(defn -select
  "Select a elements"
  [obj]
  (html/select obj [:div#co_contentColumn :a]))

(def select (memoize -select))

(defn compose
  "Compose two functions into one"
  [g f]
  (fn [h]
    (g (f h))))

(defn ^:private -build-func
  "Series of funcs that will be performed on data"
  []
  (def func1 (compose snip call-if-miss))
  (def func2 (compose select func1))
  func2)

(def func (memoize (-build-func)))

(defn build-url
  "Build full from paths"
  [path]
  (str base path))

(defn href
  "Get href"
  [obj]
  (html/attr-values obj :href))

(defn valid-str?
  "Is this a valid link (exclude Title 26.)"
  [link]
  (string/starts-with? link "/"))

(defn -first-href
  "Get href then get first"
  [obj]
  (first (href obj)))

(def first-href (memoize -first-href))

(defn valid?
  "Is this element valid?"
  [ele]
  (valid-str? (first-href ele)))

(defn -build-func2
  "Build func for sub data stuff"
  []
  (compose build-url first-href))

(def func2 (memoize (-build-func2)))

(def factorial
  (fn [n]
    (loop [cnt n
           acc 1]
      (if (zero? cnt)
       acc
       (recur (dec cnt) (* acc cnt))))))

(defn ^:private -op
  "Get HTML from URL and don't forget to cache"
  [arg]
  (map func2 (filterv valid? (func arg))))

(def op (memoize -op))

(def op-n
  "Get HTML from URL and recurse on result"
  (fn [n]
    (loop [cnt n data (list (op arg))]
      (if (zero? cnt)
        data
        (recur (dec cnt) (map op data))))))

(defn main
  "Main body of my business logic"
  [args]
  (util/log "Entered app")
  (op-n 3))
