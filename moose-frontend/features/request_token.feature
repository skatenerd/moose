Feature: Request Token

  @javascript
  Scenario: I can request a token
    Given User "walter" exists
    And User "walter" knows about a token called "toilet"
    And I am logged in as "walter"
    When I request token "toilet"
    Then I possess token "toilet"
