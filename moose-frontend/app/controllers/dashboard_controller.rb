class DashboardController < ApplicationController

  before_filter :ensure_authenticated

  def watch
    @token_service_address = "ws://localhost:8080"
    @user_id = session[:user_id]
  end
end

