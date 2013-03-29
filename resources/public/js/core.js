var first = new WebSocket("ws://localhost:8080")
first.onmessage = function(m){
  alert("FIRST GOT:   " + m.data)
}

alert("foo")
var second = new WebSocket("ws://localhost:8080")
second.onmessage = function(m){
  alert("SECOND GOT:  " + m.data)
}

first.onopen = function(){
  first.send("walther")
}

second.onopen = function(){
  second.send("zimbabwe")
}

