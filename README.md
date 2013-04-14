# moose

A Clojure library designed to facilitate sharing of resources.

Consumers of this service choose a token to represent a "resource" (the toilet, an API key, a netflix account).  They can then request ownership of (or relinquish) the token.  Once a requested token is relinquished, the requestor will receive a JSON message.


## Usage

To see how it's used, run `lein run`, and visit localhost:8080.  Look at the javascript in the browser to confirm the following narrative:

Walter requests access to token "abc".  Walter is granted ownership of token "abc".  Francine then requests token "abc".  She is granted ownership of that toekn when Walter relinquishes it.

## TODO:

Clean up after a user closes websocket connection.  Figure out what to do in the case of a browser refresh.

When you request a token, the app should tell you how many people are also waiting for it.

When you own a requested token, the app should tell you how many people want it.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
