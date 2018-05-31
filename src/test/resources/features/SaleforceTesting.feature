Feature: Verify the Login Functionality

  Scenario: Verify Login with valid username and Invalid Password
    Given user is on "LoginPage"
    When user enters the data in text box "Search" "Selenium"
    Then user Verify the Image "SeleniumImage"
    
