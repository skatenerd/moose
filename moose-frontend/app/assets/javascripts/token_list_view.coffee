jQuery ->
  class window.TokenListView extends Backbone.View
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

