jQuery ->
  class Token extends Backbone.Model
    defaults: "held" : false
    #name, index, subscribers

  class List extends Backbone.Collection
    model: Token
    url: "/tokens/"


  collection = new List
  synchronizer = new TokenSynchronizer(collection)
  subscriptionListView = new SubscriptionListView({collection: collection, connection: synchronizer.connection})
  heldTokenListView = new HeldTokenListView({collection: collection, connection: synchronizer.connection})
  listView = new TokenListView({collection: collection, connection: synchronizer.connection})
