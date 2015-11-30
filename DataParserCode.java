/* Original Author: Pratima Kshetry*******************************************************************************
	 * This Parser Code implementation is meant to parse Amazon data set aailable at http://snap.stanford.edu/data/amazon-meta.html
	 
	 * The meta data is of following type
	 * 
	 * Sample data example********************************************************************************************
	 * 
	 * Id:   1
	   ASIN: 0827229534
	   title: Patterns of Preaching: A Sermon Sampler
	   group: Book
	   salesrank: 396585
	   similar: 5  0804215715  156101074X  0687023955  0687074231  082721619X
	   categories: 2
	    |Books[283155]|Subjects[1000]|Religion & Spirituality[22]|Christianity[12290]|Clergy[12360]|Preaching[12368]
	    |Books[283155]|Subjects[1000]|Religion & Spirituality[22]|Christianity[12290]|Clergy[12360]|Sermons[12370]
	   reviews: total: 2  downloaded: 2  avg rating: 5
	    2000-7-28  cutomer: A2JW67OY8U6HHK  rating: 5  votes:  10  helpful:   9
	    2003-12-14  cutomer: A2VE83MZF98ITY  rating: 5  votes:   6  helpful:   5

	****************************************************************************************************************/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
public class AmazonDataParser {
   
   private BufferedReader reader=null;
   private  String inputLine=null;
   private String  filePath=null;
   private Map<String, AmazonCustomerProfile> CustomerProfiles=null;
   private String currentProductID=null;
   private String currentProductTitle=null;
   public AmazonDataParser(String filePath)
   {
	   this.filePath=filePath;	  
	   this.CustomerProfiles= new  HashMap<String, AmazonCustomerProfile>();
   }
   
   public void parse()
   {
	   try
	   {
		   if(reader!=null) reader.close();
		   reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)), 1024*100);
    	   inputLine=reader.readLine();
    	   while(inputLine!=null)
    	   {
    		   if(inputLine.startsWith("Id:"))
    		   {
    			   this.currentProductID=extractProductID(inputLine);
    			   inputLine=processInputLines(reader);    		   
    		   }
    		   else
    		   {
    			   inputLine=reader.readLine();
    		   }
    				   
    		   //System.out.println(inputLine);
    	   }
    	   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
       finally{
	    	try
	    	{
	    		if(reader!=null) reader.close();
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
    	
       }
	   
   }
   
	private  String processInputLines(BufferedReader reader)throws IOException
	{
		String line=reader.readLine();
		System.out.println("\n****[Start]*****");
		while(line!=null && !line.startsWith("Id:"))
		{
			System.out.println(line);
			parseLine(line); //Important parses each line
			line=reader.readLine();
		}
		System.out.println("\n****[END]****");
		return line;
	}
	
	private void parseLine(String input)
	{
		//Implement regular expression to parse and build customer profile
		input=input.trim();
		if(input.startsWith("title:")) 
		{	
			this.currentProductTitle=extractProductTitle(input);
			
		}
		if(input.contains("cutomer:") ) extractCustomerProfile(input);
		
	}
	
	private String extractProductID(String input)
	{
		String extractedText=null;
		if(input!=null && input.startsWith("Id:"))
		{
			int pos=input.indexOf(':');
			extractedText=input.substring(pos+1);
			if(extractedText!=null)
			{
				extractedText=extractedText.trim();
			}
		}
		return extractedText;
	}
	private String extractProductTitle(String input)
	{
		String extractedText=null;
		if(input==null) return null;
		input=input.trim();
		if(input.startsWith("title:"))
		{
			int pos=input.indexOf(':');
			extractedText=input.substring(pos+1);
			if(extractedText!=null)
			{
				extractedText=extractedText.trim();
			}
		}
		return extractedText;
	}
	
	
	private AmazonCustomerProfile extractCustomerProfile(String input)
	{
		//String extractedText=null;
		AmazonCustomerProfile custProfile=null;
		if(input!=null)
		{
			input=input.trim();
			if(input.contains("cutomer:"))
			{
				/*int pos=input.indexOf("cutomer:");
				extractedText=input.substring(pos+1);
				if(extractedText!=null)
				{
					extractedText.trim();
				}*/
				
				//test.s
				String[]splitString=input.split(".*cutomer:|\\s+rating:|\\s+votes:|\\shelpful:");
			    //Must contain 5 characters
				if(splitString.length==5)
				{
					
					String customerID=splitString[1].trim();
					if(CustomerProfiles.containsKey(customerID))
					{
						custProfile=CustomerProfiles.get(customerID);
					}
					else
					{
						custProfile=new AmazonCustomerProfile(customerID);
						CustomerProfiles.put(customerID, custProfile);
						
					}
					AmazonProductProfile product=new AmazonProductProfile();
					product.ID=currentProductID;
					product.Title=currentProductTitle;
					try
					{
						product.Rating=Integer.parseInt(splitString[2].trim());
					}
					catch(Exception e)
					{
						product.Rating=-1;
					}
					custProfile.AddProductProfile(product);					
				}
				
				
			}
		}
		return custProfile;
	}
	
	
	
	public void printCustomersProfile()
	{
		for(String key:CustomerProfiles.keySet())
		{
			AmazonCustomerProfile  profile=CustomerProfiles.get(key);
			System.out.println(profile.toString());
		}
	}
	

}
