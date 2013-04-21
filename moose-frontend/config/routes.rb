MooseFrontend::Application.routes.draw do
  get "/", to: 'dashboard#watch'
  resources :tokens
end
