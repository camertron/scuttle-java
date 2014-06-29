package com.camertron.Scuttle;

import com.camertron.Scuttle.Resolver.*;
import com.sun.deploy.association.Association;

import java.util.ArrayList;
import java.util.List;

public class Tester {
  public static void main(String[] args) {
    parserTest();
  }

  private static void parserTest() {
    AssociationManager manager = new AssociationManager();

    manager.addAssociation("posts", "comments", AssociationType.HAS_MANY);
    manager.addAssociation("posts", "favorites", AssociationType.HAS_MANY);
    manager.addAssociation("comments", "posts", AssociationType.BELONGS_TO);
    manager.addAssociation("comments", "authors", AssociationType.HAS_ONE);
    manager.addAssociation("authors", "comments", AssociationType.BELONGS_TO);
    manager.addAssociation("favorites", "posts", AssociationType.BELONGS_TO);

    JoinColumnPairList joins = new JoinColumnPairList();
    joins.addPair("comments", "comments", "id", "authors", "comment_id");
    joins.addPair("posts", "posts", "id", "comments", "post_id");
    joins.addPair("favorites", "favorites", "post_id", "posts", "id");

    AssociationResolver resolver = new AssociationResolver(manager);
    AssociationChain chain = resolver.getAssociationForJoins(joins);
    System.out.println(chain.toString());

    // SELECT "authors".* FROM "authors"
    // INNER JOIN "comments" ON "comments"."id" = "authors"."comment_id"
    // INNER JOIN "posts" ON "posts"."id" = "comments"."post_id"
    // INNER JOIN "favorites" ON "favorites"."post_id" = "posts"."id"

//    List<JoinColumnPair> joinPairs = resolver.getJoinsForAssociation("authors", "favorites");
//    for (JoinColumnPair j : joinPairs) {
//      System.out.println(j.toString());
//    }

//    System.out.println("");
//    manager.printAssociationJoins();

//    ArrayList<Vertex<String>> path = graph.getShortestPath("Seattle", "New York");
//
//    if (path == null) {
//      System.out.println("No path found");
//    } else {
//      for (int i = 1; i < path.size(); i ++) {
//        Vertex<String> vsFirst = path.get(i - 1);
//        Vertex<String> vsSecond = path.get(i);
//        String sName = (String)graph.getVertices().get(vsFirst.getValue()).getNeighbors().get(vsSecond.getValue()).getMetadata();
//        System.out.println(vsFirst.getValue() + " (" + sName + ") " + vsSecond.getValue());
//      }
//    }

    return;
//    String str = "SELECT COALESCE(1, 'a', (oxen.key + 1)) AS `col`, COUNT(*), STRLEN(phrases.key), created_at FROM phrases";
//    str = "SELECT phrases.key + (phrases.key / 2) AS 'foobar' FROM phrases";
//    str = "SELECT phrases.* FROM phrases";
//    str = "SELECT MAX(phrases.created_at) FROM phrases";
//    str = "SELECT COUNT(id) AS 'count' FROM phrases";
//    str = "SELECT phrase.key = phrase.meta_key FROM phrases";
//    str = "SELECT * FROM phrases WHERE (phrases.key > phrases.meta_key) AND (phrases.key = 1 OR phrases.key = 2)";
//    str = "SELECT * FROM phrases WHERE id = 1";
//    str = "SELECT id AS \"col\" FROM phrases";
//    str = "SELECT * FROM phrases WHERE phrases.key IN(SELECT id FROM phrases)";
//    str = "SELECT * FROM phrases JOIN translations ON translations.id = meta_key";
//    str = "SELECT * FROM phrases ORDER BY STRLEN(key)";
//    str = "SELECT * FROM phrases GROUP BY STRLEN(key)";
//    str = "SELECT * FROM (SELECT COUNT(*) AS count FROM translations) tb WHERE tb.id = 1";
//    str = "SELECT ph.* FROM (SELECT COUNT(phrases.id) FROM phrases) ph";
//    str = "SELECT DISTINCT id FROM phrases";
//    str = "DELETE FROM phrases WHERE phrases.id = 1";
//    str = "INSERT INTO phrases(key) values(STRLEN('foo'))";
//    str = "UPDATE phrases SET foo = 'bar'";
//    str = "INSERT INTO phrases (key) VALUES('bar')";
//    str = "SELECT id FROM foo LIMIT COUNT(id)";
//    str = "SELECT id FROM posts WHERE id BETWEEN posts.id + 1 AND posts.id + 2";
//    str = "SELECT ip, COUNT(signin_attempts.ip) FROM signin_attempts GROUP BY ip ORDER BY COUNT(signin_attempts.ip) DESC LIMIT 30";
//    str = "SELECT id, key FROM phrases";
//    str = "SELECT * FROM phrases WHERE phrases.id IN (1, 2, 3, 4)";
//    str = "SELECT * FROM phrases WHERE id = 1";
//    CharStream in = new ANTLRInputStream(str);
//    SQLLexer lexer = new SQLLexer(in);
//    CommonTokenStream tokens = new CommonTokenStream(lexer);
//    SQLParser parser = new SQLParser(tokens);
//    SQLParser.SqlContext result = parser.sql();
//    SqlStatementVisitor ssVisitor = new SqlStatementVisitor();
//    ssVisitor.visit(result);
//    System.out.println(ssVisitor.toString());
  }
}
