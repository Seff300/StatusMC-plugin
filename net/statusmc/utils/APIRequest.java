package net.statusmc.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class APIRequest {
   public static boolean checkVersion(String arg1) {
      try {
         String urlString = "https://api.statusmc.net/plugin-version.php";
         URL url = new URL(urlString);
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setRequestMethod("GET");
         connection.setRequestProperty("Accept", "application/json");
         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         StringBuilder content = new StringBuilder();

         String inputLine;
         while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
         }

         in.close();
         connection.disconnect();
         String jsonResponse = content.toString();
         JSONParser jsonParser = new JSONParser();
         JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonResponse);
         Iterator iterator = jsonObject.keySet().iterator();
         if (iterator.hasNext()) {
            String key = (String)iterator.next();
            String keyValue = (String)jsonObject.get(key);
            if (jsonObject.get(key).equals(arg1)) {
               return true;
            }

            return false;
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

      return false;
   }
}
