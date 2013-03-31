var first = new WebSocket("ws://localhost:8080")
first.onmessage = function(m){
  alert("FIRST GOT:   " + m.data)
}
var second = new WebSocket("ws://localhost:8080")
second.onmessage = function(m){
  alert("SECOND GOT:  " + m.data)
}

request_token_message = JSON.stringify({action:"request", token: "abc"})
relinquish_token_message = JSON.stringify({action:"relinquish", token: "abc"})

first.onopen = function(){
  first.send("I AM WALTER")
}

second.onopen = function(){
  second.send("I AM BALLTO")
}

var resetTokens = function(){
  first.send(relinquish_token_message)
  second.send(relinquish_token_message)
}

setTimeout(function() {
  //alert("Requesting token as walter")
  first.send(request_token_message)
  //alert("Requesting token as ballto, walter will be notified of waiter")
  setTimeout(function(){
    second.send(request_token_message)
    setTimeout(function(){
      first.send(relinquish_token_message)
    }, 50)
  }, 50)
  //alert("Relinquishing token as walter, ballto should be granted")
  setTimeout(resetTokens, 5000)
}, 1000)

