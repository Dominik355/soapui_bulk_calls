# soapui_bulk_calls
groovy script providing bulk calls for soapui based on csv data

Every step in the script is commented

To get started, you need to download two JAR files : 
        OpenCSV(https://mvnrepository.com/artifact/com.opencsv/opencsv)
        Apache-commons-lang3(https://mvnrepository.com/artifact/org.apache.commons/commons-lang3)
Place these in the folder where you have SoapUI installed and then \ bin \ ext     

Data in CSV format:
    The first line is the names that specify the path directly in the xml request (so that the values can be mapped directly to them).
    Next comes the data itself (it doesn't matter if "," or ";" is used)
  
    Example:
        <soapenv:Body>
          <tem:Add>
            <tem:intA>?</tem:intA>
            <tem:intB>?</tem:intB>
          </tem:Add>
        </soapenv:Body>
        
        For such a request, the first line will look like this: "tem:Add/tem:intA,tem:add/tem:intB".
        It starts without a slash, and then the individual parts are separated by a slash. 
        
Testing procedure: 

    The project in which you will make calls must be saved, otherwise an error will pop up when saving the results.
    1.Create a TestSuite for the project.
    2.Next, create a TestCase.
    3.After clicking TestCase, you will create a new Groovy script step in Test Steps and copy the above script into it.
    4.Next, move the request you want to work with to the Test Steps (just drag it with the mouse, or right-click on the appropriate request and put "Add to TestCase").
    5.Now for the script to work, you need to set 2 properties at the level of the respective TestCase.
      Click on your TestCase, the property will appear at the bottom left, click on custom and add.
      You need 2 properties: "request" - the name of the request you entered and "file" - the full path to the csv data file.
    6.Now just open the script and run it (green arrow at the top left) 
    7.A text file with request-response pairs is created in the path where the project is stored. 
      Example: ProjectPath\SoapUIResults\Request-Response_15-Mar-2021\ nacitajRequest_12-58-26.txt
      A folder will be created according to actual day + the appropriate file, which has a name in the form "NameRequest_hh-mm-ss.txt" 
      
      
Sources:

        https://dzone.com/articles/how-to-achieve-csv-reporting-in-soapui-open-source-1

        http://opencsv.sourceforge.net/

        https://www.youtube.com/watch?v=FxtMsva83CI&list=PLL34mf651faMwvuS3nBju98QgCByRaQQZ
    
