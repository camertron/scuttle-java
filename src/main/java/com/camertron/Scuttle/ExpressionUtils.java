package com.camertron.Scuttle;;

import java.util.regex.Pattern;

public class ExpressionUtils {
  public static String formatOperand(String sOperand, boolean bWrap) {
    return formatOperand(sOperand, bWrap, false);
  }

  public static String formatOperand(String sOperand, boolean bWrap, boolean bGroup) {
    if (sOperand.length() > 8 && sOperand.substring(0, 8).equals("LITERAL<")) {
      Pattern pattern = Pattern.compile("\\ALITERAL\\<|>\\z");
      String sCleanOperand = pattern.matcher(sOperand).replaceAll("");

      if (bWrap) {
        if (bGroup) {
          return "Arel::Nodes::Group.new(" + sCleanOperand + ")";
        } else {
          return "Arel::Nodes::SqlLiteral.new(" + Utils.quote(sCleanOperand) + ")";
        }
      } else {
        return sCleanOperand;
      }
    }

    return sOperand;
  }
}
