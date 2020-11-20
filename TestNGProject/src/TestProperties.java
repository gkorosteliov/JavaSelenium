package OCLSWebsiteTests;
public final class TestProperties {

    private static String cfgFile = "Colleges.cfg";
    
    private static String os = System.getProperty("os.name");
    
    private static String windowsReportFile = System.getProperty("user.dir") + "\\OCLSWebsiteTestResults";
    private static String windowsChromeDriverLocation = "C:\\Selenium\\chromedriver.exe";
        
    private static int implicitWaitTime = 5;//seconds wait for page load
    
    private static String[] collegeArray = {"algonquin","boreal","cambrian","canadore","centennial","conestoga","confederation","durham","fanshawe","fleming","georgebrown","georgian","humber","lacite","lambton","loyalist","mohawk","niagara","northern","sault","seneca","sheridan","stclair","stlawrence"};
	
	private static String toEmailAddr = "rt@ocls.ca";
	private static String emailTitle = "Test results for OCLS Website test ";
	private static String[] ezExceptions = {};

	public static String[] getEzExceptions() {
        return ezExceptions;
    }
	
    public static String getCfgFile() {
        return cfgFile;
    }
    
    public static String getWindowsChromeDriverLocation() {
        return windowsChromeDriverLocation;
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
