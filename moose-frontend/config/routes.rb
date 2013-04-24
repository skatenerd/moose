MooseFrontend::Application.routes.draw do
  get "/", to: 'dashboard#watch'
  resources :tokens
  resources :sessions
  resources :users
end
