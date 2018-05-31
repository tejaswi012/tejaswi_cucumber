package com.core.base;

import static com.core.util.Constants.SCREENSHOT_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.imageio.ImageIO;

import org.apache.commons.lang.UnhandledException;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.core.util.Constants;
import com.core.util.DateUtil;
import com.core.util.PropertyReader;
import com.google.common.base.Function;


public abstract class CoreBasePage

{

	WebDriver driver;
	static int timeOutInSeconds;
	PropertyReader propertyReader = new PropertyReader();
	public HashMap<String, By> ObjRep = new HashMap<String, By>();
	public static HashMap<String, String> capturedData = new HashMap<String, String>();

	public static Logger logger = Logger.getLogger(CoreBasePage.class.getName());

	public CoreBasePage(WebDriver driver) {
		this.driver = driver;
		PropertyConfigurator.configure(Constants.loggerConfigPath);
		setTimeOut();
	}

	private int setTimeOut() {
		try {
			if (propertyReader.getApplicationproperty("timOutInSeconds") == null) {
				timeOutInSeconds = 30;
			} else {
				timeOutInSeconds = Integer.parseInt(propertyReader.getApplicationproperty("timOutInSeconds"));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Aplication  time out  is set to =" + timeOutInSeconds + "seconds");
		return timeOutInSeconds;

	}

	public void addObject(String str, By by) {
		ObjRep.put(str, by);
	}

	public WebElement getObject(String objectName) {
		By by = null;
		WebElement element = null;

		try {
			if (ObjRep.get(objectName) != null) {
				by = ObjRep.get(objectName);
			} else {
				logger.info(objectName + "is not present in the given page class, PLease check");
			}

		} catch (NullPointerException ne) {
			ne.printStackTrace();
			logger.info(objectName + "is not present int the page");
		}

		waitForDisplayed(objectName, timeOutInSeconds);
		if (driver.findElement(by) != null) {
			element = driver.findElement(by);
		} else {
			logger.info("Unable to do findElement opertation on object -" + objectName);
			Assert.fail("Unable to do findElement opertation on object -" + objectName);

		}

		return element;
	}

	public String getText(String objectName) {

		return getText(getObject(objectName));
	}

	public String getPopUp() {
		return getPopUp();
	}

	public String getTitle(String objectName) {

		if (objectName.equalsIgnoreCase("PageTitle")) {
			return getTitle();
		} else
			return getText(getObject(objectName)).trim();
	}

	public WebElement waitForDisplayed(String objectName, int timOutSeconds) {
		By by = ObjRep.get(objectName);
		return waitForDisplayed(driver.findElement(by), timOutSeconds);

	}

	/*----------------------------------------- Private Implementation --------------------*/

	private WebElement waitForVisibility(WebElement element) throws Error {

		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		element = wait.until(ExpectedConditions.visibilityOf(element));
		return element;
	}

	private String getText(WebElement element) {
		String text = "";
		try {
			waitForDisplayed(element, timeOutInSeconds);
			logger.info("text -" + element.getText());
			text = element.getText().trim().equals("") ? "" : element.getText().trim();

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}

		return text;
	}

	public String getPopup() {
		String popUptext = "";
		try {
			popUptext = driver.switchTo().alert().getText().trim();
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}

		return popUptext;
	}

	private String getTitle() {
		String text = "";
		try {
			text = driver.getTitle();
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
		return text;
	}

	private void setText(WebElement element, String text) {
		try {
			waitForDisplayed(element, timeOutInSeconds);
			element.clear();
			Thread.sleep(5000);
			System.out.println("Inside setTet after Clear");
			element.sendKeys(text);

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setTextByJS(WebElement element, String text) {
		try {
			isPageLoaded();
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].setAttribute(''value','" + text + "')", element);
			mouseHover();

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}

	}

	private boolean isDisplayed(WebElement element) throws Error {
		try {
			return element.isDisplayed();
		} catch (UnhandledException ar) {
			driver.switchTo().alert().dismiss();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean click(WebElement element) throws Error {
		try {
			waitForDisplayed(element, timeOutInSeconds);
			if (isDisplayed(element)) {
				element.click();
				return true;
			}
			return false;
		} catch (UnhandledException ar) {
			driver.switchTo().alert().dismiss();
			return false;
		} catch (Exception e) {
			return false;
		}

	}

	private boolean clickonTab(WebElement element) throws Error {
		try {
			waitForDisplayed(element, 60);
			focusOn(element);
			if (isDisplayed(element)) {
				element.click();
				return true;
			}
			return false;
		} catch (UnhandledException ar) {
			driver.switchTo().alert().dismiss();
			return false;
		} catch (Exception e) {
			return false;
		}

	}

	private void focusOn(WebElement element) {
		try {

			Actions actions = new Actions(driver);
			actions.moveToElement(element).perform();
			Thread.sleep(5000);
		} catch (UnhandledException ar) {
			driver.switchTo().alert().dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void moveToElement(WebElement element) {
		try {
			waitForDisplayed(element, timeOutInSeconds);
			Actions action = new Actions(driver);
			action.moveToElement(element).click().build().perform();
		} catch (UnhandledException ar) {
			driver.switchTo().alert().dismiss();
		}
	}

	private void mouseHover() {
		Robot robot;
		try {
			Thread.sleep(1000);
			robot = new Robot();
			robot.mouseMove(0, 0);
			Thread.sleep(1000);
			robot.mouseMove(100, 100);
			Thread.sleep(1000);
			robot.mouseMove(0, 0);
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void focusAndClick(WebElement element) {
		try {
			waitForElementClickable(element);
			Actions actions = new Actions(driver);
			Action action = actions.click(element).build();
			action.perform();
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}

	}

	private void clickByJS(WebElement element) {

		try {
			waitForDisplayed(element, timeOutInSeconds);
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click()", element);
			executor = null;
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();

		}

	}

	private void setTextByInnerHTML(WebElement element, String value) {

		try {
			waitForDisplayed(element, timeOutInSeconds);
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].innerHTML ='<h1>" + value + "</h1>'", element);
			executor = null;
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
	}

	private String getTextByJS(WebElement element) {
		String value = "";
		try {
			isPageLoaded();
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			value = executor.executeScript("return argument[0].text", element).toString();
			System.out.println("value" + value);

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
		return value;
	}

	private WebElement waitForEnabled(final WebElement webElement) {
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
		return webDriverWait.withMessage("Element is not loaded in specified timeout")
				.ignoring(WebDriverException.class).until(new Function<WebDriver, WebElement>() {
					@Override
					public WebElement apply(WebDriver webDriver) {
						return webElement.isEnabled() ? webElement : null;
					}
				});

	}

	public void waitAndSwitchFrameAvailable(By by) {
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
	}

	private WebElement waitForDisplayed(final WebElement webElement, int timeOutInSeconds) {
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
		return webDriverWait.withMessage("Element is not loaded in specific timeout").ignoring(WebDriverException.class)
				.until(new Function<WebDriver, WebElement>() {
					@Override
					public WebElement apply(WebDriver webDriver) {
						return webElement.isDisplayed() ? webElement : null;
					}
				});

	}

	private WebElement waitForValuesToLoadinDropdown(final WebElement webElement, int timeOutInSeconds) {
		final Select droplist = new Select(webElement);
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeOutInSeconds, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		return wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver d) {
				return (!droplist.getOptions().isEmpty() ? webElement : null);
			}
		});

	}

	public WebElement fluentWait(final WebElement webElement) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeOutInSeconds, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		return wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver webDriver) {
				return webElement.isDisplayed() ? webElement : null;
			}
		});
	}

	public WebElement fluentWait(final String xpath) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeOutInSeconds, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		return wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver webDriver) {
				return driver.findElement(By.xpath(xpath)) != null ? driver.findElement(By.xpath(xpath)) : null;
			}
		});

	}

	protected WebElement waitForElementToAppear(final By locator) {
		int pollingTime = 5;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeOutInSeconds, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);

		WebElement element = null;
		try {
			element = wait.until(new Function<WebDriver, WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(locator);
				}
			});
		} catch (TimeoutException e) {
			try {
				driver.findElement(locator);
			} catch (NoSuchElementException renamedErrorOutPut) {
				renamedErrorOutPut.printStackTrace();
				renamedErrorOutPut.addSuppressed(e);
				throw renamedErrorOutPut;
			}
			e.addSuppressed(e);
		}

		return element;
	}

	private boolean waitForTitleDisplay(String title, int timeOutInSeconds) {
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
		return webDriverWait.until(ExpectedConditions.titleContains(title));
	}

	private void waitForElementClickable(WebElement element) {
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
	}

	private WebElement waitForClickable(final WebElement webElement, int timeOutInSeconds) {
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
		return webDriverWait.withMessage("Element is not loaded in specified timeout")
				.ignoring(WebDriverException.class).until(new Function<WebDriver, WebElement>() {
					@Override
					public WebElement apply(WebDriver webDriver) {
						return webElement.isEnabled() && webElement.isDisplayed() ? webElement : null;
					}
				});
	}

	private boolean waitMillSecsForDisplay(WebElement element, int iWaitSeconds) throws Exception {
		final long threadSleep = 1000;
		for (int second = 0; second < threadSleep; second++) {
			if (second < iWaitSeconds) {
				try {
					if (element.isDisplayed()) {
						if (element.getSize().getWidth() > 0) {
							return true;
						}
					}
				} catch (Exception e) {

				}
				Thread.sleep(1);
			} else {
				return false;
			}
		}
		return false;
	}

	private boolean waitForNotDisplayed(WebElement element, int iWaitSeconds) throws Exception {
		final long threadSleep = 1000;
		for (int second = 0; second < threadSleep; second++) {
			try {

				if (element.isDisplayed()) {

				}
				if (element.getSize().getWidth() == 0) {
					return true;
				}

			} catch (Exception e) {
				return true;
			}
			Thread.sleep(threadSleep);
		}
		return false;
	}

	private String getSelectedValueFromDropDown(WebElement element) {

		String text = "";
		try {
			waitForDisplayed(element, timeOutInSeconds);
			Select select = new Select(element);
			text = select.getFirstSelectedOption().getText();
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
		return text;
	}

	private void selectValueFromDropDown(WebElement element, String value) {

		try {

			waitForDisplayed(element, timeOutInSeconds);
			Select select = new Select(element);
			select.selectByVisibleText(value);

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
	}

	private void waitAndselectValueFromDropDown(WebElement element, String value) {
		try {

			waitForDisplayed(element, timeOutInSeconds);
			waitForValuesToLoadinDropdown(element, timeOutInSeconds);
			Select select = new Select(element);
			select.selectByVisibleText(value);

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
	}

	private void selectValueFromDropDownByIndex(WebElement element, int index) {

		try {
			waitForDisplayed(element, timeOutInSeconds);
			Select select = new Select(element);
			select.selectByIndex(index);
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
	}

	public void selectMultipleItemsInList(LinkedHashMap<String, String> mapVal) {

		try {
			String value = null;
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			WebElement element = getObject(mapVal.get("LogicalName"));
			Select select = new Select(element);
			if (select.isMultiple()) {
				System.out.println("Element is multiple selected");
				while (newEntries.hasNext()) {
					Map.Entry<String, String> newEntry = newEntries.next();
					if (newEntry.getKey().contains("Value")) {
						value = newEntry.getValue();
						logger.info("Value - " + value);
					}
					clickControl();
					select.selectByValue(value);
				}
			} else {
				logger.info("Multiple Select is not enable for given drop down");
			}

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().accept();
		}

	}

	private void selectMultipleItemsInAList(LinkedHashMap<String, String> mapVal) {

		String value = null;
		String logicalName = null;
		ArrayList<String> valueList = new ArrayList<String>();
		try {
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();
				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value - " + value);
					valueList.add(value);
				}

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("logicalName" + logicalName);
				}
			}
			WebElement element = getObject(logicalName);
			final Select dropdown = new Select(element);
			final List<WebElement> options = dropdown.getOptions();
			final Actions builder = new Actions(driver);
			final boolean isMultiple = dropdown.isMultiple();
			if (isMultiple) {

				dropdown.deselectAll();
				builder.keyDown(Keys.CONTROL);

				for (String textOption : valueList) {
					for (WebElement option : options) {
						final String optionText = option.getText().trim();
						if (optionText.equalsIgnoreCase(textOption)) {
							if (valueList.size() > 1) {
								if (!option.isSelected()) {
									builder.click(option);
									builder.keyUp(Keys.CONTROL).build().perform();
								}
							} else {
								option.click();
							}
							break;
						}
					}
				}

			} else {
				logger.info("Multiple Select is not enable for given drop down");
			}
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}

	}

	private boolean Selected(WebElement element) {
		boolean status = false;

		try {
			status = element.isSelected();
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
		return status;
	}

	private void selectRadioButton(WebElement element) {
		try {
			if (!element.isSelected()) {
				element.click();
			}
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}

	}

	private boolean verifyCheckBoxStatus(WebElement element) {
		boolean status = false;

		try {
			waitForDisplayed(element, timeOutInSeconds);
			status = element.isSelected() || element.getAttribute("checked") != null;

		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
		return status;
	}

	private void selectCheckBox(WebElement element, String value) {

		try {
			waitForDisplayed(element, timeOutInSeconds);
			if (value.equals("checked")) {
				if (!element.isSelected() || element.getAttribute("checked") != null) {
					element.click();
				} else {
					if ((element.isSelected() || element.getAttribute("checked") != null)) {
						element.click();
					}
				}
			}
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
	}

	private void scrollToView(WebElement element) {
		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
	}

	private void switchToFrame(WebElement element) {

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		waitForVisibility(element);
		driver.switchTo().frame(element);
	}

	private void switchToDefault() {
		driver.switchTo().defaultContent();
	}

	private void close() {
		driver.quit();
	}

	private List<WebElement> getAllElements(WebElement element, String tagName) {
		List<WebElement> elementList = null;
		try {
			waitForDisplayed(element, timeOutInSeconds);
			elementList = element.findElements(By.tagName(tagName));
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
		return elementList;
	}

	private String getValueFromTextBox(WebElement element) {
		waitForDisplayed(element, timeOutInSeconds);
		return element.getAttribute("value").trim();

	}

	private String getValuebyAttribute(WebElement element, String attType) {
		waitForDisplayed(element, timeOutInSeconds);
		return element.getAttribute(attType).trim();
	}

	private void verifyElementByAttribute(WebElement element, String attType) {
		waitForDisplayed(element, timeOutInSeconds);
		Assert.assertTrue("Element is " + attType, element.getAttribute(attType).trim().equals("true"));
	}

	private boolean getElementStatusByAttribute(WebElement element, String attType) {
		waitForDisplayed(element, timeOutInSeconds);

		if (element.getAttribute(attType).trim() != null) {
			return true;
		}

		return false;
	}

	private void clickByRobot(WebElement element) {
		Robot robot;
		try {
			robot = new Robot();

			Point point = element.getLocation();
			System.out.println("X Position:" + point.x);
			System.out.println("Y Position:" + point.y);
			robot.mouseMove(point.x, point.y);

		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void clickPopUpByRobot() {
		Robot robot;

		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

	public void clickEnterByRobot() {
		Robot robot;
		for (int i = 0; i < 3; i++) {
			try {
				logger.info("Attempt - " + i + 1);
				robot = new Robot();
				Thread.sleep(3000);
				robot.keyPress(KeyEvent.VK_ENTER);
				Thread.sleep(3000);
				robot.keyRelease(KeyEvent.VK_ENTER);
			} catch (AWTException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void clickControl() {
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			// Thread.sleep(3000);
			// robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

	public void clickEnter() {
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(3000);
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void clickEnterByDriver() {
		final Actions builder = new Actions(driver);
		builder.sendKeys(Keys.ENTER).build().perform();
	}

	public void clickCancelByDriver() {
		final Actions builder = new Actions(driver);
		builder.sendKeys(Keys.TAB).build().perform();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		builder.sendKeys(Keys.ENTER).build().perform();
	}

	public void clickCancel() {
		Robot robot;
		try {
			robot = new Robot();
			Thread.sleep(3000);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
			Thread.sleep(3000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();

		}
	}

	public void clickAtByActionMove(WebElement element) {
		int x = element.getLocation().getX();
		int y = element.getLocation().getY();

		Actions actions = new Actions(driver);
		isDisplayed(element);
		Action action = actions.moveByOffset(x, y).sendKeys(Keys.SPACE).build();
		action.perform();
	}

	public void acceptAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 2);
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.accept();
		} catch (Exception e) {

		}
	}

	public void acceptAlertUsingJs(String msg) {
		try {
			((JavascriptExecutor) driver).executeScript("window.confirm=function(" + msg + ") {return true}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelAlertUsingJS(String msg) {
		try {
			((JavascriptExecutor) driver).executeScript("window.confirm=function(" + msg + ") {return false}");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void dismissAlert() {
		try {

			WebDriverWait wait = new WebDriverWait(driver, 2);
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			logger.info("Alert Present-" + alert.getText());
			alert.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException ex) {

			return false;
		}
	}

	public HashMap<String, String> captureTextBoxValue(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			if (getValueFromTextBox(logicalName) == null) {
				capturedData.put(value, "");
			} else {
				capturedData.put(value, getValueFromTextBox(logicalName));
			}
		}
		System.out.println(capturedData);
		return capturedData;
	}

	public HashMap<String, String> captureText(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			logger.info("Text from UI -" + getText(logicalName) + "|");
			if (getText(logicalName) == null || getText(logicalName).equals("") || getText(logicalName).isEmpty()) {
				capturedData.put(value, "");
			} else {
				capturedData.put(value, getText(logicalName));
			}
			System.out.println("Captured Data:" + capturedData);
		}

		return capturedData;
	}

	public HashMap<String, String> captureDropDownValue(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			if (getSelectedValueFromDropDown(logicalName) == null) {
				capturedData.put(value, "");
			} else {
				capturedData.put(value, getSelectedValueFromDropDown(logicalName));
			}
		}
		return capturedData;
	}

	public boolean verifyRadioButtonDisplayed(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		boolean status = false;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			status = verifyRadioButtonDisplayed(logicalName, value);
			if (!status) {
				return false;
			}
		}

		return false;
	}

	private boolean verifyRadioButtonDisplayed(String objectName, String value) {
		// TODO Auto-generated method stub
		return getText(objectName).trim().equals(value);
	}

	public boolean verifyCheckBoxDisplayed(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		boolean status = false;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			status = verifyCheckBoxDisplayed(logicalName, value);
			if (!status) {
				return false;
			}
		}

		return false;
	}

	private boolean verifyCheckBoxDisplayed(String objectName, String value) {
		return getText(objectName).trim().equals(value);
	}

	public void verifyPopupDisplayed(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyPopUp(logicalName, value);
		}
	}

	public boolean verifyPopUp(String objectName, String value) {

		return getText(objectName).trim().equals(value);
		// TODO Auto-generated method stub

	}

	public void moveToElement(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			moveToElement(logicalName);
		}
	}

	public void moveToElement(String objectName) {
		moveToElement(getObject(objectName));
	}

	public void verifyCapturedValueWithTextBox(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyCapturedValueWithTextBox(logicalName, value.trim());
		}
	}

	public void verifyCapturedValueWithTextBox(String objectName, String value) {
		System.out.println(value);
		String tempValue = (String) capturedData.get(value);
		System.out.println(tempValue);
		Assert.assertTrue(tempValue + "is not displayed", tempValue.equals(getValueFromTextBox(objectName).toString()));
	}

	public void verifyCapturedValueWithDropDown(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyCapturedValueWithDropDown(logicalName, value.trim());
		}
	}

	public void verifyCapturedValueWithDropDown(String objectName, String value) {
		System.out.println(value);
		String tempValue = (String) capturedData.get(value);
		System.out.println(tempValue);
		Assert.assertTrue(tempValue + "is not displayed",
				tempValue.equals(getSelectedValueFromDropDown(objectName).toString()));
	}

	public void verifyCapturedText(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyCapturedText(logicalName, value);
		}
	}

	public void verifyCapturedText(String objectName, String value) {
		System.out.println(value);
		String tempValue = (String) capturedData.get(value);
		System.out.println(tempValue);
		Assert.assertTrue(tempValue + "is not matched with" + getText(objectName).toString().trim(),
				tempValue.trim().equals(getText(objectName).toString().trim()));
	}

	public void verifyPartialCapturedText(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyPartialCapturedText(logicalName, value);
		}
	}

	public void verifyPartialCapturedText(String objectName, String value) {
		System.out.println(value);
		String tempValue = (String) capturedData.get(value);
		System.out.println(tempValue);
		if (tempValue.trim().contains(getText(objectName).toString().trim())
				|| getText(objectName).toString().trim().contains(tempValue.trim())) {
			Assert.assertTrue(true);
		} else {
			Assert.fail(tempValue + "is not Matched with" + getText(objectName).toString().trim());
		}
	}

	public void verifyPartialTextByCapturedValue(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyPartialText(logicalName, value);
		}
	}

	public void setText(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			setText(logicalName, value);
		}
	}

	public void selectValueFromDropdown(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			selectValueFromDropdown(logicalName, value);
		}
	}

	public void click(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			click(logicalName);
		}
	}

	public void click(String objectName) {
		click(getObject(objectName));
	}

	public void selectValueFromDropdown(String objectName, String value) {
		waitAndselectValueFromDropDown(getObject(objectName), value);
	}

	public boolean verifyRadioButtonStatus(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		boolean status = false;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			status = verifyRadioButtonSelected(logicalName, value);
			if (!status) {
				return false;
			}
		}
		return false;
	}

	public boolean verifyCheckBoxSelected(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		boolean status = false;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			status = verifyCheckBoxSelected(logicalName, value);
			if (!status) {
				return false;
			}
		}
		return false;
	}

	public void selectCheckBox(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		boolean status = false;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			selectCheckBox(logicalName, value);
		}

	}

	public void switchToFrame(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		boolean status = false;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			switchToFrame(logicalName);
		}

	}

	public void switchToDefaultPage() {
		switchToDefaultPage();
	}

	public void switchToFrame(String objectName) {
		switchToFrame(getObject(objectName));
	}

	public void selectCheckBox(String objectName, String value) {
		selectCheckBox(getObject(objectName), value);
	}

	public boolean verifyCheckBoxSelected(String objectName, String value) {

		return (value.equals("checked")) ? verifyCheckBoxStatus(getObject(objectName))
				: !verifyCheckBoxStatus(getObject(objectName));
	}

	public boolean verifyRadioButtonSelected(String objectName, String value) {

		return (value.equals("Selected")) ? verifyRadioButtonSelected(getObject(objectName))
				: !verifyRadioButtonSelected(getObject(objectName));
	}

	private boolean verifyRadioButtonSelected(WebElement element) {
		boolean status = false;
		try {
			status = element.isSelected();
		} catch (UnhandledAlertException ar) {
			driver.switchTo().alert().dismiss();
		}
		return status;
	}

	public void focusOn(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			focusOn(logicalName);
		}
	}

	public void verifyValueInDropdown(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
					value = getDynamicValue(value);
				}
			}
			List<String> valueListFromUI = getAllOptionsFromDropdown(logicalName);
			List<String> actualValueList = new ArrayList<String>();
			if (value.contains(",")) {
				String[] valuestr = value.split(",");
				for (int i = 0; i < valuestr.length; i++) {
					actualValueList.add(valuestr[i].trim());
				}
			} else {
				actualValueList.add(value.trim());
			}
			System.out.println("values in UI" + valueListFromUI);
			System.out.println("value from datalist" + actualValueList);
			ArrayList<String> diff = compareArrayList(actualValueList, valueListFromUI);
			if (diff.size() == 0) {
				Assert.assertTrue(value + " is present in dropdown" + logicalName, true);
			} else {
				logger.info("following values are not present" + diff);
				Assert.fail(value + "is not present in the dropdown" + logicalName);
			}
		}
	}

	ArrayList<String> compareArrayList(List<String> actualList, List<String> expectedList) {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < actualList.size(); i++) {
			String str = (String) actualList.get(i);
			if (!expectedList.contains(str)) {
				if (!output.contains(str))
					output.add(str);
			}
		}
		return output;
	}

	public List<String> getAllOptionsFromDropdown(String objectName) {
		return getAllOptionsFromDropdown(getObject(objectName));
	}

	public List<String> getAllOptionsFromDropdown(WebElement element) {
		List<String> listdropdown = new ArrayList<>();
		Select select = new Select(element);
		List<WebElement> allOptions = select.getOptions();
		for (WebElement option : allOptions) {
			listdropdown.add(option.getText().trim());
		}

		return listdropdown;
	}

	public void verifyPartialText(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyPartialText(logicalName, value);
		}
	}

	public void verifyTitle(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			verifyPageDisplayed(logicalName, value);
		}
	}

	public void clickByJS(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			clickByJS(logicalName);
		}
	}

	public void selectMultipleValuesInList(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			logger.info("Running row no - " + entry.getKey());
			LinkedHashMap<String, String> mapVal = entry.getValue();
			selectMultipleItemsInList(mapVal);
		}
	}

	public void clickByJS(String objectName) {
		clickByJS(getObject(objectName));

	}

	public boolean verifyElementDisplayed(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {
		String logicalName = null;
		String value = null;
		boolean status = true;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			if (value.equalsIgnoreCase("Displayed")) {
				status = isElementPresent(logicalName);
				if (!status) {
					return false;
				}
			} else if (value.equalsIgnoreCase("Enabled")) {
				status = isElementEnabled(logicalName);
				if (!status) {
					return false;
				}
			} else if (value.equalsIgnoreCase("Disabled") || value.equalsIgnoreCase("Hidden")) {
				status = isElementEnabled(logicalName);
				if (status) {
					return false;
				}
			} else if (value.equalsIgnoreCase("ReadOnly")) {
				status = getElementStatusByAttribute(logicalName, "readOnly");
				if (!status) {
					return false;
				}
			} else {
				status = isElementPresent(logicalName);
				if (!status) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean getElementStatusByAttribute(String objectName, String attrType) {
		return getElementStatusByAttribute(getObject(objectName), attrType);
	}

	public boolean isElementEnabled(String objectName) {
		return isElementEnabled(getObject(objectName));
	}

	public boolean isElementEnabled(WebElement element) {
		boolean flag = false;
		try {
			if (element.isEnabled())
				flag = true;
		} catch (NoSuchElementException e) {
			flag = false;
		} catch (StaleElementReferenceException e) {
			flag = false;
		}
		return flag;
	}

	public void verifyPageDisplayed(String objectName, String value) {
		try {
			if (driver.getTitle().contains(value)) {
				verifyTitleContains(value);
			} else {
				Thread.sleep(3000);
				if (objectName.equalsIgnoreCase("PageTitle")) {
					switchToWindowTitleContains(value);
				} else {
					// switchToWindowByElement(objectName, value);
				}
			}
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void switchToWindowTitleContains(String wTitle) {
		Set<String> winIDs = driver.getWindowHandles();
		boolean status = false;
		logger.info("Totoal Windows size-" + winIDs.size());
		if (winIDs.size() != 1) {
			for (String hndl : winIDs) {
				driver.switchTo().window(hndl);
				waitForDisplayed(driver.getTitle(), timeOutInSeconds);
				logger.info("Title -" + driver.getTitle());
				if (driver.getTitle().contains(wTitle)) {
					Assert.assertTrue(true);
					System.out.println("Status" + status);
					break;
				}
			}
			if (!status) {
				Assert.fail("'" + wTitle + "'Title from data table is not matched with UI Title'" + driver.getTitle()
						+ "'");
			}
		} else {
			logger.info("Only one window is present..so not doing windows switch");
			driver.switchTo().window(driver.getWindowHandle());
			verifyTitleContains(wTitle);
		}
	}

	/*
	 * public switchToWindowByElement(String objectName, String heading) {
	 * Set<String> winIDs = driver.getWindowHandles(); boolean status = false;
	 * logger.info("Totoal Windows size-" + winIDs.size()); if (winIDs.size() !=
	 * 1) { Iterator<String> it = winIDs.iterator(); it.next(); for (String hndl
	 * : winIDs) { driver.switchTo().window(hndl); isPageLoaded(); if
	 * (isElementPresent(objectName)) {
	 * 
	 * } } } }
	 */

	public boolean isElementPresent(String objectName) {
		return isElementPresent(getObject(objectName));
	}

	public boolean isElementPresent(WebElement element) {
		boolean flag = false;
		try {
			if (element.isDisplayed())
				flag = true;
		} catch (NoSuchElementException e) {
			flag = false;
		} catch (StaleElementReferenceException e) {
			flag = false;
		}
		return flag;
	}

	public void verifyTitleContains(String wTitle) {
		if (driver.getTitle().contains(wTitle)) {
			Assert.assertTrue(true);
		} else {
			logger.info("Title -" + driver.getTitle());
			Assert.fail(
					"'" + wTitle + "'Title from the data table is not match with UI title" + driver.getTitle() + "'");
		}
	}

	public void verifyPartialText(String objectName, String value) {

		Assert.assertTrue(value + "is not partially matched with UI" + getText(objectName),
				getText(objectName).toString().trim().contains(value.trim()));

	}

	public void setTextByCapturedValue(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			System.out.println("capturedData is setText" + capturedData);
			setText(logicalName, capturedData.get(value));
		}
	}

	public void selectRadioButton(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			selectRadioButton(logicalName);
		}
	}

	public void selectRadioButton(String objectName) {
		selectRadioButton(getObject(objectName));
	}

	public boolean verifyTextNotDisplayed(String objectName, String value) {
		focusOn(objectName);
		if (!value.equals(getText(objectName).toString())) {
			return true;
		} else
			return false;
	}

	public void verifyTextNotDisplayed(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			logger.info("Running row no -" + entry.getKey());
			LinkedHashMap<String, String> mapVal = entry.getValue();
			verifyTextNotDisplayed(mapVal.get("LogicalName"), mapVal.get("Value"));
		}

	}

	public void setTextByFocus(String objectName, String value) {
		focusAndClick(objectName);
		setText(getObject(objectName), value);

	}

	public void focusAndClick(String objectName) {
		focusAndClick(getObject(objectName));
	}

	public void setTextByFocus(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			logger.info("Running row no -" + entry.getKey());
			LinkedHashMap<String, String> mapVal = entry.getValue();
			setTextByFocus(mapVal.get("LogicalName"), mapVal.get("Value"));
		}

	}

	public static String getTimeStampValue() throws IOException {
		Calendar cal = Calendar.getInstance();
		Date time = cal.getTime();
		String timestamp = time.toString();
		System.out.println(timestamp);
		String systime = timestamp.replace(":", "-");
		System.out.println(systime);
		return systime;

	}

	public String screenCapture(String scenarioName, String initialDateStamp) {
		String fileName = null;

		try {
			String dateStr = "YYYY-MM-dd";
			String dateFormatter = "HH-mm-ss";
			DateUtil dateUtil = new DateUtil();
			String sBasePath = SCREENSHOT_PATH;
			String fileNamePath = sBasePath + File.separator
					+ dateUtil.convertDateToString(dateUtil.convertCalenderToDate(), dateStr) + File.separator
					+ scenarioName + File.separator + initialDateStamp + File.separator + scenarioName + "_"
					+ dateUtil.convertDateToString(dateUtil.convertCalenderToDate(), dateFormatter);

			Robot robot = new Robot();
			String format = "png";
			fileName = fileNamePath + "+Screenshot." + format;
			File file = new File(String.valueOf(fileName));
			file.mkdirs();
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
			ImageIO.write(screenFullImage, format, new File(fileName));
			System.out.println("A full screen shot captured");
		} catch (AWTException | IOException ex) {
			System.err.print(ex);
		}
		return fileName;
	}

	public void loadPreviousScenarioData(String fileBaseFolder) {
		File testFile = new File(fileBaseFolder + File.separator + "test-data.properties");
		if (testFile.exists()) {
			propertyReader.loadMapsFromPropertyFile(testFile, capturedData);
		}
	}

	public static String generateRandomWords() {

		String randomStrings = null;
		Random random = new Random();

		char[] word = new char[3];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));

		}
		randomStrings = new String(word);
		return randomStrings;
	}

	public static String generateRandom1500CharWords() {

		String randomStrings = null;
		Random random = new Random();

		char[] word = new char[1499];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));

		}
		randomStrings = new String(word);
		return randomStrings + "$";
	}

	public static String generateRandomCharWords(int range) {

		String randomStrings = null;
		Random random = new Random();

		char[] word = new char[range - 1];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));

		}
		randomStrings = new String(word);
		return randomStrings + "$";
	}

	public void sendKeys(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			sendKeys(logicalName, value);
		}
	}

	public void sendKeys(String objectName, String value) {
		sendKeys(getObject(objectName), value);
	}

	public void sendKeys(WebElement element, String value) {
		System.out.println("Inside the Send Keyes");
		waitForDisplayed(element, 30);
		element.sendKeys(value);
	}

	public void mouseHoverToElement(WebElement element) {
		waitForDisplayed(element, 30);
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}

	public void mouseHoverToElement(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;
		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();
			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("Logical Name-" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = newEntry.getValue();
					logger.info("Value-" + value);
				}
			}
			mouseHoverToElement(logicalName);
		}
	}

	public void mouseHoverToElement(String objectName) {
		mouseHoverToElement(getObject(objectName));
	}

	public void waitForBrowserToLoadCompletely() {
		String state = null;
		String oldstate = null;
		try {
			System.out.println("Waiting for browser loading to complete");
			int i = 0;
			while (i < 5) {
				Thread.sleep(1000);

				state = ((JavascriptExecutor) driver).executeScript("return document.readyState;").toString();
				System.out.println("." + Character.toUpperCase(state.charAt(0)) + ".");

				if (state.equals("interactive") || state.equals("loading"))
					break;

				if (i == 1 && state.equals("complete")) {
					System.out.println();
					return;
				}
				i++;
			}
			i = 0;
			oldstate = null;
			Thread.sleep(2000);

			while (true) {
				state = ((JavascriptExecutor) driver).executeScript("return document.readyState;").toString();
				System.out.println("." + state.charAt(0) + ".");
				if (state.equals("compelte"))
					break;

				if (state.equals(oldstate))
					i++;
				else
					i = 0;

				if (i == 15 && state.equals("loading")) {

					System.out.println("\nBrowser in" + state + "state since last 60 secs. So refreshing browser");
					driver.navigate().refresh();
					System.out.println("Waiting for browser loading to complete");
					i = 0;
				} else if (i == 6 && state.equals("interactive")) {
					System.out.println("\nBrowser in " + state + "state since last 30 secs.So starting with execution");
					return;
				}

				Thread.sleep(4000);
				oldstate = state;
			}
			System.out.println();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

	}

	public void focusOn(String objectName) {
		focusOn(getObject(objectName));
	}

	public void setText(String objectName, String value) {
		setText(getObject(objectName), value);
	}

	private String getValueFromTextBox(String objectName) {

		return getValueFromTextBox(getObject(objectName));
	}

	public String getSelectedValueFromDropDown(String objectName) {
		return getSelectedValueFromDropDown(getObject(objectName));
	}

	public void isPageLoaded() {
		waitForPageLoad();

	}

	public void waitForPageLoad() {
		System.out.println("Waiitng for ready state complete");
		(new WebDriverWait(driver, timeOutInSeconds)).ignoring(WebDriverException.class)
				.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						JavascriptExecutor js = (JavascriptExecutor) d;
						String readyState = js.executeScript("return document.readyState").toString();
						System.out.println("Ready state:" + readyState);
						return (Boolean) readyState.equals("complete");
					}
				});

	}

	public void verifyText(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();

			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("LogicalName" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = getDataTableValue(newEntry);

				}

			}

			verifyText(logicalName, value);

		}

	}

	public void verifyTextAfterScroll(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();

			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("LogicalName" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = getDataTableValue(newEntry);

				}

			}

			scrollToView(logicalName);
			verifyText(logicalName, value);

		}

	}

	public void UploadFile(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();

			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("LogicalName" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = getDataTableValue(newEntry);

				}

			}

			// scrollToView(logicalName);
			UploadFile(logicalName, value);

		}

	}

	public void scrollToView(String objectName) {
		scrollToView(getObject(objectName));
	}

	public void verifyValueinTextBox(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();

			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("LogicalName" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = getDataTableValue(newEntry);

				}

			}

			verifyValueinTextBox(logicalName, value);

		}

	}

	public void verifyTextByJS(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();

			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("LogicalName" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = getDataTableValue(newEntry);

				}

			}

			verifyTextByJS(logicalName, value);

		}

	}

	public void verifyTextByJS(String objectName, String value) {
		assertEquals(value, getTextByJS(objectName));
	}

	public String getTextByJS(String objectName) {
		return getTextByJS(getObject(objectName));
	}

	public void verifyValueByAttribute(LinkedHashMap<Integer, LinkedHashMap<String, String>> mData, String attType) {

		String logicalName = null;
		String value = null;

		Iterator<Map.Entry<Integer, LinkedHashMap<String, String>>> entries = mData.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<Integer, LinkedHashMap<String, String>> entry = entries.next();

			LinkedHashMap<String, String> mapVal = entry.getValue();
			Iterator<Map.Entry<String, String>> newEntries = mapVal.entrySet().iterator();
			while (newEntries.hasNext()) {
				Map.Entry<String, String> newEntry = newEntries.next();

				if (newEntry.getKey().contains("LogicalName")) {
					logicalName = newEntry.getValue();
					logger.info("LogicalName" + logicalName);
				}

				if (newEntry.getKey().contains("Value")) {
					value = getDataTableValue(newEntry);

				}

			}

			verifyValueByAttribute(logicalName, value, attType);

		}

	}

	public void verifyValueByAttribute(String objectName, String value, String attType) {
		assertTrue(value + "is displayed", value.equals(getValuebyAttribute(objectName, attType).toString()));
	}

	public String getValuebyAttribute(String objectName, String attType) {
		return getValuebyAttribute(getObject(objectName), attType);
	}

	public void verifyValueinTextBox(String objectName, String value) {
		assertTrue(value + "is not Matched with UI value",
				value.equals(getValueFromTextBox(objectName).toString().trim()));
	}

	public void verifyText(String objectName, String value) {
		Assert.assertTrue(value + "is not matched with UI" + getText(objectName),
				value.equals(getText(objectName).toString().trim()));

	}

	private void UploadFile(String objectName, String value) {
		getObject(objectName).click();
		uploadFiletoPath(value);

	}

	public static void setClipboardData(String string) {
		// StringSelection is a class that can be used for copy and paste
		// operations.
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	public static void uploadFiletoPath(String fileLocation) {
		try {
			// Setting clipboard with file location
			setClipboardData(fileLocation);
			// native key strokes for CTRL, V and ENTER keys
			Robot robot = new Robot();

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			Thread.sleep(2000);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			Thread.sleep(3000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	private String getDataTableValue(Map.Entry<String, String> newEntry) {
		String value = newEntry.getValue() == null ? "" : newEntry.getValue();

		if (value.contains("\"\"")) {
			value = value.replace("\"\"", "");
		}
		logger.info("Value -" + value);
		value = getDynamicValue(value);
		return value;
	}

	private String getDynamicValue(String valueDT) {
		String value = "";
		String columnValue = valueDT;
		value = valueDT;

		return value;

	}
	
	public void ValidateImage(String objectName) throws InterruptedException {
		Thread.sleep(10000);
		//driver.switchTo().frame(0);
		WebElement ImageFile = getObject(objectName);

		JavascriptExecutor executor = (JavascriptExecutor) driver;
		Boolean ImagePresent = (Boolean) ((executor).executeScript(
				"return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0",
				ImageFile));
		System.out.println("Image present or not " + ImagePresent);
		if (ImagePresent) {
			Assert.assertTrue(true);
		} else {
			// logger.info("Title -" + driver.getTitle());
			Assert.fail("'" + ImagePresent + "'Image is Not present");
		}
	}


}
