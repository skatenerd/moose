jQuery ->
  class window.HeldTokenView extends Backbone.View
    tagName: 'div'

    initialize: ->
      _.bindAll(@)

    render: =>
      $(@el).html """
        <span>#{@model.get 'name'}!</span>
        <span class="subscribers">#{(@model.get 'subscribers') || 0}</span>
        <span class="held">#{(@model.get 'held') || false}</span>
        <span class="relinquish">RELINQUISH</span>
      """
      @

    events: 'click .relinquish': 'relinquish'

    relinquish: ->
      @model.set({subscribed: false, held: false})
      @options.connection.send(JSON.stringify({action:"relinquish", token: @model.get 'name'}))

