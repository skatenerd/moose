var walterChannel = new WebSocket("ws://localhost:8080")
walterChannel.onmessage = function(m){
  alert("WALTER GOT:   " + m.data)
}
var francineChannel = new WebSocket("ws://localhost:8080")
francineChannel.onmessage = function(m){
  alert("FRANCINE GOT:  " + m.data)
}

request_token_message = JSON.stringify({action:"request", token: "abc"})
relinquish_token_message = JSON.stringify({action:"relinquish", token: "abc"})

walterChannel.onopen = function(){
  walterChannel.send("I AM WALTER")
}

francineChannel.onopen = function(){
  francineChannel.send("I AM FRANCINE")
}

var resetTokens = function(){
  walterChannel.send(relinquish_token_message)
  francineChannel.send(relinquish_token_message)
}

setTimeout(function() {
  //alert("Requesting token as walter")
  walterChannel.send(request_token_message)
  //alert("Requesting token as ballto, walter will be notified of waiter")
  setTimeout(function(){
    francineChannel.send(request_token_message)
    setTimeout(function(){
      walterChannel.send(relinquish_token_message)
    }, 50)
  }, 50)
  //alert("Relinquishing token as walter, ballto should be granted")
  setTimeout(resetTokens, 5000)
}, 1000)

