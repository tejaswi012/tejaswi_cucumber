$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("src/test/resources/features/SaleforceTesting.feature");
formatter.feature({
  "line": 1,
  "name": "Verify the Login Functionality",
  "description": "",
  "id": "verify-the-login-functionality",
  "keyword": "Feature"
});
formatter.before({
  "duration": 9947553346,
  "status": "passed"
});
formatter.scenario({
  "line": 3,
  "name": "Verify Login with valid username and Invalid Password",
  "description": "",
  "id": "verify-the-login-functionality;verify-login-with-valid-username-and-invalid-password",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 4,
  "name": "user is on \"LoginPage\"",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "user enters the data in text box \"Search\" \"Selenium\"",
  "keyword": "When "
});
formatter.step({
  "line": 6,
  "name": "user Verify the Image \"SeleniumImage\"",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "LoginPage",
      "offset": 12
    }
  ],
  "location": "SalesForceStandardSteps.user_is_on(String)"
});
formatter.result({
  "duration": 169208277,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Search",
      "offset": 34
    },
    {
      "val": "Selenium",
      "offset": 43
    }
  ],
  "location": "SalesForceStandardSteps.user_enters_the_below_data_in_text_box(String,String)"
});
formatter.result({
  "duration": 195421185,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "SeleniumImage",
      "offset": 23
    }
  ],
  "location": "SalesForceStandardSteps.user_Verify_the_Image(String)"
});
formatter.result({
  "duration": 10070069018,
  "status": "passed"
});
formatter.after({
  "duration": 10474938,
  "status": "passed"
});
});