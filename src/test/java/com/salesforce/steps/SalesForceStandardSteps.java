package com.salesforce.steps;

import com.salesforce.pagefactory.PageFactory;
import com.salesforce.pages.SalesForceBasePage;
import com.salesforce.util.SalesforceWebdriverBase;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

// Write code here that turns the phrase above into concrete actionss

public class SalesForceStandardSteps extends SalesForceBaseStep {

	PageFactory pageFactory = new PageFactory();
	
	SalesForceBasePage page;
	@Before
	public void setUp(Scenario s) throws Exception {

		SalesforceWebdriverBase.loadInitialURL();
	}

	@After
	public void tearDown(Scenario s) throws Exception {
		try {
			SalesforceWebdriverBase.closeBrowser();
			// page.resetAndWriteCaptureData(fileName);
		} finally {
		}
	}

	@Given("^user is on \"(.*?)\"$")
	public void user_is_on(String pageName) throws Throwable {
		page = pageFactory.initialize(pageName);
	}

	@When("^user enters the data in text box \"(.*?)\" \"(.*?)\"$")
	public void user_enters_the_below_data_in_text_box(String arg1, String arg2) throws Throwable {
	
		System.out.println("Argument1"+arg1);
		System.out.println("Argument2"+arg2);
		page.sendKeys(arg1, arg2);
	}

	@Then("^user click on the Button \"(.*?)\"$")
	public void user_click_on_the_Button(String objectName) throws Throwable {
		page.click(objectName);
	}

	@Then("^user verifies the page is displayed \"(.*?)\"$")
	public void user_verifies_the_page_is_displayed(String arg1) throws Throwable {
		page.verifyTitleContains(arg1);
	}

	@Then("^user verifies the texts are displayed \"(.*?)\" \"(.*?)\"$")
	public void user_verifies_the_texts_are_displayed(String objectName, String value) throws Throwable {
		page.verifyText(objectName, value);

	}

	@Then("^user click on the LinkTest \"(.*?)\"$")
	public void user_click_on_the_LinkTest(String objectName) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		page.click(objectName);
	}

	@Then("^user switches to frame \"(.*?)\"$")
	public void user_switches_to_frame(String arg1) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		page.switchToFrame(arg1);
	}

	@Then("^user click on the Enter$")
	public void user_click_on_the_Enter() throws Throwable {
		clickEnter();
	}

	@Then("^user select the value from the drop down \"(.*?)\" \"(.*?)\"$")
	public void user_select_the_value_from_the_drop_down(String logicalName, String value) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		page.selectValueFromDropdown(logicalName, value);
	}

	@Then("^user Verify the Image \"(.*?)\"$")
	public void user_Verify_the_Image(String arg1) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		page.ValidateImage(arg1);

	}

}