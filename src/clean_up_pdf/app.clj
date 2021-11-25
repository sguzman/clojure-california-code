(ns clean-up-pdf.app
  (:gen-class)
  (:require [clean-up-pdf.util :as util]
            [clojure.java.io :as io]))

(defn compose
  "Compose two functions"
  [f g]
  (fn [h] (f (g h))))


(defn ^:private get-dir
  "Get directory of text files"
  []
  "./resources/txt/")

(defn main
  "Main body of my business logic"
  [args]
  (util/log "Entered app")
  (def fs (drop 1 (file-seq (io/file "./resources/txt/"))))
  (def txt (map slurp fs))
  (println (nth txt 100)))
