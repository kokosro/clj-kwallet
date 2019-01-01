(ns clj-kwallet.core
  (:require [clj-http.client :as http]
            [clojure.data.json :as json])
  (:gen-class))

(def ^:dynamic wallet-connection
  {:base "http://localhost:3000"
   :network "testnet"})


(defn- make-request
  [type url data]
  (try (let [response (http/request {:method type
                                     :url url
                                     :content-type :json
                                     :accept :json
                                     :body (json/write-str data)})]
         (json/read-str (:body response) :key-fn #'keyword))
       (catch Exception e (println e))))

(defn- make-url
  [account-hash action asset & [position network]]
  (apply str 
         (interpose "/"
                    [(:base wallet-connection)
                     account-hash
                      action
                      (or network
                          "mainnet")
                      asset
                      (or position 0)])))
(defn get-address
  [account-hash password asset & [position network]]
  (make-request :POST
                  (make-url account-hash
                      "address"
                      asset
                      position
                      (or network "mainnet"))
                  {:password password}))

(defn get-balance
  [account-hash password asset & [position network]]
  (let [x (make-request :POST
                (make-url account-hash
                          "balance"
                          asset
                          position
                          (or network "mainnet"))
                {:password password})
       ]
    x))

(defn send-amount
  [account-hash password asset position amount to-address & [ network ]]
  (make-request :POST
                (make-url account-hash
                          "send"
                          asset
                          position
                          (or network "mainnet"))
                {:to to-address
                 :amount (if (string? amount) amount
                             (format "%.8f" (try (* amount 1.0) (catch Exception e 0.0))))
                 :password password}))


(defn create-account
  []
  (let [url (str (:base wallet-connection)
                 "/generate")]
    (make-request :GET url {})))


(defn generate-wallet
  [{:keys [account password network]
    :or {network "mainnet"}}]
  (fn [ & [wrapper wallet-fn
           asset position
           args]]
    (let [network network
          position (or position 0)
          wrapper (or wrapper identity)
          wallet-fn (or wallet-fn (fn [& ags]
                                    (if (= (count ags) 0)
                                      (first ags) ags)))]
      (wrapper
       (apply wallet-fn
              (concat
               [account password
                asset
                position]
               args
               [network]))))))
