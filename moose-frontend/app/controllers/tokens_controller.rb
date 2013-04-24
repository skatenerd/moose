class TokensController < ApplicationController

  before_filter :ensure_authenticated

  def list
    user = User.find_by_id(session[:user_id])
    render(json: user.tokens)
  end

  def index
    user = User.find_by_id(session[:user_id])
    render(json: user.tokens)
  end

  def destroy
    user = User.find_by_id(session[:user_id])
    token = user.tokens.where(id: params[:id]).first
    token.destroy! if token
    render(json: {success: true})
  end

  def create
    user = User.find_by_id(session[:user_id])
    user.tokens.create!(name: params[:name])
    render(json: {success: true})
  end
end
