import com.opencsv.CSVReader
import com.eviware.soapui.support.XmlHolder
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestRunContext

//Initialization of needed values from testCase properties
def fileName = testRunner.testCase.getPropertyValue("file")
def requestName = testRunner.testCase.getPropertyValue("request")

//asserts for those 2 properties
assert fileName != null : "Musis vytvorit fileName property na urovni prislusneho testCase"
assert requestName != null : "Musis vytvorit requestName property na urovni prislusneho testCase"

//check if there is step with such a name
if(testRunner.testCase.testSteps[requestName] == null) {
	throw new Exception("uvedeny request nie je umiestneny v krokoch tohoto testcase-u")	
}

//Creation of CSVReader from provided filename
def providedFile = new File(fileName)
//check if file exists
if(!providedFile.exists()) {
	throw new Exception("Subor neexistuje, skontroluj ci si spravne zadal nazov a pripadne cestu k nemu")	
}

CSVReader reader = new CSVReader(new FileReader(providedFile))

// we read first line with names of rows
String[] rows = reader.readNext()

//then read all other data
List<String[]> data = reader.readAll()

//initialization of Lists for requests and responses, so we can save them after
def responses = new ArrayList<String[]>()
def requests = new ArrayList<String[]>()

//check if number of rows is equals to number of data
assert rows.length == data[0].length : "Pocet nazvov stlpcov a dlzky dat sa nezhoduju"

//cycle for sending requests
for(String[] row in data) {
	//we get XML requests straight from provided requestName
	def xmlReq = testRunner.testCase.testSteps[requestName].getPropertyValue("Request")
	
	//create a XMLHolder
	def sampleReq = new XmlHolder(xmlReq)
	
	//now we directly fill request nodes with our data
	for (def i = 0; i < rows.length; i++) {
		//value of data (rows[i]) should be compatible with name of row (row[i])
		sampleReq.setNodeValue("//" + rows[i], row[i])
	}
	//get final XML
	def finalXml = sampleReq.getXml()
	
	//add xml to our requests
	requests.add(finalXml)
	
	//set Request body property of our request
	testRunner.testCase.testSteps[requestName].setPropertyValue("Request", finalXml)

	//we take the whole Request - right from test steps
	def requestObject = testRunner.testCase.testSteps[requestName]
	
	//creation of context for request
	def contextRequest = new WsdlTestRunContext(requestObject)

	//and we run request
	requestObject.run(testRunner, contextRequest)

	//after request has been processed, we take out response propety 
	def xmlResponse = testRunner.testCase.testSteps[requestName].getPropertyValue('Response')

	//and save it into responses
	responses.add(xmlResponse)
}

//Part for saving responses into txt File
try {
     // Retrieve the project root folder
     def projectPath = new com.eviware.soapui.support.GroovyUtils(context).projectPath

     // Specify a folder inside project root to store the results
     String folderPath = projectPath + "/SoapUIResults";

     // Create a File object for the specified path
     def resultFolder = new File(folderPath);

     // Check for existence of folder and create a folder
     if(!resultFolder.exists()) {
       	resultFolder.mkdirs();
     }

	//Get timestamps for file and folder
     Date d = new Date();
     def executionDate = d.format("dd-MMM-yyyy");
     def executionTime = d.format("HH-mm-ss");

     // Specify the subfolder path with name Request-Response_CurrentTimeStamp
     String subfolderPath1 = folderPath+ "/Request-Response_"+executionDate;

     // Create this sub-folder
     def subResultfolder = new File(subfolderPath1);
     // Check for existence of folder and create a folder
     if(!subResultfolder.exists()) {
       	subResultfolder.mkdirs();
     }


	//Create a txt file for our response data
	def resultsFileName = subfolderPath1 + "/" + requestName + "_" + executionTime + ".txt"
	def resultsFile = new File(resultsFileName);

	//fill file with request-response pairs and separate with bunch of *
	for(def i = 0; i < requests.size(); i++) {
		resultsFile.append("REQUEST: \n")
		resultsFile.append(requests.get(i) + "\n")

		resultsFile.append("\nRESPONSE: \n")
		resultsFile.append(responses.get(i) + "\n")

		resultsFile.append("****************************************************************************************************\n")
	}

   }
   catch(exc){
   log.error("Exception happened: " + exc.toString());
}
//just so you know, you are done
log.info "End of script"
