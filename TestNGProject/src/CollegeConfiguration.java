package OCLSWebsiteTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class CollegeConfiguration {
	private String college;
	private String documentsUrl;
	private String documentsTitle;
	private String subscriptionsUrl;
	private String subscriptionsTitle;
	private String loginUser;
	private String loginPwd;

	
	public CollegeConfiguration(String college, String documentsUrl, 
			String documentsTitle, String subscriptionsUrl, String subscriptionsTitle,
			String loginUser, String loginPwd)
	{
		this.college = college;
		this.documentsUrl = documentsUrl;
		this.documentsTitle=documentsTitle;
		this.subscriptionsUrl = subscriptionsUrl;
		this.subscriptionsTitle = subscriptionsTitle;
		this.loginUser = loginUser;
		this.loginPwd = loginPwd;		
	}
	
	public CollegeConfiguration(String college)
	{
		this.college = college;
	}
	
	public CollegeConfiguration()
	{
		this.college = "";
	}
	
	public String getCollege()
	{
		return this.college;
	}
		
	public String getDocumentsUrl()
	{
		return this.documentsUrl;
	}
	
	public String getDocumentsTitle()
	{
		return this.documentsTitle;
	}

	public String getSubscriptionsUrl()
	{
		return this.subscriptionsUrl;
	}
	
	public String getSubscriptionsTitle()
	{
		return this.subscriptionsTitle;
	}

	public String getLoginUser()
	{
		return this.loginUser;
	}
	
	public String getLoginPwd()
	{
		return this.loginPwd;
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
		
		this.documentsUrl = prop.getProperty(this.college + ".documents");
		this.documentsTitle=prop.getProperty(this.college + ".documentstitle");
		this.subscriptionsUrl = prop.getProperty(this.college + ".subscriptions");
		this.subscriptionsTitle = prop.getProperty(this.college + ".subscriptionstitle");
		this.loginUser = prop.getProperty(this.college + ".user");
		this.loginPwd = prop.getProperty(this.college + ".pwd");
	}
	
}