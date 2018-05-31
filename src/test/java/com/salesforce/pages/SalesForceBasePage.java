package com.salesforce.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.core.base.CoreBasePage;
import com.salesforce.util.SalesforceWebdriverBase;





public class SalesForceBasePage extends CoreBasePage {
	static WebDriver driver;
	
	public SalesForceBasePage() {
	super(driver =SalesforceWebdriverBase.getCurrentDriver());
		
}

}
