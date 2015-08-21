package org.infinispan.tutorial.remote;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Rest example accessing Infinispan Cache.
 */
public class RestExample {

   /**
    * Method that puts a String value in cache.
    * @param urlServerAddress
    * @param value
    * @throws IOException
    */
   public void putMethod(String urlServerAddress, String value) throws IOException {
      System.out.println("----------------------------------------");
      System.out.println("Executing PUT");
      System.out.println("----------------------------------------");
      URL address = new URL(urlServerAddress);
      System.out.println("executing request " + urlServerAddress);
      HttpURLConnection connection = (HttpURLConnection) address.openConnection();
      System.out.println("Executing put method of value: " + value);
      connection.setRequestMethod("PUT");
      connection.setRequestProperty("Content-Type", "text/plain");
      connection.setDoOutput(true);

      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
      outputStreamWriter.write(value);
         
      connection.connect();
      outputStreamWriter.flush();
       
      System.out.println("----------------------------------------");
      System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
      System.out.println("----------------------------------------");
         
      connection.disconnect();
   }

   /**
    * Method that gets a value by a key in url as param value.
    * @param urlServerAddress
    * @return String value
    * @throws IOException
    */
   public String getMethod(String urlServerAddress) throws IOException {
      String line = new String();
      StringBuilder stringBuilder = new StringBuilder();

      System.out.println("----------------------------------------");
      System.out.println("Executing GET");
      System.out.println("----------------------------------------");

      URL address = new URL(urlServerAddress);
      System.out.println("executing request " + urlServerAddress);

      HttpURLConnection connection = (HttpURLConnection) address.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "text/plain");
      connection.setDoOutput(true);

      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

      connection.connect();

      while ((line = bufferedReader.readLine()) != null) {
         stringBuilder.append(line + '\n');
      }

      System.out.println("Executing get method of value: " + stringBuilder.toString());

      System.out.println("----------------------------------------");
      System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
      System.out.println("----------------------------------------");

      connection.disconnect();

      return stringBuilder.toString();
   }

   /**
    * Main method example.
    * @param args
    * @throws IOException
    */
   public static void main(String[] args) throws IOException {
      RestExample restExample = new RestExample();
      restExample.putMethod("http://localhost:8080/rest/default/1", "Infinispan REST Test");
      restExample.getMethod("http://localhost:8080/rest/default/1");         
   }
}