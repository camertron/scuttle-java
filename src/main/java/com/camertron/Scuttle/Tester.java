package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLLexer;
import com.camertron.SQLParser.SQLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Tester {
  public static void main(String[] args) {
    CharStream in = new ANTLRInputStream("SELECT * FROM foo");
    SQLLexer lexer = new SQLLexer(in);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    SQLParser parser = new SQLParser(tokens);
    SQLParser.SqlContext result = parser.sql();
    SqlStatement statement = new SqlStatementVisitor().visit(result);
    System.out.println("Done");
  }
}
