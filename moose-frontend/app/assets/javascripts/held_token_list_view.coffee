jQuery ->
  class window.HeldTokenListView extends Backbone.View
    el: $ '#held_tokens'
    initialize: () ->
      @collection.bind('change', @render)
      _.bindAll @

    appendToken:(token) ->
      tokenView = new HeldTokenView(model: token, connection: @options.connection)
      tokenView.render()
      @$("#held_tokens_list").append(tokenView.el)

    render: =>
      @$("#held_tokens_list").html("")
      @collection.where({subscribed: true, held: true}).forEach (token) =>
        @appendToken(token)


