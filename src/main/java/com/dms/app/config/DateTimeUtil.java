package com.dms.app.config;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {
	
	public static String getSysDateTime(){
		
		/*DateFormat	dateFormat = new SimpleDateFormat("dd-MM-yyy hh:mm:ss a");
		Date date  = new Date();
		String	sysDate    = dateFormat.format(date);*/
		
		LocalDateTime localDate = LocalDateTime.now(ZoneId.of("GMT+05:30"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy hh:mm a");
        String formatDateTime = localDate.format(formatter);
		
		return formatDateTime;
	}
     
	public static String getSysDateTimeMilis(){
						
		LocalDateTime localDate = LocalDateTime.now(ZoneId.of("GMT+05:30"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy hh:mm:ss.SSS a");
        String formatDateTime = localDate.format(formatter);
		
		return formatDateTime;
	}
	
	
    public static String getSysDate(){
		
		/*DateFormat	dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date  = new Date();
		String	sysDate    = dateFormat.format(date);*/
		
		LocalDate localDate = LocalDate.now(ZoneId.of("GMT+05:30"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formatDateTime = localDate.format(formatter);
				
		return formatDateTime;
	}
        
    public static String convertDateFormat(String dateform) throws ParseException{
		
    	DateFormat	dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
    	Date datefm = dateFormat1.parse(dateform);
		DateFormat	dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String	sysDate    = dateFormat.format(datefm);
		
		return sysDate;
	}
    
    public static int getCurrentMonth(){
		
    	int month = 0;
		/*DateFormat	dateFormat = new SimpleDateFormat("MM");
		Date date  = new Date();
		String	sysDate    = dateFormat.format(date);*/
		
		LocalDate localDate1 = LocalDate.now(ZoneId.of("GMT+05:30"));
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM");
        String formatDateTime1 = localDate1.format(formatter1);
		month = Integer.parseInt(formatDateTime1);
		
		return month;
	}
    
    public static String getCurrentMonthYear(){
		
		/*DateFormat	dateFormat = new SimpleDateFormat("MM-yyyy");
		Date date  = new Date();
		String	sysDate    = dateFormat.format(date);*/
		
		LocalDate localDate1 = LocalDate.now(ZoneId.of("GMT+05:30"));
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM-yyyy");
	    String formatDateTime1 = localDate1.format(formatter1);
		
		return formatDateTime1;
	}
    
    public static long getTimeStampInMiliseconds() {
    	 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    	
    	 return timestamp.getTime();
    }
    
    public static String getMonthName(int number){
		String formatedNumber = "";
		switch(number){ 
			 case 1 :
			            formatedNumber = "January"; 
			            break;
	         case 2 :
		        	 	formatedNumber = "February"; 
			            break;
	         case 3 :
		        	 	formatedNumber = "March"; 
			            break;
	         case 4 :
		        	 	formatedNumber = "April"; 
			            break;
	         case 5 :
		        	 	formatedNumber = "May"; 
			            break;
	         case 6 :
		        	 	formatedNumber = "June"; 
			            break;
	         case 7 :
		        	 	formatedNumber = "July"; 
			            break;
	         case 8 :
		        	 	formatedNumber = "August"; 
			            break;
	         case 9 :
		        	 	formatedNumber = "September"; 
			            break;   
			 case 10 :
				   	 	formatedNumber = "October"; 
						break;       
			 case 11 :
					    formatedNumber = "November"; 
						break;     
			 case 12 :
		        	 	formatedNumber = "December"; 
			            break;      			
	        }
		  
		return formatedNumber.trim();  
	}
    
    public static LinkedHashMap<String, String> getMonths(){
		
		LinkedHashMap<String, String> months = new LinkedHashMap<String, String>();
		 
			months.put("01", "January");
			months.put("02", "February");
			months.put("03", "March");
			months.put("04", "April");
			months.put("05", "May");
			months.put("06", "June");
			months.put("07", "July");
			months.put("08", "August");
			months.put("09", "September");
			months.put("10", "October");
			months.put("11", "November");
			months.put("12", "December");
		
		return months;
	}
    
    public static List<Date> getDatesList(String str_date, String end_date) throws ParseException {
    	
    	List<Date> dateList  = new ArrayList<Date>();
    	
    	DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm a"); // hh:mm:ss a
    	
		Date  startDate = (Date)formatter.parse(str_date); 
		Date  endDate   = (Date)formatter.parse(end_date);
		
		long interval = 24*1000*60*60; // 1 hour in millis
		long endTime  = endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
		long curTime  = startDate.getTime();
		
		/*System.out.println("startTime : "+curTime);
		System.out.println("endTime : "+endTime);		
		*/
		while (curTime <= endTime) {
			dateList.add(new Date(curTime));
		   // System.out.println("dates : "+new Date(curTime));	
		    curTime += interval;
		    //System.out.println("interval : "+curTime);	
		}
		/*for(int i=0;i<dateList.size();i++){
		    Date lDate =(Date)dateList.get(i);
		    String ds = formatter.format(lDate);    
		    System.out.println(" Date is ..." + ds);
		}*/
    	//System.out.println("date list : "+dateList.size());
    	return dateList;
    }

	public static String getSysDateTimeFormat() {
		
		/*DateFormat	dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
		Date date  = new Date();
		String	sysDate    = dateFormat.format(date);*/
		
		LocalDateTime localDate1     = LocalDateTime.now(ZoneId.of("GMT+05:30"));
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
	    String formatDateTime1       = localDate1.format(formatter1);
		
		return formatDateTime1;
	}
	
	public static List<Date> getDatesBetween(Date startDate, Date endDate) {
		
		List<Date> datesInRange = new ArrayList<>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
			     
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);
			 
		while (calendar.before(endCalendar)) {
			Date result = calendar.getTime();
			datesInRange.add(result);
			calendar.add(Calendar.DATE, 1);
		}
		return datesInRange;
	}
	
		
	public static String formatDuration(long duration) {
	    long hours = TimeUnit.MILLISECONDS.toHours(duration);
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
	    long milliseconds = duration % 1000;
	    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
	}
}