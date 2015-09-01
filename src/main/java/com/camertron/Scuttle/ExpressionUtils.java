package com.camertron.Scuttle;;

import java.util.regex.Pattern;

public class ExpressionUtils {
  public static String formatOperand(String sOperand, boolean bWrap, ScuttleOptions sptOptions) {
    return formatOperand(sOperand, bWrap, false, sptOptions);
  }

  public static String formatOperand(String sOperand, boolean bWrap, boolean bGroup, ScuttleOptions sptOptions) {
    if (sOperand.length() > 8 && sOperand.substring(0, 8).equals("LITERAL<")) {
      Pattern pattern = Pattern.compile("\\ALITERAL\\<|>\\z");
      String sCleanOperand = pattern.matcher(sOperand).replaceAll("");
      String sResult;

      if (bWrap) {
        if (bGroup) {
          sResult = "Group.new(" + sCleanOperand + ")";
        } else {
          sResult = "SqlLiteral.new(" + Utils.quote(sCleanOperand) + ")";
        }
      } else {
        return sCleanOperand;
      }

      return sptOptions.namespaceArelNodeClass(sResult);
    }

    return sOperand;
  }
}
