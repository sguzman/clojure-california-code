(ns clean-up-pdf.core
  (:gen-class)
  (:require [clean-up-pdf.init :as init]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (init/payload args))
