@foo
Feature:

  Background: A Calculator
    Given a calculator I just turned on

  Scenario:
  # Try to change one of the values below to provoke a failure
    When I add 4 and 5
    Then the result is 9
