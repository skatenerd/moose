jQuery ->
  class Token extends Backbone.Model
    queueLength: ->
      22
    #name, index, subscribers

  class List extends Backbone.Collection
    model: Token
    url: "/tokens/"

  class TokenView extends Backbone.View
    tagName: 'li'

    initialize: ->
      _.bindAll(@)
      @model.bind 'remove', @unrender

    render: =>
      $(@el).html """
        <span>#{@model.get 'name'}!</span>
        <span class="delete">DELETE</span>
        <span class="request">REQUEST</span>
      """
      @

    unrender: =>
      #send relinquish message
      $(@el).remove()

    events: {
      'click .delete': 'remove'
      'click .request': 'request'
    }

    remove: ->
      @model.destroy()

    request: ->
      @model.set({subscribed: true})
      @options.connection.send(JSON.stringify({action:"request", token: @model.get 'name'}))

  class TokenListView extends Backbone.View
    el: $ '#tokens'
    initialize: () ->
      _.bindAll @
      @collection.bind('add', @appendToken)
      @collection.bind('sync', @render)
      @collection.fetch()

    events: 'click #add_token_button': 'addToken'

    render: ->
      @$("#tokens_list").html("")
      _.each(@collection.models, (token) =>
        @appendToken(token)
      )

    appendToken:(token) ->
      tokenView = new TokenView(model: token, connection: @options.connection)
      tokenView.render()
      @$("#tokens_list").append(tokenView.el)

    addToken: (e) ->
      foo = @collection.create({name: @$("#new_token_name")[0].value})

  class SubscriptionListView extends Backbone.View
    el: $ '#subscriptions'
    initialize: () ->
      @collection.bind('change', @render)
      _.bindAll @
      @tokens = []

    appendToken:(token) ->
      tokenView = new SubscriptionView(model: token, connection: @options.connection)
      tokenView.render()
      @$("#subscriptions_list").append(tokenView.el)

    render: =>
      @$("#subscriptions_list").html("")
      @collection.where({subscribed: true}).forEach (token) =>
        @appendToken(token)

  class SubscriptionView extends Backbone.View
    tagName: 'div'

    initialize: ->
      _.bindAll(@)
      @model.bind 'remove', @unrender

    render: =>
      $(@el).html """
        <span>#{@model.get 'name'}!</span>
        <span class="subscribers">#{(@model.get 'subscribers') || 0}</span>
        <span class="held">#{(@model.get 'held') || false}</span>
        #{@relinquish_span()}
      """
      @

    relinquish_span: ->
      if(@model.get('held') == true)
        '<span class="relinquish">RELINQUISH</span>'
      else
        ""

    unrender: =>
      $(@el).remove()

    events: 'click .relinquish': 'relinquish'

    relinquish: ->
      #set held to false as well?
      @model.set({subscribed: false})
      @options.connection.send(JSON.stringify({action:"relinquish", token: @model.get 'name'}))

  class TokenSynchronizer
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


    send_it: () ->
      @connection.send(JSON.stringify({action:"request", token: "asdf"}))

    connect: ->
      conn = new WebSocket(@token_service_address)
      conn.onopen = () =>
        conn.send(@user_id)
        alert("open")
      conn.onmessage = @updateCollection
      conn


  collection = new List
  synchronizer = new TokenSynchronizer(collection)
  subscriptionListView = new SubscriptionListView({collection: collection, connection: synchronizer.connection})
  listView = new TokenListView({collection: collection, connection: synchronizer.connection})
