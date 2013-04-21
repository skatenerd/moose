jQuery ->
  class Token extends Backbone.Model
    #name, index

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
      """
      @

    unrender: =>
      $(@el).remove()

    events: 'click .delete': 'remove'

    remove: ->
      @model.destroy()

  class TokenListView extends Backbone.View
    el: $ 'body'
    initialize: ->
      _.bindAll @
      @collection = new List
      @collection.bind('add', @appendToken)
      @collection.bind('sync', @render)
      @collection.fetch()

    events: 'click #add_token_button': 'addToken'

    render: ->
      $('#tokens').html("")
      _.each(@collection.models, (token) =>
        @appendToken(token)
      )

    appendToken:(token) ->
      tokenView = new TokenView(model: token)
      tokenView.render()
      $('#tokens').append(tokenView.el)


    addToken: (e) ->
      foo = @collection.create({name: @$("#new_token_name")[0].value})

  listView = new TokenListView
