package com.acko.assignment;

import io.restassured.response.Response;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.get;

public class XMLTest {

    @Test()
    public void compareGetRequests() throws Exception {
        Response response1 = get("https://samples.openweathermap.org/data/2.5/weather?q=London&mode=xml&appid=b6907d289e10d714a6e88b30761fae22");
        Response response2 = get("https://maps.googleapis.com/maps/api/place/textsearch/xml?query=Churchgate%20Station&key=AIzaSyC-zh0WKJq4IeDaE0CD6xjEEafF68vGe80");
        Response response3 = get("https://samples.openweathermap.org/data/2.5/weather?q=London&mode=xml&appid=b6907d289e10d714a6e88b30761fae22");

        assertXMLEquals(response1.asString(), response3.asString());
    }

    public static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

        DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));
        System.out.println(diff.similar());

        List<?> allDifferences = diff.getAllDifferences();
        System.out.println("Difference: \n" + diff.toString());
        System.out.println("Difference Size: \n" + allDifferences.size());
    }

}
