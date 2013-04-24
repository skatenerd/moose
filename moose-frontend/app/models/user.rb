require 'active_record'
class User < ActiveRecord::Base
  has_many :user_tokens
  has_many :tokens, :through => :user_tokens

  def self.create_unique(username, password)
    return unless User.where(username: username).empty?
    User.create!(username: username, hashed_password: BCrypt::Password.create(password))
  end
end
