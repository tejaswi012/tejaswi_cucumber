package com.core.base;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.core.util.Constants;
import com.core.util.PropertyReader;



/**
 * Unit test for simple Application.
 */
public class CoreWebDriverBase {

	public static final int iWaitSeconds = 20;
	protected static WebDriver driver;
	protected static PropertyReader propReader = new PropertyReader();
	public Process process;
	public ProcessBuilder builder;

	public static Logger logger =Logger.getLogger(CoreWebDriverBase.class.getName());

	public CoreWebDriverBase() {
		PropertyConfigurator.configure(Constants.loggerConfigPath);

	}

	protected static WebDriver createFirefoxDriver() {
		System.out.println("create FireFOx driver");
		DesiredCapabilities cap = getFirefoxDesiredCapabilities();
		driver = new FirefoxDriver(cap);
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		return driver;

	}

	private static DesiredCapabilities getFirefoxDesiredCapabilities() {
		ProfilesIni allProfiles = new ProfilesIni();
		FirefoxProfile profile = allProfiles.getProfile("default");
		profile.setEnableNativeEvents(false);
		setPreferences(profile);
		DesiredCapabilities cap = setCapabilities(profile);

		return cap;

	}

	private static WebDriver createFFDriver() {
		ProfilesIni allProfiles = new ProfilesIni();
		FirefoxProfile myProfile = allProfiles.getProfile("default");
		setPreferences(myProfile);
		driver = new FirefoxDriver(myProfile);
		return driver;
	}

	private static DesiredCapabilities setCapabilities(FirefoxProfile profile) {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setBrowserName("firefox");
		capabilities.setPlatform(org.openqa.selenium.Platform.ANY);
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		DesiredCapabilities cap = DesiredCapabilities.firefox();
		cap.setCapability(FirefoxDriver.PROFILE, profile);
		return cap;
	}

	private static void setPreferences(FirefoxProfile profile) {
		profile.setPreference("browser.startup.page", 0);
	}

	protected static WebDriver createRemoteDriverforFirefox(String hubIP,
			String sUrl) throws MalformedURLException {
		if (driver == null) {
			new DesiredCapabilities();
			DesiredCapabilities caps = getFirefoxDesiredCapabilities();
			URL hubUrl = new URL("http://" + hubIP + ":4444/wd/hub");
			driver = new RemoteWebDriver(hubUrl, caps);
			driver.manage().window().maximize();
			driver.get(sUrl);
		}
		return driver;
	}

	protected static WebDriver createRemoteDriverforIE(String hubIP, String sUrl)
			throws IOException {
		URL hubUrl = new URL("http://" + hubIP + ":4444/wd/hub");

		DesiredCapabilities caps = getIECapabalities(sUrl);

		driver = new RemoteWebDriver(hubUrl, caps);
		driver.manage().window().maximize();
		return driver;

	}

	protected static WebDriver createIEWebDriver(String sUrl)
			throws IOException {
		DesiredCapabilities cap = getIECapabalities(sUrl);
		driver = new InternetExplorerDriver(cap);
		driver.manage().window().maximize();
		return driver;

	}

	private static DesiredCapabilities getIECapabalities(String sUrl)
			throws IOException {
		String sPathIEServer = System.getProperty("user.dir") + File.separator
				+ "repositories" + File.separator + "IEDriverServer.exe";
		File file = new File(sPathIEServer);
		System.setProperty("webdriver.ie.driver", file.getAbsolutePath());

		DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
		cap.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, false);
		cap.setCapability(
				InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
				true);
		cap.setCapability("ignoreZoomSetting", true);
		cap.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,
				true);
		cap.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
		cap.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP,
				true);
		cap.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
				UnexpectedAlertBehaviour.IGNORE);
		cap.setBrowserName(cap.getBrowserName());
		cap.setCapability("initialBrowserUrl", sUrl);
		cap.setJavascriptEnabled(true);
		return cap;

	}

	public synchronized static WebDriver getCTRCurrentDriver() throws Exception {
		if (driver == null) {
			try {

				String sBrowserName = propReader.getApplicationproperty(
						"browser.type").trim();
				String sUrl = propReader.getApplicationproperty("app.url")
						.trim();
				String sRemoteMode = propReader.getApplicationproperty(
						"use.grid").trim();
				String sHub = propReader.getApplicationproperty("hub.url");
				if (sRemoteMode.equalsIgnoreCase("true")) {
					if (sBrowserName.trim().equalsIgnoreCase("firefox")) {
						driver = createRemoteDriverforFirefox(sHub, sUrl);
						return driver;
					}

					if (sBrowserName.trim().equalsIgnoreCase("ie")) {
						driver = createRemoteDriverforIE(sHub, sUrl);
						return driver;
					}
				} else {

					if (sBrowserName.trim().equalsIgnoreCase("firefox")) {
						driver = createFirefoxDriver();

					}

					else if (sBrowserName.trim().equalsIgnoreCase("ie")) {
						driver = createIEWebDriver(sUrl);

					} else {
						System.out
								.println("Please define the browser type = as firefox or ie and remoteMode = true/false in application.properties inside properties folder");
					}

				}
				driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
			} finally {
				Runtime.getRuntime().addShutdownHook(
						new Thread(new browserCleanup()));
			}
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}

	public static void openURL(String url) throws Exception {
		getCTRCurrentDriver().navigate().to(url);

	}

	public static void loadInitialURL(String user) throws Exception {
		String sUrl = propReader.getApplicationproperty("app.url").trim();
		getCTRCurrentDriver().navigate().to(sUrl + user);
	}

	public static void loadInitialURL() throws Exception {
		String sUrl = propReader.getApplicationproperty("app.url").trim();
		getCTRCurrentDriver().navigate().to(sUrl);
	}

	public static class browserCleanup implements Runnable {

		public void run() {
			logger.info("closing the browser");
			close();
		}

	}

	public static void close() {
		try {
			driver.quit();
			driver = null;
			logger.info("closing the browser");
		} catch (UnreachableBrowserException e) {
			logger.info("cannot close browser:unreachable browser");
		}
	}

	public void startHub() throws Exception {

		String gridPath = System.getProperty("user.dir") + File.separator
				+ "grid" + File.separator;
		process = Runtime.getRuntime().exec(
				"cmd /c start " + gridPath + "StartHub.bat");

	}

	public void stopHub() throws Exception {

		String gridPath = System.getProperty("user.dir") + File.separator
				+ "grid" + File.separator;
		process = Runtime.getRuntime().exec(
				"cmd /c start " + gridPath + "StopHub.bat");

	}

	/*
	 * public static void killIE() { try {
	 * Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
	 * Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
	 * System.out.println("IE Explorer and server got killed..");
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } }
	 */

}
