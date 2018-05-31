package com.salesforce.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.core.base.CoreWebDriverBase;
import com.core.util.PropertyReader;

public class SalesforceWebdriverBase extends CoreWebDriverBase {

	public static final int iWaitSeconds = 20;
	protected static WebDriver driver;
	protected static PropertyReader propReader = new PropertyReader();
	
	static String sUrl = null;
	
	public static Logger logger = Logger.getLogger(SalesforceWebdriverBase.class.getName());

	public static void loadInitialURL() throws Exception {
		String sUrl = getAppURLNew();
		// System.out.println("URL from load Intial URL fucntion");
		driver = getCurrentDriver();
		driver.get(sUrl);
		Thread.sleep(3000);

	}

	public static String getAppURLNew() {
		try {
			sUrl = propReader.getApplicationproperty("url").trim();
			// System.out.println("Inside getApp Url");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return sUrl;
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

	public static WebDriver getCurrentDriver() {
		if (driver == null) {
			try {

				String sBrowserName = propReader.getApplicationproperty("browser.type").trim();
				System.out.println("Browser Name" + sBrowserName);
				System.setProperty("browser", sBrowserName);
				if (sBrowserName.trim().equalsIgnoreCase("firefox")) {
					DesiredCapabilities capabilities = new DesiredCapabilities();
					capabilities.setBrowserName("firefox");
					capabilities.setPlatform(org.openqa.selenium.Platform.ANY);
					ProfilesIni allProfiles = new ProfilesIni();
					FirefoxProfile profile = allProfiles.getProfile("default");
					capabilities.setCapability(FirefoxDriver.PROFILE, profile);
					DesiredCapabilities capabalities = DesiredCapabilities.firefox();
					// capabalities.setCapability("marionette", true);
					driver = new FirefoxDriver(capabalities);
					driver.manage().deleteAllCookies();
					driver.manage().window().maximize();
				} else if (sBrowserName.trim().equalsIgnoreCase("chrome")) {
					System.setProperty("webdriver.chrome.driver", "/Users/tejaswiganta/Desktop/techark/testingdocsandapps/chromedriver");
					driver = new ChromeDriver();
					driver.manage().window().maximize();
				}
			} catch (Exception exception) {
				logger.info("Exeption -" + exception.getMessage());
			}

		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}

	protected static WebDriver createIEWebDriver(String sUrl) throws IOException {
		DesiredCapabilities cap = getIECapabalities(sUrl);
		driver = new InternetExplorerDriver(cap);
		driver.manage().window().maximize();
		return driver;

	}

	private static DesiredCapabilities getIECapabalities(String sUrl) throws IOException {
		String sPathIEServer = System.getProperty("user.dir") + File.separator + "repositories" + File.separator
				+ "IEDriverServer.exe";
		File file = new File(sPathIEServer);
		System.setProperty("webdriver.ie.driver", file.getAbsolutePath());

		DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
		cap.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, false);
		cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		cap.setCapability("ignoreZoomSetting", true);
		cap.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
		cap.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
		cap.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
		cap.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		cap.setBrowserName(cap.getBrowserName());
		cap.setCapability("initialBrowserUrl", sUrl);
		cap.setJavascriptEnabled(true);
		return cap;

	}

	public static void closeBrowser() {

		try {

			if (driver != null) {
				driver.close();
			//	driver.quit();
				driver = null;

			}

		} catch (Exception e) {

		}

	}

	

	
}
