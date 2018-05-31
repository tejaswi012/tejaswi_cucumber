package com.salesforce.pages.common;

import org.openqa.selenium.By;


import com.salesforce.pages.SalesForceBasePage;

public class LoginPage extends SalesForceBasePage {
	public LoginPage() {
		super();
	

		addObject("Search", By.xpath(".//*[@id='q']"));
		addObject("Submit", By.xpath(".//*[@id='submit']"));
		addObject("SeleniumImage", By.xpath(".//*[@id='sidebar']/img"));
	
	
	}
}
