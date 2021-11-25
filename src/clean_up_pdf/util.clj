(ns clean-up-pdf.util
  (:gen-class)
  (:require [clojure.data.json :as json]
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

(defn ^:private get-auth-file
  "Slurp up auth file"
  []
  (slurp "./resources/auth.json"))

(def auth-file (memoize get-auth-file))

(defn ^:private auth-to-json
  "Convert auth json file from json"
  []
  (json/read-str (auth-file)))

(def auth-json (memoize auth-to-json))