import java.io.IOException;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BlueOptima
{

	void getIndustryInfo(String html, String temp)
	{
		HashMap<String, String> map = new HashMap<String, String>();		//saves industry name, permID and hierarchicalId
		
		try
		{
			FileWriter Industry = new FileWriter("/home/vikash/Desktop/Industry.csv",false);

			XSSFWorkbook book = new XSSFWorkbook(new FileInputStream("/home/vikash/Downloads/BusinessClassificationIndex.xlsx"));
			Sheet sheet = book.getSheetAt(0);
			for (int j=1; j< sheet.getLastRowNum() + 1; j++)		//loop to fill HashMap map
			{
				Row row = sheet.getRow(j);
				Cell cell = row.getCell(3);
				Cell permId = row.getCell(4);
				Cell hierarchicalId = row.getCell(5);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				permId.setCellType(Cell.CELL_TYPE_STRING);
				hierarchicalId.setCellType(Cell.CELL_TYPE_STRING);
				String id = permId.toString()+ ";" +hierarchicalId.toString();
				map.put(cell.toString(), id);
			}
			final String INDUSTRY_HEADER = "Industry;PermId;HierarchicalId;Thomson Reuters Company Ranking URL";
			Industry.append(INDUSTRY_HEADER);
			Industry.append('\n');
			for(int k=1;k<293;k++)				//maps URLs to permId and hierarchicalID
			{
				Document doc = Jsoup.connect(html+k+temp).get();
				Element header = doc.select("div#sectionTitle").first();		//scrapes the title from the URL
				if(header!=null)
				{
					String Id = null;
					if (map.containsKey(header.text())) 
					{
						Id = map.get(header.text());
						System.out.println(header.text()+";"+Id+";"+html+k);
						Industry.append(header.text()+";"+Id+";"+html+k);
						Industry.append('\n');
						Industry.flush();
					}
				}
			}
			Industry.close();
			book.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	void getPeopleInfo(HashSet<String> people) throws IOException
	{
		HashMap<String, String> Summary = new HashMap<String, String>();		//stores employee imformation
		FileWriter People = new FileWriter("/home/vikash/Desktop/People.csv",false);
		final String PEOPLE_HEADER = "Company;Name;Age;Since;Current Position;Description";
		People.append(PEOPLE_HEADER);
		People.append('\n');
		String Key,Value;
		for(String s : people)				//loop to scrape information from URLs fed to this function
		{
			Document Doc = Jsoup.connect(s).get();
			Elements TableElements = Doc.select("div.column1");
			Element Title = Doc.select("title").first();
			int len = Title.text().length();
			Elements TableRowElements = TableElements.select(":not(thead) tr");
			for(int i=0;i<TableRowElements.size();i++)
			{
				Element Row = TableRowElements.get(i);
    		  		Elements RowItems = Row.select("td");
    		  		Key="";Value="";
    		  		for(int j=0;j<RowItems.size();j++)
				{
					if(j==0)
						Key=RowItems.get(0).text();
					else
						Value+=RowItems.get(j).text()+";";
				}
				if(Summary.containsKey(Key))
				{
					String a = Summary.get(Key);
					String Temp = a+Value;
					Summary.put(Key, Temp);
					System.out.println(Title.text().substring(0,len-20)+";"+Key+";"+Temp);
					if(Key.length()!=0)
					{
						People.append(Title.text().substring(0,len-20)+";"+Key+";"+Temp);
						People.append('\n');
						People.flush();
					}
				}
				else
					Summary.put(Key,Value);
    		  		System.out.println();
			}
		}
		People.close();
		
	}


	void getCompanyInfo(String html, String temp)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		HashSet<String> links = new HashSet<String>();
		HashSet<String> people = new HashSet<String>();		//stores the URLs containing employee information
		String url = "https://www.reuters.com";
		
		try
		{
			FileWriter Company = new FileWriter("/home/vikash/Desktop/Company.csv",false);
			final String COMPANY_HEADER = "Industry;PermId;HierarchicalId;Ticker;Name;MarketCapitalization;TTM Sales$;Employees;Companies URL";

			XSSFWorkbook book = new XSSFWorkbook(new FileInputStream("/home/vikash/Downloads/BusinessClassificationIndex.xlsx"));
			Sheet sheet = book.getSheetAt(0);
			
			Company.append(COMPANY_HEADER);
			Company.append('\n');
			for (int j=1; j< sheet.getLastRowNum() + 1; j++)
			{
				Row row = sheet.getRow(j);
				Cell cell = row.getCell(3);
				Cell permId = row.getCell(4);
				Cell hierarchicalId = row.getCell(5);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				permId.setCellType(Cell.CELL_TYPE_STRING);
				hierarchicalId.setCellType(Cell.CELL_TYPE_STRING);
				String id = permId.toString()+ ";" +hierarchicalId.toString();
				map.put(cell.toString(), id);
			}

			for(int k=1;k<293;k++)
			{
				Document doc = Jsoup.connect(html+k+temp).get();
				Element header = doc.select("div#sectionTitle").first();
				if(header!=null)
				{
					String Id = null;
					if (map.containsKey(header.text())) 
					{
						Id = map.get(header.text());
					}

					Elements tableElements = doc.select("#dataTable");		//scrapes company info(tickers, etc.) from the webpage
					Elements tableRowElements = tableElements.select(":not(thead) tr");

					for(int i = 2; i < tableRowElements.size(); i++)
					{
						Company.append(header.text()+";"+Id+";");
						System.out.print(header.text()+";"+Id+";");
						Element row = tableRowElements.get(i);
						Elements rowItems = row.select("td");
						Element link = rowItems.select("a").first();
						for (int j = 0; j < rowItems.size(); j++)
						{
							Company.append(rowItems.get(j).text()+";");
							System.out.print(rowItems.get(j).text()+";");
						}
						if(link!=null)
						{
							Company.append(url+link.attr("href")+";");
							System.out.print(url+link.attr("href")+";");
							links.add(url+link.attr("href"));
						}
						Company.append('\n');
						Company.flush();
						System.out.println();
					}
					for(String s : links)
					{
						Document Doc = Jsoup.connect(s).get();
						Element element = Doc.select("a[href^=\"/finance/stocks/company-officers/\"]").first();
						people.add(url+element.attr("href"));
					}
				
					getPeopleInfo(people);		//scrapes the employee details and save it in People.csv
					links.clear();
					people.clear();
				}
			}
			book.close();
			Company.close();
		}
		
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public static void main(String[] args)
	{
		BlueOptima obj=new BlueOptima();

		String html = "https://www.reuters.com/sectors/industries/rankings?industryCode="; //company ranking URL for generating classification index
		String temp = "&view=size&page=-1&sortby=mktcap&sortdir=DESC";

		obj.getIndustryInfo(html, temp);		//scrapes the industry name and save it in Industry.csv

		obj.getCompanyInfo(html, temp);		//scrapes the company details and save it in Company.csv

	}
}