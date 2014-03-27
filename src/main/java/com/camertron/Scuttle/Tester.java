package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLLexer;
import com.camertron.SQLParser.SQLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

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
//    str = "SELECT * FROM phrases WHERE (phrases.key > phrases.meta_key) AND (phrases.key = 1 OR phrases.key = 2)";
//    str = "SELECT * FROM phrases WHERE 1 = 1";
//    str = "SELECT id FROM phrases";
//    str = "SELECT * FROM phrases WHERE phrases.key IN(SELECT id FROM phrases)";
    str = "SELECT * FROM phrases JOIN translations ON translations.id = meta_key";
//    str = "SELECT * FROM phrases ORDER BY id";
//    str = "SELECT * FROM phrases GROUP BY phrases.id";
//    str = "SELECT * FROM (SELECT COUNT(*) AS count FROM translations) tb WHERE tb.id = 1";
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
