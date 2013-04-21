class Token < ActiveRecord::Base
  has_many :user_tokens
  has_many :users, :through => :user_tokens
end
