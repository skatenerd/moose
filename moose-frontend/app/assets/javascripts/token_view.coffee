jQuery ->
  class window.TokenView extends Backbone.View
    tagName: 'li'

    initialize: ->
      _.bindAll(@)
      @model.bind 'remove', @unrender

    render: =>
      $(@el).html """
        <span>#{@model.get 'name'}!</span>
        <button class="delete">DELETE</button>
        <button id="request-#{@model.get 'name'}" class="request">REQUEST</button>
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

