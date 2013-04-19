MooseFrontend::Application.routes.draw do
  get "/", to: 'tokens#index'
end
