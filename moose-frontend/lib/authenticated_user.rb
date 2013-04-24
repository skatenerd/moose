require 'bcrypt'

class AuthenticatedUser
  def self.create_if_valid(username, password)
    found_user = found_user(username)
    return unless found_user
    return found_user if passwords_match(found_user, password)
  end

  def self.found_user(username)
    User.where(username: username).first
  end

  def self.passwords_match(user, password)
    BCrypt::Password.new(user.hashed_password) == password
  end
end
