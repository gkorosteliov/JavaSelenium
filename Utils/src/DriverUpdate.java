package seleniumUtils;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager; //added into build path and in Referenced libraries

public class DriverUpdate {
	//Uses https://github.com/bonigarcia/webdrivermanager
	//Jar downloaded from https://jar-download.com/artifacts/io.github.bonigarcia/webdrivermanager
	
	public static final String CHROME_VER_CMD = "C:\\Windows\\System32\\reg.exe  query \"HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon\" /v version";
	public static final String CHROME_DRIVER_LOCATION = "C:\\Selenium\\chromedriver.exe";
	
	public static void checkAndUpdate() throws Exception 
	{
		System.out.println("ChromDriverCheck");
		try {
			String windowsChromeVer = execCmdSync(CHROME_VER_CMD);
			System.out.println(windowsChromeVer);
			//HKEY_CURRENT_USER\Software\Google\Chrome\BLBeacon
		    //version    REG_SZ    85.0.4183.83
			int chromeVerToCompare = parseVer(windowsChromeVer, "REG_SZ\\s+(\\d+)\\.");
			System.out.println(String.valueOf(chromeVerToCompare));
			String chromeDriverVerInstalledFull = execCmdSync(CHROME_DRIVER_LOCATION + " --v");
			int chromeDriverVerInstalled = parseVer(chromeDriverVerInstalledFull, "ChromeDriver\\s+(\\d+)\\.");
			//ChromeDriver 84.0.4147.30 (48b3e868b4cc0aa7e8149519690b6f6949e110a8-refs/branch-heads/4147@{#310})
			
			if (chromeDriverVerInstalled < chromeVerToCompare)
			{
				//String chromeDriverVerToInstallFull = getChromeDriverVerNum("https://chromedriver.storage.googleapis.com/LATEST_RELEASE");
				//ChromeDriverManager.chromedriver().forceDownload().driverVersion(chromeDriverVerToInstallFull);
				ChromeDriverManager.chromedriver().setup();
				String ver = ChromeDriverManager.chromedriver().getDownloadedDriverVersion();
				System.out.println(ver);
				String path = ChromeDriverManager.chromedriver().getDownloadedDriverPath();
				System.out.println(path);
				File source = new File(path);
				File dest = new File(CHROME_DRIVER_LOCATION);
				Runtime.getRuntime().exec("taskkill /F /IM ChromeDriver.exe");
				Files.copy(source.toPath(), dest.toPath(),StandardCopyOption.REPLACE_EXISTING);
			}
		}		
		catch (IOException e)
		{
			//System.out.println("Exception: Unable to copy file " + e.getMessage());
			throw new Exception ("Exception: Unable to copy CromeDriver file to " + e.getMessage());
		}
		catch (Exception e)
		{
			//System.out.println("Exception: Unable to copy file " + e.getMessage());
			throw new Exception ("Exception: Unable to update CromeDriver " + e.getMessage());
		}
		
	}
	
	public static String getChromeDriverVerNum(String url) throws Exception {
		String ver = "";
		
		URL chromVerPage = new URL(url);
		BufferedReader in = new BufferedReader(
		new InputStreamReader(chromVerPage.openStream()));

        if ((ver = in.readLine()) != null)
        {
        	//System.out.println(ver);
        }
        in.close();
		
		if ((ver == null) || (ver.equals("")))
	    {
	    	throw new Exception("Exception: Unable to parse ChromeDriver version from: " + url);
	    }
		return ver;
	}
	
	public static int parseVer(String str, String patternStr) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);

        int ver = 0;
        if(matcher.find()) {
        	try {
        		ver =  Integer.parseInt(matcher.group(1));
        		//System.out.println("Version = " + String.valueOf(ver));
        	}
        	catch (NumberFormatException ex)
        	{
        		throw new Exception("Exception: Unable to convert to a number Google Chrome version from: " + str);
        	}
        }
        if (ver == 0)
	    {
	    	throw new Exception("Exception: Unable to parse Google Chrome version from: " + str);
	    }
		return ver;
	}
	
	public static String execCmdSync(String cmd) throws java.io.IOException, Exception {
	    Process proc = Runtime.getRuntime().exec(cmd);

	    BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

	    StringBuffer stdOut = new StringBuffer();
	    StringBuffer errOut = new StringBuffer();

	    // Read the output from the command:
	    String s = null;
	    while ((s = stdInput.readLine()) != null) {
	        //System.out.println(s);
	        stdOut.append(s);
	    }

	    // Read any errors from the attempted command:
	    while ((s = stdError.readLine()) != null) {
	        //System.out.println(s);
	        errOut.append(s);
	    }
	    
	    String errors = errOut.toString();
	    String res = stdOut.toString();
	    
	    if (!errors.equals(""))
	    {
	    	throw new Exception("Exception updating Google Chrome: Unable to run command line: " + cmd);
	    }
	    	
	    return res;
	}

}

