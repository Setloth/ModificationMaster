package me.setloth.modificationMaster.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class VersionChecker {

  public static String latestVersion() {
    String latestVersion = null;
    try {
      // URL to your GitHub repo tags API
      URI uri = new URI("https://api.github.com/repos/Setloth/ModificationMaster/tags");

      HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");

      // Check if the request was successful
      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
      }

      // Read the response from the API
      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      StringBuilder jsonOutput = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        jsonOutput.append(line);
      }
      conn.disconnect();

      // Parse the JSON array to get the latest tag
      JSONParser parser = new JSONParser();
      Object obj = parser.parse(jsonOutput.toString());
      if (! (obj instanceof JSONArray json)) {
        throw new RuntimeException("Failed: JSON invalid "+jsonOutput);
      }
      if (!json.isEmpty()) {
        JSONObject jsonO = (JSONObject) json.getFirst();
        latestVersion = (String) jsonO.get("name");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return latestVersion;
  }

}
