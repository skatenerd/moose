jQuery ->
  class window.SubscriptionListView extends Backbone.View
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
      @collection.where({subscribed: true, held: false}).forEach (token) =>
        @appendToken(token)


