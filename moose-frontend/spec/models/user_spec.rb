require 'spec_helper'

describe User do
  describe ".create_unique" do
    it "creates if absent" do
      User.create_unique("foo", "bar").should_not be_nil
    end
    it "does not create if present" do
      User.create!(username: "exists")
      User.create_unique("exists", "bar").should be_nil
    end
  end
end

