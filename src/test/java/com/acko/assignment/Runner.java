package com.acko.assignment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static io.restassured.RestAssured.get;

public class Runner {

    /*
    Running in parallel with 100 threads
    Took about 30 secs on my laptop
    Can increase thread count depending on system performance & API response time

    To run tests, run testng.xml file
     */

    @DataProvider(parallel = true)
    public Object[][] getApiRequests() throws IOException {
        BufferedReader reader1 = new BufferedReader(new FileReader(Constants.FILEPATH1));
        BufferedReader reader2 = new BufferedReader(new FileReader(Constants.FILEPATH1));
        BufferedReader reader3 = new BufferedReader(new FileReader(Constants.FILEPATH2));

        // Getting count of requests
        int rowCount = 0;
        while (reader1.readLine() != null)
            rowCount++;

        // Creating 2D array to return as data
        String[][] testData = new String[rowCount][2];

        // Adding File1.txt data to dataprovider's 1st argument
        for (int i = 0; i < rowCount; i++)
            testData[i][0] = reader2.readLine();

        // Adding File2.txt data to dataprovider's 2nd argument
        for (int j = 0; j < rowCount; j++)
            testData[j][1] = reader3.readLine();

        return testData;
    }


    @Test(dataProvider = "getApiRequests")
    public void compareGetRequests(String request1, String request2) {
        Response response1 = get(request1);
        Response response2 = get(request2);

        // Comparing both responses are either json or xml and calling the right comparator method
        if (response1.contentType().contains("json") && response2.contentType().contains("json"))
            compareJson(request1, request2);

        else if (response1.contentType().contains("xml") && response2.contentType().contains("xml"))
            compareXml(request1, request2);

        else
            System.out.println("Responses are in different formats & hence not comparable");
        // We can also convert the responses to either format and then compare
    }

    /*
    Compares json responses from 2 requests passed as arguments
     */
    private void compareJson(String request1, String request2) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode actualObj1 = mapper.readTree(get(request1).asString());
            JsonNode actualObj2 = mapper.readTree(get(request2).asString());

            if (actualObj1.equals(actualObj2))
                System.out.println(request1 + " equals " + request2);
            else
                System.out.println(request1 + " not equals " + request2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Compares xml responses from 2 requests passed as arguments
     */
    private void compareXml(String request1, String request2) {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

        try {
            DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(get(request1).asString(), get(request2).asString()));
            if (diff.similar())
                System.out.println(request1 + " equals " + request2);
            else
                System.out.println(request1 + " not equals " + request2);

        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Rest Assured to get API responses
    TestNG tests can be run in parallel for multiple threads as per system capacity
    Using XML Unit to compare XML responses
    Using Jackson to compare Json responses
     */

}
