(ns california-code.init
  (:gen-class)
  (:require [california-code.app :as app]
            [california-code.util :as util]))

(defn ^:private init
  "Init stuff"
  []
  (util/log "hi :)"))

(defn ^:private tini
  "End stuff"
  []
  (util/log "bye :("))

(defn ^:private body
  "Actual body of program"
  [args]
  (util/log args)
  (app/main args))

(defn payload
  "Entry for program execution"
  [args]
  (init)
  (body args)
  (tini))
