require 'authenticated_user'
require 'spec_helper'

describe AuthenticatedUser do
  it "creates authenticated user" do
    User.create!(username: "walter", hashed_password: BCrypt::Password.create("secret"))
    AuthenticatedUser.create_if_valid("walter", "secret").should == User.first
  end

  it "finds the user we are looking for" do
    User.create!(username: "walter", hashed_password: BCrypt::Password.create("secret"))
    User.create!(username: "moses", hashed_password: BCrypt::Password.create("secret"))
    AuthenticatedUser.create_if_valid("moses", "secret").should == User.last
  end

  it "does not return user if password is wrong" do
    User.create!(username: "walter", hashed_password: BCrypt::Password.create("secret"))
    AuthenticatedUser.create_if_valid("walter", "wrong").should be_nil
  end

  it "does not die when cant find user" do
    AuthenticatedUser.create_if_valid("walter", "wrong").should be_nil
  end
end
