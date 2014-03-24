package com.camertron.Scuttle;;

import java.util.regex.Pattern;

public class ExpressionUtils {
  public static String formatOperand(String sOperand, boolean bWrap) {
    if (sOperand.length() > 8 && sOperand.substring(0, 8).equals("LITERAL<")) {
      Pattern pattern = Pattern.compile("\\ALITERAL\\<|>\\z");
      String sCleanOperand = pattern.matcher(sOperand).replaceAll("");

      if (bWrap) {
        return "Arel::Nodes::SqlLiteral.new(" + Utils.quote(sCleanOperand) + ")";
      } else {
        return sCleanOperand;
      }
    }

    return sOperand;
  }
}
