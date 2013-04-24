class UsersController < ApplicationController
  def new
  end
  def create
    created_user = User.create_unique(params["username"], params["password"])
    if created_user
      session[:user_id] = created_user.id
      redirect_to "/"
    else
      redirect_to action: :new
    end
  end
end
