package com.camertron.Scuttle;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Utils {
  public static final String DEFAULT_DELIMITER = ", ";

  public static String camelize(String str) {
    if (str.length() == 0)
      return str;

    String[] saParts = str.split("_");
    String sFinal = "";

    for(String sPart : saParts) {
      sFinal += Character.toString(sPart.charAt(0)).toUpperCase() + sPart.substring(1);
    }

    return sFinal;
  }

  public static String bracketize(String sText) {
    return "[" + sText + "]";
  }

  public static String arrayFormat(ArrayList<String> alItems) {
    return bracketize(join(alItems, DEFAULT_DELIMITER));
  }

  public static String singletonArrayFormat(ArrayList<String> alItems) {
    if (alItems.size() == 1) {
      return alItems.get(0);
    } else {
      return arrayFormat(alItems);
    }
  }

  public static String commaize(ArrayList<String> alItems) {
    return join(alItems, DEFAULT_DELIMITER);
  }

  public static String join(ArrayList<String> alItems, String sDelimiter) {
    String sResult = "";

    for (int i = 0; i < alItems.size(); i ++) {
      if (i != 0)
        sResult += sDelimiter;

      sResult += alItems.get(i);
    }

    return sResult;
  }

  public static String quote(String str) {
    Pattern p = Pattern.compile("\\A['\"]|['\"]\\z", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    String cleanStr = p.matcher(str.trim()).replaceAll("");
    return "'" + cleanStr.replaceAll("'", "\\'") + "'";
  }

  public static String symbolize(String sStr) {
    if (sStr.contains("-") || sStr.contains("'") || sStr.contains("\"")) {
      return ":" + quote(sStr);
    } else {
      return ":" + sStr;
    }
  }
}
