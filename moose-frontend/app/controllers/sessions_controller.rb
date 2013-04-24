require 'bcrypt'
require 'authenticated_user'

class SessionsController < ApplicationController
  def new
  end

  def create
    authenticated_user = AuthenticatedUser.create_if_valid(params["username"], params["password"])
    if authenticated_user
      session[:user_id] = authenticated_user.id
      redirect_to "/"
    else
      redirect_to action: :new
    end
  end

end
