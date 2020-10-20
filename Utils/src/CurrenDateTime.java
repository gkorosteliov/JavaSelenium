package seleniumUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;  

 
public class CurrenDateTime {
	
	public static String getDateTime()
	{
	    SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");  
	    Date date = new Date();  
	    return formatter.format(date);
    }
	
	public static long getCurrentTime()
	{
		return new Date().getTime();
	}
	
	public static String getDurationMinutes(long startTime, long endTime)
	{
		long mins = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
		long secs = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime) - (TimeUnit.MILLISECONDS.toMinutes(endTime - startTime)*60);
		return String.valueOf(mins)+ " minute(s) " + String.valueOf(secs)+ " second(s)";
	}
}
