package com.salesforce.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(format = { "pretty", "html:target/cucumber", "json:target/cucumber-report.json" }, glue = {
		"com.salesforce.steps" }, features = { "src/test/resources/features/SaleforceTesting.feature" }, tags = {})
public class CucumberTestRunnerSuit {

}
