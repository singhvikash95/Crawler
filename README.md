# Crawler
Web Crawler is a program used by search engines to discover what is new on Internet, or on a particular website. Web Crawler begins by crawling the pages of a website in order to index the
words and contents found on that website. Then it visits the links available on that website.
I did one such implementation for crawling the Thomson Reuters company ranking pages.

1) Java code is written that can crawl through the pages and scrape different sectors, companies in
those sectors, and people working in those companies.

2) From the spreadsheet provided, the industries, permanent ID and hierarchical ID are mapped to a
HashMap with Industry name being the key for the HashMap.
Also, we are storing this simultaneously in a CSV file named Industry.csv.

3) Now we are ready with all the key values. The company titles are then scraped from the main
URL provided and mapped to the respective key values in the HashMap.

4) The mapped URLs correspond to a page that contains the information of the companies
belonging to a specific industry. Hence we can extract Tickers, Names, Market Capitalization,
TTM Sales, Employees. This data is getting stored in Company.csv.

5) A HashSet is used for temporarily storing the URLs for the companies that were extracted in the
previous step.

6) Such URLs redirects us to the employee’s information such as Name, Age, Since(Joining Year),
Current Position and Biographies.
People.csv gathers the generated employee information.

7) The above stated implementation requires the usage of jsoup, poi, xmlbeans, stax for crawling
and reading xlsx file.

IDE used: Eclipse
Dependencies: 
commons-codec-1.9.jar
jsoup-1.11.2.jar
poi-3.11-beta2.jar
poi-ooxml-3.11-beta2.jar
poi-ooxml-schemas-3.11-beta2.jar
stax-api-1.0.1.jar
xmlbeans-2.6.0.jar
