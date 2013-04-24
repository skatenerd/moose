require 'active_record'
class User < ActiveRecord::Base
  has_many :user_tokens
  has_many :tokens, :through => :user_tokens
end
