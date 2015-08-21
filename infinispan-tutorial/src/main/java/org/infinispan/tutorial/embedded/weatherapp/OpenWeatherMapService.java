package org.infinispan.tutorial.embedded.weatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.infinispan.Cache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpenWeatherMapService extends CachingWeatherService {
   final private static String OWM_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
   private DocumentBuilder db;

   public OpenWeatherMapService(Cache<String, LocationWeather> cache) {
	  super(cache);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      try {
         db = dbf.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
      }
   }

   private Document fetchData(String location) {
      HttpURLConnection conn = null;
      try {
         String query = String.format("%s?q=%s&mode=xml&units=metric", OWM_BASE_URL,
               URLEncoder.encode(location.replaceAll(" ", ""), "UTF-8"));
         //System.out.println(query); 
         URL url = new URL(query);
         conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Accept", "application/xml");
         
//         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
// 		String responseLine;
// 		while ((responseLine = br.readLine()) != null) {
// 			System.out.println(responseLine);
// 		}
 		
         //commenting to get rid of the 307 response code issue.
         if (conn.getResponseCode() != 200) {
            throw new Exception();
         }
         return db.parse(conn.getInputStream());
      } catch (Exception e) {
         return null;
      } finally {
         if (conn != null) {
            conn.disconnect();
         }
      }
   }

   @Override
   protected LocationWeather fetchWeather(String location) {

      Document dom = fetchData(location);
      Element current = (Element) dom.getElementsByTagName("current").item(0);
      Element temperature = (Element) current.getElementsByTagName("temperature").item(0);
      Element weather = (Element) current.getElementsByTagName("weather").item(0);
      String[] split = location.split(",");
      return new LocationWeather(
            Float.parseFloat(temperature.getAttribute("value")),
            weather.getAttribute("value"),
            split[1].trim());
   }

}
