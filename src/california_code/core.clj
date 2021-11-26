(ns california-code.core
  (:gen-class)
  (:require [california-code.init :as init]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (init/payload args))
