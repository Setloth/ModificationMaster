package me.setloth.modificationMaster.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class VersionChecker {

  public static String latestVersion() {
    String latestVersion = null;
    try {
      URI uri = new URI("https://api.github.com/repos/Setloth/ModificationMaster/tags");

      HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");

      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed To Check Version: HTTP error code: " + conn.getResponseCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      StringBuilder jsonOutput = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        jsonOutput.append(line);
      }
      conn.disconnect();

      JSONParser parser = new JSONParser();
      Object obj = parser.parse(jsonOutput.toString());
      if (!(obj instanceof JSONArray json)) {
        throw new RuntimeException("Failed To Parse JSON: invalid " + jsonOutput);
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
