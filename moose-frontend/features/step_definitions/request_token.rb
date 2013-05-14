Given /^User "(.*)" exists$/ do |username|
  user = User.create_unique(username, "test")
end

Given /^User "(.*)" knows about a token called "(.*)"$/ do |username, token_name|
  User.find_by_username(username).tokens.create!(name: token_name)
end

Given /^I am logged in as "(.*)"$/ do |username|
  visit new_session_path
  fill_in("Username", with: username)
  fill_in("Password", with: "test")
  click_button("Submit")
end


When /^I request token "(.*)"$/ do |token|
  find("#request-#{token}").click
end

Then /^I possess token "(.*)"$/ do |token|
  page.text.should match(/#{token}.*RELINQUISH/)
end
