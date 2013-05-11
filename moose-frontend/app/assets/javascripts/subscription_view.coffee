jQuery ->
  class window.SubscriptionView extends Backbone.View
    tagName: 'div'

    initialize: ->
      _.bindAll(@)

    render: =>
      $(@el).html """
        <span>#{@model.get 'name'}!</span>
        <span class="subscribers">#{(@model.get 'subscribers') || 0}</span>
      """
      @

