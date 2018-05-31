package com.salesforce.steps;



import java.util.logging.Logger;

import com.core.util.CommonUtility;
import com.salesforce.util.SalesforceWebdriverBase;

;

public abstract class SalesForceBaseStep extends SalesforceWebdriverBase {

	 public CommonUtility commUtil = new CommonUtility();
	
	public Logger logger = Logger.getLogger(this.getClass().getName());
}
