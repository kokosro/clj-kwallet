# clj-kwallet

A Clojure library designed to communicate with [https://github.com/Kokosapiens/wallet] http api.

## Usage

`[org.clojars.kokos/clj-kwallet]`
```clojure
(ns example
    (:require [clj-kwallet :refer :all]))
```

(def account (create-account))

(def wallet (generate-wallet account))

(println
   (wallet nil get-balance "eth" 0))
;;will print the balance of address at position 0 of specified asset type

(println
   (wallet pr-str get-balance "eth" 0))
;;will print an edn string of the balance of address at position 0 of specified asset type

```

Wallet service must run before using the library.


## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
