package OCLSWebsiteTests;

import org.testng.annotations.*;

import seleniumUtils.CurrenDateTime;
import seleniumUtils.DriverUpdate;
import seleniumUtils.JUnitHTMLReporter;

//import org.testng.Assert;
import org.junit.Assert;
import org.testng.Reporter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

//@Listeners(report.CustomTestNGReporter.class)  
public class SiteTests {
	public String baseUrl = "https://www.ocls.ca";
	public int testSuccesscount = 0;
	public int testTotalcount = 0;
	public String stepNumber = "0";
	long start = CurrenDateTime.getCurrentTime();
	
	public WebDriver driver;

	// @Test(dataProvider = "dp")
	// public void f(Integer n, String s) {
	// }

	@Test(priority = 0, alwaysRun = true)
	public void verifyHomepageTitle() {
		String url = baseUrl;
		testTotalcount++;
		JUnitHTMLReporter.report("*** Running Test #" + testTotalcount + " to verify that OCLS.CA is running.");
		driver.get(url);
		String expectedTitle = "Ontario Colleges Library Service";
		String actualTitle = driver.getTitle();
		try {
			Assert.assertEquals(actualTitle, expectedTitle);
			testSuccesscount++;
		} catch (AssertionError e) {
			String desc = "***Error: The following assertion error happened in <span style=\"color: blue\">" + url
					+ "</span> . The test <span style=\"color: brown\">verifyHomepageTitle</span> has an error: "
					+ e.toString();
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			throw e;
		}
	}

	@Test(priority = 1, alwaysRun = true)
	public void verifyCollegeShortcutsPage() throws Exception {
		String url = "https://www.ocls.ca/aaa";
		testTotalcount++;
		JUnitHTMLReporter.report("*** Running Test #" + testTotalcount + " to verify login to " + url);
		driver.get(url);
		String expectedTitle = "Access denied | Ontario Colleges Library Service"; // Ontario Colleges Library Service
		String actualTitle = driver.getTitle();
		try {
			Assert.assertEquals(actualTitle, expectedTitle);
			login(driver, url, "General OCLS","aaa","bbb", "College Web Page Shortcuts");
			testSuccesscount++;
		} catch (AssertionError e) {
			String desc = "***Error: The following assertion error happened in <span style=\"color: blue\">" + url
					+ "</span> . The test <span style=\"color: brown\">verifyCollegeShortcutsPage</span> has an error: "
					+ e.toString();
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			throw e;
		}
	}
	
	@DataProvider(name = "DocumentsTest")
	public String[] dataProviderMethod() {
		String [] colleges = TestProperties.getCollegeArray();
		return colleges;
	}
	
	@Test(dataProvider = "DocumentsTest", priority = 2, alwaysRun = true)
	public void verifyDocumentLinks(String college) throws Exception {
		testTotalcount++;
		CollegeConfiguration collegeData = new CollegeConfiguration(college);
		
		try {
			collegeData.getCollegeConfigFromFile();
			String url=collegeData.getDocumentsUrl();
			JUnitHTMLReporter.report("*** Running Test #" + testTotalcount + " to verify login to " + url);
			driver.get(url);
			String actualTitle = driver.getTitle();
			String expectedTitle = "Access denied | Ontario Colleges Library Service";
			Assert.assertEquals(actualTitle, expectedTitle);
			login(driver, collegeData, "verifyDocumentLinks");
			testSuccesscount++;
			
		} catch (FileNotFoundException e) {
			String desc = "***Error: Unable to open configuration file Colleges.cfg "
					+ e.toString();
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			throw e;
		} catch (AssertionError e) {
			String desc = "***Error: The following assertion error happened in <span style=\"color: blue\">" + collegeData.getDocumentsUrl()
					+ "</span> . The test <span style=\"color: brown\">verifyCollegeShortcutsPage</span> has an error: "
					+ e.toString();
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			throw e;
		}
	}
	
	public void login(WebDriver driver, CollegeConfiguration collegeData, String calledBy) throws Exception {
		String college = collegeData.getCollege();
		String username = collegeData.getLoginUser();
		String password = collegeData.getLoginPwd();
		String url = "";
		String expectedAfterLoginTitle="";
		if (calledBy.equals("verifyDocumentLinks"))
		{
			url = collegeData.getDocumentsUrl();
			expectedAfterLoginTitle = collegeData.getDocumentsTitle();
		}
		else if(calledBy.equals("verifySubscriptionLinks"))
		{
			url = collegeData.getSubscriptionsUrl();
			expectedAfterLoginTitle = collegeData.getSubscriptionsTitle();
		}
		login(driver, url, college, username, password, expectedAfterLoginTitle);

	}

	public void login(WebDriver driver, String url, String college, String username, String password, String expectedAfterLoginTitle) throws Exception {
		String stepNumber = "L0";

		try {

			driver.findElement(By.xpath("//input[@id='edit-name']")).sendKeys(username);
			stepNumber = "L3";
			driver.findElement(By.xpath("//input[@id='edit-pass']")).sendKeys(password);
			stepNumber = "L4";
			driver.findElement(By.xpath("//input[@id='edit-submit--2']")).sendKeys(Keys.ENTER);// works better
																										// than click
			stepNumber = "L5";
			// wait for login screen to be replaced by database screen
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30))
					.pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);
			wait.until(ExpectedConditions.titleContains(expectedAfterLoginTitle));
			stepNumber = "L6";
			String testTitle = driver.getTitle();
			stepNumber = "L7";
			// make sure that after login screen title changed
			Assert.assertTrue(
					"Tried to login, but was not successfull. Expected to have after login screen, but is still on login.",
					testTitle.contains(expectedAfterLoginTitle));
			stepNumber = "L8";
		} catch (AssertionError e) {
			String desc = "***Assertion Error: <span style=\"color: red\">College: " + college
					+ "</span> <span style=\"color: blue\">URL: " + url
					+ "</span> Last step: " + stepNumber
					+ " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			System.out.println(desc);
			throw new Exception("AssertionError ", e);
		} catch (UnhandledAlertException e) {
			String desc = "***Error: <span style=\"color: red\">College: " + college
					+ "</span> <span style=\"color: blue\">URL: " + url
					+ "</span>Last step: " + stepNumber
					+ " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			System.out.println(desc);
			throw new Exception("UnhandledAlertException ", e);
		} catch (NoSuchElementException e) {
			String desc = "***Error: <span style=\"color: red\">College: " + college
					+ "</span> <span style=\"color: blue\">URL: " + url
					+ "</span> Last step: " + stepNumber
					+ " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			System.out.println(desc);
			throw new Exception("NoSuchElementException ", e);
		} catch (Exception e) {
			String desc = "***Error: <span style=\"color: red\">College: " + college
					+ "</span> <span style=\"color: blue\">URL: " + url
					+ "</span> Last step: " + stepNumber
					+ " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>" + desc + "</b>");
			System.out.println(desc);
			throw e;
		}

	}

	@BeforeMethod
	// public void beforeMethod() {}
	public void createDriver() {
		ChromeOptions options = new ChromeOptions();
		// options.addArguments("--headless");
		// options.addArguments("disable-infobars"); //to prevent the yellow infobar
		// from appearing, like "You are using unsupported command line flag ..."
		// options.addArguments("enable-automation");

		System.setProperty("webdriver.chrome.driver", TestProperties.getWindowsChromeDriverLocation());

		disableChromeImages(options);// not to load images
		driver = new ChromeDriver(options);
		driver.manage().deleteAllCookies();
	}

	@AfterMethod
	// public void afterMethod() {}
	public void driverTearDown() {
		driver.close(); // Close the browser window that the driver has focus of
		driver.quit(); // Calls Dispose()
	}

	@DataProvider (name = "subscriptions")
	public Object[][] dp() {
		return new Object[][] { new Object[] { 1, "a" },
								new Object[] { 1, "a" },
								new Object[] { 2, "b" },
								new Object[] { 2, "b" }, };
	}

	@BeforeClass
	public void beforeClass() {
	}

	@AfterClass
	public void afterClass() {
	}

	@BeforeTest
	public void beforeTest() {
		//before first test
		//JUnitHTMLReporter.report("### BeforeTest ###");
	}

	@AfterTest
	public void afterTest() {
		//after last test
		//JUnitHTMLReporter.report("### AfterTest ###");
	}

	@BeforeSuite
	// public void beforeSuite() {}
	public void updateDriver() {
		try {
			setupTestReporter();
			
			DriverUpdate.checkAndUpdate();
			System.out.println("ChromeDriver is up to date");
		} catch (Exception e) {
			System.out.println("Unable to update ChromeDriver" + e.toString());
		}
	}

	@AfterSuite
	public void afterSuite() {
		String success = " SUCCESS";
		if (testSuccesscount < testTotalcount) {
			success = " FAILED";
		}
		try {
			JUnitHTMLReporter.report("<b>"+testSuccesscount + " out of " + testTotalcount + " ran successfully.</b>");
			JUnitHTMLReporter.report("<b>***Test Finished***</b>");
			JUnitHTMLReporter.report("<b>***Test end: "+ CurrenDateTime.getDateTime() + "***</b>");
			long end = CurrenDateTime.getCurrentTime();
			JUnitHTMLReporter.report("<b>***Test duration: "+ CurrenDateTime.getDurationMinutes(start, end) + "***</b>");
			JUnitHTMLReporter.tearDown(true, TestProperties.getToEmailAddr(), TestProperties.getEmailTitle() + success);
		} catch (IOException exc) {
			System.out.println("***Test failed: Unable to write to log file: " + exc.toString() + " ***");
		}
	}

	public void disableChromeImages(ChromeOptions options) {
		// the website images will not appear
		HashMap<String, Object> images = new HashMap<String, Object>();
		images.put("images", 2);
		HashMap<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_setting_values", images);
		options.setExperimentalOption("prefs", prefs);
	}

	public void setupTestReporter() throws Exception {
		// reporter setup
		try {
			JUnitHTMLReporter.setUp(TestProperties.getWindowsReportFile() + ".html");
		} catch (IOException exc) {
			System.out.println("***Test failed: Unable to open log file: " + exc.toString() + " ***");
			throw new Exception(exc);
		}
	}

}
