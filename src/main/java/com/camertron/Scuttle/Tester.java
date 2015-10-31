package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLLexer;
import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Tester {
  public static void main(String[] args) {
    parserTest();
  }

  private static void parserTest() {
    AssociationManager manager = new AssociationManager();

    manager.addAssociation("badges", "user_badges", AssociationType.HAS_MANY);

//    manager.addAssociation("posts", "comments", AssociationType.HAS_MANY, null, "my_post_id");
//    manager.addAssociation("posts", "favorites", AssociationType.HAS_MANY, "foobar");
//    manager.addAssociation("comments", "posts", AssociationType.BELONGS_TO, null, "my_post_id");
//    manager.addAssociation("comments", "authors", AssociationType.BELONGS_TO);
//    manager.addAssociation("authors", "comments", AssociationType.HAS_ONE);
//    manager.addAssociation("favorites", "posts", AssociationType.BELONGS_TO);
//    manager.addAssociation("collab_posts", "authors", AssociationType.HAS_AND_BELONGS_TO_MANY);
//    manager.addAssociation("authors", "collab_posts", AssociationType.HAS_AND_BELONGS_TO_MANY);

//    JoinColumnPairList joins = new JoinColumnPairList();
//    joins.addPair("comments", "comments", "id", "authors", "comment_id");
//    joins.addPair("posts", "posts", "id", "comments", "post_id");
//    joins.addPair("favorites", "favorites", "post_id", "posts", "id");
//
//    AssociationResolver resolver = manager.createResolver();
//    AssociationChain chain = resolver.getAssociationChainForJoins(joins);
//    System.out.println(chain);

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

    String str = "SELECT COALESCE(1, 'a', (oxen.key + 1)) AS `col`, COUNT(*), STRLEN(phrases.key), created_at FROM phrases";
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
//    str = "SELECT `authors`.* FROM `authors` JOIN `comments` ON `comments`.`author_id` = `authors`.`id` INNER JOIN `posts` ON `posts`.`id` = `comments`.`post_id` INNER JOIN `favorites` ON `favorites`.`post_id` = `posts`.`id`";
//    str = "SELECT `posts`.* from `posts` INNER JOIN `comments` ON `comments`.`post_id` = `posts`.`id`";
//    str = "SELECT `authors`.* FROM `authors` INNER JOIN `authors_collab_posts` ON `authors_collab_posts`.`author_id` = `authors`.`id` INNER JOIN `collab_posts` ON `collab_posts`.`id` = `authors_collab_posts`.`collab_post_id`";
//    str = "SELECT collab_posts.* FROM collab_posts INNER JOIN authors_collab_posts ON authors_collab_posts.collab_post_id = collab_posts.id INNER JOIN authors ON authors.id = authors_collab_posts.author_id";
//    str = "SELECT `authors`.* FROM `authors` INNER JOIN `comments` ON `comments`.`author_id` = `authors`.`id` INNER JOIN `posts` ON `posts`.`id` = `comments`.`my_post_id` INNER JOIN `favorites` ON `favorites`.`post_id` = `posts`.`id`";
//    str = "SELECT * FROM posts INNER JOIN comments ON posts.id = comments.my_post_id";
//    str = "SELECT * FROM comments INNER JOIN posts ON comments.my_post_id = posts.id";
//    str = "SELECT * FROM provider_bill_items WHERE cpt_code IS NULL";
    str = "SELECT game_events.name, sum(CASE WHEN game_events.has_happened THEN 1 ELSE 0 END) total_has_happened FROM game_events GROUP BY game_events.name;";
    CharStream in = new ANTLRInputStream(str);
    SQLLexer lexer = new SQLLexer(in);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    SQLParser parser = new SQLParser(tokens);
    SQLParser.SqlContext result = parser.sql();
    SqlStatementVisitor ssVisitor = new SqlStatementVisitor(manager.createResolver());
    ssVisitor.visit(result);
    System.out.println(ssVisitor.toString());
  }
}
