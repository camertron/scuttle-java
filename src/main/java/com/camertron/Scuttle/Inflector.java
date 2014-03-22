package com.camertron.Scuttle;

public class Inflector {
  // there are way more cases than this...
  public static String dePluralize(String str) {
    if (str.substring(str.length() - 1).equals("s")) {
      return str.substring(0, str.length() - 1);
    } else {
      return str;
    }
  }
}
