package seleniumUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class JUnitHTMLReporter {

	static BufferedWriter junitWriter;
	static String emailbody;

	@BeforeClass
	public static void setUp(String junitReportFile) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
		Date date = new Date();
		File junitReport = new File(junitReportFile);
		junitWriter = new BufferedWriter(new FileWriter(junitReport, false)); //true is to append to existing file
		junitWriter.write("<html><body>");
		junitWriter.write("<h1>Test Execution Summary - " + dateFormat.format(date)
				+ "</h1>");
		emailbody = "<h2>Test Execution Summary - " + dateFormat.format(date) + "</h2>\n";
	}

	@AfterClass
	public static void tearDown(boolean sendEmail, String addr, String title) throws IOException {

		junitWriter.write("</body></html>");
		junitWriter.close();
		if (sendEmail) //send email only in case of errors
		{EmailUtil.sendEmail(addr, title, emailbody);}
		//to open the report in a browser
		//Desktop.getDesktop().browse(junitReport.toURI());

	}
	
	public static void report(String desc) {
		try {
			junitWriter.write("<p>" + desc + "</p>");
			emailbody += "<p>" + desc + "</p>";
		}
		catch (IOException e)
		{}
	}
}