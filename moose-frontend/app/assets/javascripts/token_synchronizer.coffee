class window.TokenSynchronizer
  constructor: (collection) ->
    @collection = collection
    @user_id = $('#user_id').text()
    @token_service_address = $('#token_service_address').text()
    @connection = @connect()

  updateCollection: (message) =>
    parsed = JSON.parse(message.data)
    switch parsed.event
      when "grant" then @handle_grant(parsed)
      when "queue-length" then @update_queue_length(parsed)
      when "requested" then @handle_requested(parsed)
      else alert(parsed.event)

  handle_grant: (parsed_message) ->
    theToken = @collection.findWhere(name: parsed_message.token)
    theToken.set({subscribers: 0, held: true})

  handle_requested: (parsed_message) ->
    theToken = @collection.findWhere(name: parsed_message.token)
    subscriberCount = parsed_message["queue-length"]
    theToken.set({subscribers: subscriberCount, held: true})

  update_queue_length: (parsed_message) ->
    theToken = @collection.findWhere(name: parsed_message.token)
    subscriberCount = parsed_message["queue-length"]
    theToken.set({subscribers: subscriberCount})

  connect: ->
    conn = new WebSocket(@token_service_address)
    conn.onopen = () =>
      conn.send(@user_id)
      conn.onmessage = @updateCollection
      conn
    conn
