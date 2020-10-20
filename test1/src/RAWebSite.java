import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RAWebSite {
	private String college;
	private String url;
	private String loginTitle;
	private String loginUserCSSpath;
	private String loginUser;
	private String loginPwdCSSpath;
	private String loginPwd;
	private String loginSubmitCSSpath;
	private String loginErrorXpath;
	private String dbUrlCheckInDbList;
	private String dbUrlCheckOnDBPage;
	
	public RAWebSite(String college, String url, 
			String loginTitle, String loginUserCSSpath, String loginUser,
			String loginPwdCSSpath, String loginPwd, String loginSubmitCSSpath, String loginErrorXpath,
			String dbUrlCheckInDbList, String dbUrlCheckOnDBPage)
	{
		this.college = college;
		this.url = url;
		this.loginTitle=loginTitle;
		this.loginUserCSSpath = loginUserCSSpath;
		this.loginUser = loginUser;
		this.loginPwdCSSpath = loginPwdCSSpath;
		this.loginPwd = loginPwd;
		this.loginSubmitCSSpath = loginSubmitCSSpath;
		this.loginErrorXpath = loginErrorXpath;
		this.dbUrlCheckInDbList = dbUrlCheckInDbList;
		this.dbUrlCheckOnDBPage = dbUrlCheckOnDBPage;
		
	}
	
	public RAWebSite(String college)
	{
		this.college = college;
	}
	
	public RAWebSite()
	{
		this.college = "";
	}
	
	public String getCollege()
	{
		return this.college;
	}
		
	public String getUrl()
	{
		return this.url;
	}
	
	public String getLoginTitle()
	{
		return this.loginTitle;
	}

	public String getLoginUserCSSpath()
	{
		return this.loginUserCSSpath;
	}

	public String getLoginUser()
	{
		return this.loginUser;
	}
	
	public String getLoginPwdCSSpath()
	{
		return this.loginPwdCSSpath;
	}

	public String getLoginPwd()
	{
		return this.loginPwd;
	}

	public String getLoginSubmitCSSpath()
	{
		return this.loginSubmitCSSpath;
	}

	public String getLoginErrorXpath()
	{
		return this.loginErrorXpath;
	}
	
	public String getDbUrlCheckInDbList()
	{
		return this.dbUrlCheckInDbList;
	}
	
	public String getDbUrlCheckOnDBPage()
	{
		return this.dbUrlCheckOnDBPage;
	}

	public void getCollegeConfigFromFile() throws FileNotFoundException
	{
		File file = new File(TestProperties.getCfgFile());
		  
		FileInputStream fileInput = null;
		fileInput = new FileInputStream(file);
		Properties prop = new Properties();
		
		//load properties file
		try {
			prop.load(fileInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.url = prop.getProperty(this.college + ".url");
		this.loginTitle=prop.getProperty(this.college + ".loginTitle");
		this.loginUserCSSpath = prop.getProperty(this.college + ".loginUserCSSpath");
		this.loginUser = prop.getProperty(this.college + ".loginUser");
		this.loginPwdCSSpath = prop.getProperty(this.college + ".loginPwdCSSpath");
		this.loginPwd = prop.getProperty(this.college + ".loginPwd");
		this.loginSubmitCSSpath = prop.getProperty(this.college + ".loginSubmitCSSpath");
		this.loginErrorXpath = prop.getProperty(this.college + ".loginErrorXpath");
		this.dbUrlCheckInDbList = prop.getProperty(this.college + ".dbUrlCheckInDbList");
		this.dbUrlCheckOnDBPage = prop.getProperty(this.college + ".dbUrlCheckOnDBPage");
	}
	
}
