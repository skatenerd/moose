class UserToken < ActiveRecord::Base
  belongs_to :user
  belongs_to :token
end

