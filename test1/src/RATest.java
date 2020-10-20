
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.remote.RemoteWebDriver;
//import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;

import java.lang.StringIndexOutOfBoundsException;
import org.junit.Assert;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import seleniumUtils.CurrenDateTime;
import seleniumUtils.DriverUpdate;
import seleniumUtils.JUnitHTMLReporter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher; 
 
public class RATest {
		
	public static int testSuccesscount = 0;
	public static String stepNumber = "0";
	public static LinkedHashMap<String, String> hm_collegeURLs = new LinkedHashMap<String, String>();//URLs=DatabaseName
	
	public static void main(String[] args) throws InterruptedException
	{
		boolean failure = false;
		
		System.out.println("EZRA test of Full Urls for each college");
		String collegeName;
		long start = CurrenDateTime.getCurrentTime();
		if (args.length > 0)
		{
			collegeName = args[0]; //college listed in a command line
		}
		else
		{
			collegeName = "";//processing for all colleges should be added if needed
			
			//String[] collegeArray = TestProperties.getCollegeArray();
			//int totalTestCount = collegeArray.length;
		}
		
		try
		{
			setupTestReporter(collegeName);
			
			DriverUpdate.checkAndUpdate();//check if ChromeDriver version corresponds to Chrome version, update if needed
			
			if (pingProcess ("C:\\Windows\\System32\\PING.EXE ra.ocls.ca") == true)
			{
				failure = true;
			}
			if (pingProcess ("C:\\Windows\\System32\\PING.EXE ezra.ocls.ca") == true)
			{
				failure = true;
			}
			if (failure = pingProcess ("C:\\Windows\\System32\\PING.EXE eztest.ocls.ca") == true)
			{
				failure = true;
			}
			
			failure = findURLs(collegeName);
			if (failure)
			{
				String desc = "***Info: Trying again to read URLs from ERA.***";
				JUnitHTMLReporter.report(desc);
				failure = findURLs(collegeName);
			}
			
			int totalTestCount = hm_collegeURLs.size();
			if (!failure)
			{
				JUnitHTMLReporter.report("*** Start Test <b>" + collegeName + "</b>");
				failure = processURLs(collegeName);
			}

			JUnitHTMLReporter.report("<b>"+testSuccesscount+" out of "+totalTestCount+" ran successfully.</b>");
			if(testSuccesscount < totalTestCount)
			{failure = true;}
		}
		catch (Exception e)
		{
			failure = true;
			String desc = "***Error: " + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		finally
		{
			String success = "";
			if (failure == false)
			{success = " SUCCESS";}
			else
			{success = " FAILED";}
			try {
				JUnitHTMLReporter.report("<b>***Test Finished***</b>");
				JUnitHTMLReporter.report("<b>***Test end: "+ CurrenDateTime.getDateTime() + "***</b>");
				long end = CurrenDateTime.getCurrentTime();
				JUnitHTMLReporter.report("<b>***Test duration: "+ CurrenDateTime.getDurationMinutes(start, end) + "***</b>");

				JUnitHTMLReporter.tearDown(true,TestProperties.getToEmailAddr(),TestProperties.getEmailTitle()+ collegeName + success);
			}
			catch (IOException exc)
			{
				System.out.println("***Test failed: Unable to open log file: " + exc.toString() + " ***");
			}
			//try {Runtime.getRuntime().exec("taskkill /F /IM ChromeDriver.exe");}
			//catch(IOException exc) {}
		}
		
	}
	
	@Test
	public static boolean processURL(RAWebSite website, String url, String dbName)
	{
		boolean failure = false;
		stepNumber = "L0";
		WebDriver driver = createDriver();
		if (url == null || url.isEmpty())
		{
			String desc = "Warning: <span style=\"color: red\">College: "+website.getCollege()+"</span> <span style=\"color: blue\">Please set full URL</span> for <span style=\"color: green\">DB Name: "+dbName+"</span>. The URL is not set.";
			JUnitHTMLReporter.report(""+desc+"");
			return failure;
		}
		try {
			driver.get(url);
			String actualTitle = driver.getTitle();
			stepNumber = "L1";
			//System.out.print("\n2\n");
			actualTitle = actualTitle.replaceAll("(\\r|\\n)", "");
			Assert.assertTrue("Expected login title did not appear. Actual title is \""+actualTitle + "\"", actualTitle.contains(website.getLoginTitle()));
			//System.out.print("\n3\n");
			stepNumber = "L2";
	        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);
    		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(website.getLoginUserCSSpath()))); //wait until login user name appears

			driver.findElement(By.cssSelector(website.getLoginUserCSSpath())).sendKeys(website.getLoginUser());
			stepNumber = "L3";
			if (! website.getLoginPwd().equals(""))
			{
				driver.findElement(By.cssSelector(website.getLoginPwdCSSpath())).sendKeys(website.getLoginPwd());
			}
			stepNumber = "L4";
			driver.findElement(By.cssSelector(website.getLoginSubmitCSSpath())).sendKeys(Keys.ENTER);//works better than click
			stepNumber = "L5";
			//wait for login screen to be replaced by database screen
			fluentWaitAfterLogin(website.getLoginTitle(), 15, driver);
			stepNumber = "L6";
			String testTitle = driver.getTitle();
			stepNumber = "L7";
			//make sure that after login screen title changed 
			Assert.assertFalse("Tried to login, but was not successfull. Expected to have after login screen, but is still on login.", testTitle.contains(website.getLoginTitle()));
			stepNumber = "L8";
			//we are still move from the login screen
			if(isElementPresent(driver, By.xpath(website.getLoginErrorXpath())))
			{
				System.out.print(driver.findElement(By.xpath(website.getLoginErrorXpath())).getText());
			}
			stepNumber = "L9";
			Assert.assertFalse("Login error. Please try again to login message appeared.",isElementPresent(driver, By.xpath(website.getLoginErrorXpath())));
			
			stepNumber = "L10";
			//title[contains(text(),'Host Needed')] #ezproxy error might appear
			Assert.assertFalse("\"Host Needed\" title appeared. There is RA error, please review.", driver.getTitle().equals("Host Needed"));
			
			stepNumber = "L10a";
			//title[contains(text(),'404 error')]
			Assert.assertFalse("\"404\" in title appeared. There is RA error, please review.", driver.getTitle().contains("404"));
			
			stepNumber = "L10b";
			//proxy error messages 3,5,6,9 have //h3[contains(text(),'ERROR MESSAGE ...')]
			Assert.assertFalse("\"ERROR MESSAGE\" in header element appeared. There is RA error, please review.", (! driver.findElements(By.xpath("//h3[contains(text(),'ERROR MESSAGE')]")).isEmpty()));
			
			stepNumber = "L10c";
			//it asks to login after the login //form @login='xxloginxx'
			Assert.assertFalse("Additional login screen appeared, please review.", (! driver.findElements(By.xpath("//form[contains(@name, 'login')]")).isEmpty()));
						
			stepNumber = "L11";
			//eztest, ezcentennial #If the URL does not have one of them put warning
			if ((driver.getCurrentUrl().indexOf("eztest") == -1) && (driver.getCurrentUrl().indexOf("ezcentennial") == -1))
			{
				//there are several sites for which it is OK to redirect without ez in URL
				boolean ok=false;
				for (String exc : TestProperties.getEzExceptions())
				{
					if (driver.getCurrentUrl().indexOf(exc) > -1) 
					{
						ok=true;
					}
				}
				//exceptions
				if (ok == false)
				{
					String desc = "Warning: <span style=\"color: red\">College: "+website.getCollege()+"</span> <span style=\"color: blue\">URL: "+url+"</span> <span style=\"color: green\">DB Name: "+dbName+"</span> does not have eztest or ezcentennial in the URL.";
					JUnitHTMLReporter.report(""+desc+"");
				}
			}
			testSuccesscount ++;
		}
		catch (AssertionError e)
		{
			failure=true;
			String desc = "***Assertion Error: <span style=\"color: red\">College: "+website.getCollege()+"</span> <span style=\"color: blue\">URL: "+url+"</span> <span style=\"color: green\">DB Name: "+dbName+"</span> Last step: "+ stepNumber  + " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		catch (UnhandledAlertException e)
		{
			failure=true;
			String desc = "***Error: <span style=\"color: red\">College: "+website.getCollege()+"</span> <span style=\"color: blue\">URL: "+url+"</span> <span style=\"color: green\">DB Name: "+dbName+"</span> Last step: "+ stepNumber  + " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		catch (NoSuchElementException e)
		{
			failure=true;
			String desc = "***Error: <span style=\"color: red\">College: "+website.getCollege()+"</span> <span style=\"color: blue\">URL: "+url+"</span> <span style=\"color: green\">DB Name: "+dbName+"</span> Last step: "+ stepNumber  + " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		catch (Exception e)
		{
			failure=true;
			String desc = "***Error: <span style=\"color: red\">College: "+website.getCollege()+"</span> <span style=\"color: blue\">URL: "+url+"</span> <span style=\"color: green\">DB Name: "+dbName+"</span> Last step: "+ stepNumber  + " Exception:" + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		finally
		{
			destroyDriver(driver);
		}
		return failure;
	}
	
	public static boolean processURLs(String collegeName)
	{
		boolean failure = false;
		RAWebSite website = new RAWebSite();
		website = new RAWebSite(collegeName);
		try
		{
	    	website.getCollegeConfigFromFile();
			for (Map.Entry<String, String> mapElement : hm_collegeURLs.entrySet()) { 
				  
	            String url = mapElement.getKey(); 
	            String db = mapElement.getValue(); 
	            boolean testFailure = processURL(website, url, db);
	            if (testFailure)
	            {failure = true;}
	            // print the key : value pair 
	            System.out.println(url + " : " + collegeName); 
	        } 
		}catch (FileNotFoundException e) {
			failure = true;
			String desc = "Error: Unabe to read configuration from College Config file " + TestProperties.getCfgFile() + " " + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		return failure;
	}
	
	/* Create a hashmap of URLs for colleges
	 * Opens Ezra website and reads the table columns College and FullURLs (currently around 800)
	 */
	@Test
	public static boolean findURLs(String collegeName)
	{		
		//0 - means no errors, 255 - fatal, no need to continue with the college, 1 - try another link
		boolean failure = false;
		WebDriver driver = createDriver();
		String url = "";
		String dbName = "";
		String college = "";
		try
		{
			driver.get("http://ezra.ocls.ca/");

			String expectedTitle = "OCLS Connection Manager - Login";
			String ezraTitle = driver.getTitle();
			stepNumber = "1";

			Assert.assertTrue("Expected login title did not appear. Actual title is \""+ezraTitle + "\" , but should be \"" + expectedTitle +"\"", ezraTitle.contains(expectedTitle));
			
			stepNumber = "2";
			driver.findElement(By.xpath("//input[@id='UserName']")).sendKeys("usr");
			stepNumber = "3";
			driver.findElement(By.xpath("//input[@id='Password']")).sendKeys("pwd");
			stepNumber = "4";
			driver.findElement(By.xpath("//input[@value='Log in']")).sendKeys(Keys.ENTER);
			stepNumber = "5";
			fluentWait("OCLS Connection Manager - Remote Authentication", 15, driver);
			stepNumber = "6";
			driver.findElement(By.xpath("//span[@class='t-icon t-arrow-down']")).click();
			stepNumber = "7";
			driver.findElement(By.xpath("//li[contains(text(),'10000')]")).click();
			stepNumber = "8";
			String status = "";
			status = driver.findElement(By.xpath("//div[@class='t-status-text']")).getText();
			stepNumber = "9";
			while (status.equals("Displaying items 0 - 0 of 0"))
			{
				status = driver.findElement(By.xpath("//div[@class='t-status-text']")).getText();
			}
			int tableSize = 0;
	        Pattern pattern = Pattern.compile("(\\d+)$");
	        Matcher matcher = pattern.matcher(status);
	        stepNumber = "10";
	        if(matcher.find()) {
	        	//System.out.println("The status = " + status);
	        	tableSize =  Integer.parseInt(matcher.group(1));
	        }
	        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);
    		wait.until(ExpectedConditions.textToBe(By.xpath("//div[@class='t-status-text']"), "Displaying items 1 - "+String.valueOf(tableSize)+" of "+String.valueOf(tableSize)));
    		
	        //System.out.println("***After parse***");
	        stepNumber = "11";
	        for (int i=0;i<tableSize;i++)
	        {
	        	String stri = String.valueOf(i+1);

	        	college = getElementText(driver, "//div[@id='RemoteAuthenticationsGrid']//tr["+stri+"]//td[1]").toLowerCase();//need lowercase
	        	//WebElement table = driver.findElement(By.xpath("//div[@class='t-grid-content']//table"));
	            //List<WebElement> allRows = table.findElements(By.tagName("tr"));
	        	//WebElement row = driver.findElement(By.xpath("//div[@id='RemoteAuthenticationsGrid']//tr["+stri+"]"));
        		//college = row.getText();
        		stepNumber = "12";
        		if (college.equals(collegeName))
        		{
	        		url=getElementText(driver, "//div[@id='RemoteAuthenticationsGrid']//tr["+stri+"]//td[5]");
	        		dbName=getElementText(driver, "//div[@id='RemoteAuthenticationsGrid']//tr["+stri+"]//td[3]");
	        		hm_collegeURLs.put(url, dbName);// add a pair to hashmap unique url=>college
	        		System.out.println("***In the loop ezra***"+ String.valueOf(i)+ " " +college+ " " +url+ "***");
        		}
 	        }
		} catch (TimeoutException e) {
			failure = true;
			String desc = "*** Error: Timeout exception in site EZRA.OCLS.CA. <span style=\"color: red\">College: "+collegeName+"</span> <span style=\"color: blue\">URL: "+url+"</span><span style=\"color: green\">DB name: "+dbName+"</span> Step:" + stepNumber+ " " + e.toString();
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		} catch (NoSuchElementException e) {
			failure = true;
			String desc = "***Error: Expected element did not appear in EZRA.OCLS.CA. <span style=\"color: red\">College: "+collegeName+"</span> <span style=\"color: blue\">URL after: "+url+"</span><span style=\"color: green\">DB name: "+dbName+"</span> Step:" + stepNumber+ " " + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		} catch (AssertionError e) {
			failure = true;
			String desc = "***Error: The following assertion error happened in EZRA.OCLS.CA. <span style=\"color: red\">College: "+collegeName+"</span> <span style=\"color: blue\">URL after: "+url+"</span> Step:" + stepNumber+ " "  + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}catch (StaleElementReferenceException e) {
			failure = true;
			String desc = "***Error: Unable to read next row in the table in EZRA.OCLS.CA. <span style=\"color: red\">College: "+collegeName+"</span> <span style=\"color: blue\">URL after: "+url+"</span><span style=\"color: green\">DB name: "+dbName+"</span> Step:" + stepNumber+ " "  + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}catch (Exception e) {
			failure = true;
			String desc = "***Error: Unknown error in the table in EZRA.OCLS.CA. <span style=\"color: red\">College: "+collegeName+"</span> <span style=\"color: blue\">URL after: "+url+"</span><span style=\"color: green\">DB name: "+dbName+"</span> Step:" + stepNumber+ " "  + e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}finally
		{
			destroyDriver(driver);
		}
		return failure;
		
	}
	
	public static boolean pingProcess (String cmd)
	{
		boolean failure = false;
		try
		{

			Process process = Runtime.getRuntime().exec(cmd);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String report = "";
		    String line = "";
		    while ((line = reader.readLine()) != null) {
		        report = report.concat(line);
		    }
		    System.out.println("*******************************");
		    System.out.println(report);
		    System.out.println("*********###########***********");
		    Pattern pattern = Pattern.compile("Lost = 0 \\(0\\% loss\\)");
	        Matcher matcher = pattern.matcher(report);
	        if(!matcher.find()) {
	        	failure = true;
				String desc = "***Error: Ping failure "  + cmd + " Plase review the txt file. ***";
				JUnitHTMLReporter.report("<b>"+desc+"</b>");
				System.out.println(desc);
	        }
	        else
	        {
	        	JUnitHTMLReporter.report("<b>The command \""+cmd+"\" is successfull</b>");
	        }
		}
		catch (IOException e)
		{
			failure = true;
			String desc = "***Error: Unable to run command " + cmd + " "+ e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		catch (Error e)
		{
			failure = true;
			String desc = "***Error in InputStreamReader: " + cmd + " "+ e.toString() + " ***";
			JUnitHTMLReporter.report("<b>"+desc+"</b>");
			System.out.println(desc);
		}
		
		return failure;
	}
	
	public static String getElementText(WebDriver driver, String xpath)
	{
		String url = "";
		
		for (int i=1;i<=100;i++)
        {
			try {
				url = driver.findElement(By.xpath(xpath)).getText();
				return url;
			}
			catch (StaleElementReferenceException ar) {
				driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			}
        }
		url = driver.findElement(By.xpath(xpath)).getText();
		return url;
	}
	
	
	public static WebDriver createDriver()
	{
		WebDriver driver;
		//setup driver location depending on OS
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		options.addArguments("disable-infobars"); //to prevent the yellow infobar from appearing, like "You are using unsupported command line flag ..."
		//		options.addArguments("--disable-gpu");
		options.addArguments("enable-automation");

		if (TestProperties.getOS().startsWith("Windows"))
		{
			System.setProperty("webdriver.chrome.driver", TestProperties.getWindowsChromeDriverLocation());
		}
		else
		{
			System.setProperty("webdriver.chrome.driver", TestProperties.getLinuxChromeDriverLocation());

		}
		disableChromeImages(options);//not to load images
		driver = new ChromeDriver(options);
		
		// Put an Implicit wait, this means that any search for elements on the page could take the time the implicit wait is set for before throwing exception
        //driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		return driver;
	}
	
    public static void disableChromeImages(ChromeOptions options)
    {
        HashMap<String, Object> images = new HashMap<String, Object>();
        images.put("images", 2);

        HashMap<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values", images);

        options.setExperimentalOption("prefs", prefs);
    }
	
	public static void destroyDriver(WebDriver driver)
	{
		driver.close();
		driver.quit();
	}
	
	public static String[] initCollegeArray(String[] args)
	{
		String[] collegeArray = new String[args.length];
		int j = 0;
		for (String s: args) 
		{
			collegeArray[j]=s;
			j++;
		}

		return collegeArray;
	}
		
	public static void setupTestReporter(String college)
	{
		//reporter setup
		try {
			if (TestProperties.getOS().startsWith("Windows"))
			{
				JUnitHTMLReporter.setUp(TestProperties.getWindowsReportFile()+ "_"+college+ ".html");
			}
			else
			{
				JUnitHTMLReporter.setUp(TestProperties.getLinuxReportFile()+ "_"+ college+ ".html");
			}
		}
		catch (IOException exc)
		{
			System.out.println("***Test failed: Unable to open log file: " + exc.toString() + " ***");
		}
	}
	
	public static boolean isElementPresent(WebDriver driver, By by)
	{
        try{
            driver.findElement(by);
            return true;
        }
        catch(NoSuchElementException e){
            return false;
        }
    }
	
	public static void fluentWait(String title, int timeout, WebDriver driver) 
	{
	 		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout)).pollingEvery(Duration.ofSeconds(1)).ignoring(Exception.class);
		wait.until(ExpectedConditions.titleContains(title));
	}
	public static void fluentWaitAfterLogin(String title, int timeout, WebDriver driver) 
	{
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout)).pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);
		wait.until(ExpectedConditions.not(ExpectedConditions.titleContains(title)));
	}

}

	
