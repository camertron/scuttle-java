package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLLexer;
import com.camertron.SQLParser.SQLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Stack;

public class Tester {
  public static void main(String[] args) {
    parserTest();
  }

  private static void parserTest() {
    String str = "SELECT COALESCE(1, 'a', (phrases.key + 1)) AS `col`, COUNT(*), STRLEN(phrases.key), phrases.created_at FROM phrases";
//    str = "SELECT phrases.key + (phrases.key / 2) AS 'foobar' FROM phrases";
//    str = "SELECT phrases.* FROM phrases";
//    str = "SELECT MAX(phrases.created_at) FROM phrases";
//    str = "SELECT COUNT(*) AS 'count' FROM phrases";
//    str = "SELECT phrase.key = phrase.meta_key FROM phrases";
    CharStream in = new ANTLRInputStream(str);
    SQLLexer lexer = new SQLLexer(in);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    SQLParser parser = new SQLParser(tokens);
    SQLParser.SqlContext result = parser.sql();
    SqlStatementVisitor ssVisitor = new SqlStatementVisitor();
    ssVisitor.visit(result);
    System.out.println(ssVisitor.toString());
  }
}
