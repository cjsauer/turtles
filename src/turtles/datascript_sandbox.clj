(ns turtles.datascript-sandbox
  (:require [datascript.core :as d]))

(def schema
  {:eid {:db/unique :db.unique/identity}})

(defn new-eid
  []
  (java.util.UUID/randomUUID))

(defn create-turtle
  [attrs]
  (let [db (d/create-conn schema)
        eid (new-eid)
        attrs* (assoc attrs :eid eid)]
    (alter-meta! db #(assoc % :self [:eid eid]))
    (d/transact! db [attrs*])
    db))

(defn self
  [turtle]
  (d/pull @turtle '[*] (:self (meta turtle))))

(def turtle1 (create-turtle {:coord [0 0]}))
(def turtle2 (create-turtle {:coord [0 10]}))
(def turtle3 (create-turtle {:coord [-5 12]}))

(defn current-time-seconds
  []
  (/ (System/currentTimeMillis) 1000))

(def fact1 {:origin [5 5]
            :speed 0.5
            :t (current-time-seconds)
            :fact {:pheremone 1}})

(defn fact-snapshot
  [{:keys [t speed] :as fact}]
  (let [dt (- (current-time-seconds) t)]
    (assoc fact :radius (* dt speed))))
