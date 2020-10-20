public final class TestProperties {

    private static String cfgFile = "Colleges.cfg";
    
    private static String os = System.getProperty("os.name");
    
    private static String windowsReportFile = System.getProperty("user.dir") + "\\RAWebsiteTestResults";
    private static String windowsChromeDriverLocation = "C:\\Selenium\\chromedriver.exe";
    
    private static String linuxReportFile = "/selenium_tests/RAtest-standard/RAWebsiteTestResults.html";
    private static String linuxChromeDriverLocation = "/usr/bin/chromedriver";
    
    private static int implicitWaitTime = 5;//seconds wait for page load
    
    private static String[] collegeArray = {"algonquin","boreal","cambrian","centennial","conestoga","confederation","sandford","georgian","lacite","lambton","northern","sault","stclair","stlawrence"};
	
	private static String toEmailAddr = "rt@ocls.ca";
	private static String emailTitle = "Test results for EZRA full URLs for ";
	private static String[] ezExceptions = {"kanopy.com","westlaw.com","education.transparent.com","nrcresearchpress.com","syrano.demarque.com"};

	public static String[] getEzExceptions() {
        return ezExceptions;
    }
	
    public static String getCfgFile() {
        return cfgFile;
    }
    
    public static String getWindowsChromeDriverLocation() {
        return windowsChromeDriverLocation;
    }
    
    public static String getLinuxChromeDriverLocation() {
        return linuxChromeDriverLocation;
    }

    public static int getImplicitWaitTime() {
        return implicitWaitTime;
    }
    
    public static String[] getCollegeArray() {
        return collegeArray;
    }
    
    public static String getWindowsReportFile() {
        return windowsReportFile;
    }
    
    public static String getLinuxReportFile() {
        return linuxReportFile;
    }
    
    public static String getOS() {
        return os;
    }
    
    public static String getToEmailAddr() {
        return toEmailAddr;
    }
    
    public static String getEmailTitle() {
        return emailTitle;
    }

}
