package ch.usi.si.seart.src2abs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class AbstractorTest {

    @Test
    void classAbstractionTest() {
        String original =
                "package org.example;\n" +
                "\n" +
                "/**\n" +
                " * This is a JavaDoc comment\n" +
                " */\n" +
                "public class App {\n" +
                "\n" +
                "    /**\n" +
                "     * This is another JavaDoc comment\n" +
                "     */\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World!\");\n" +
                "    }\n" +
                "}\n";
        String expected = "package VAR_1 . VAR_2 ; public class VAR_3 { public static void METHOD_1 ( TYPE_1 [ ] VAR_4 ) { VAR_5 . VAR_6 . METHOD_2 ( STRING_1 ) ; } }";
        Abstractor.Result result = Abstractor.abstractCode(Parser.Granularity.CLASS, original, Set.of());
        Assertions.assertEquals(expected, result.getAbstracted());
        Assertions.assertEquals(10, result.getMapping().size());
    }

    @Test
    void methodAbstractionTest() {
        String original =
                "/**\n" +
                " * This is a JavaDoc comment\n" +
                " */\n" +
                "public static void main(String[] args) {\n" +
                "    System.out.println(\"Hello World!\");\n" +
                "}\n";
        String expected = "public static void METHOD_1 ( TYPE_1 [ ] VAR_1 ) { VAR_2 . VAR_3 . METHOD_2 ( STRING_1 ) ; }";
        Abstractor.Result result = Abstractor.abstractCode(Parser.Granularity.METHOD, original, Set.of());
        Assertions.assertEquals(expected, result.getAbstracted());
        Assertions.assertEquals(7, result.getMapping().size());
    }

    @Test
    void idiomsTest() {
        String original =
                "package org.example;\n" +
                "\n" +
                "/**\n" +
                " * This is a JavaDoc comment\n" +
                " */\n" +
                "public class App {\n" +
                "\n" +
                "    /**\n" +
                "     * This is another JavaDoc comment\n" +
                "     */\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World!\");\n" +
                "    }\n" +
                "}\n";
        String expected = "package VAR_1 . VAR_2 ; public class App { public static void main ( String [ ] VAR_3 ) { VAR_4 . VAR_5 . METHOD_1 ( STRING_1 ) ; } }";
        Abstractor.Result result = Abstractor.abstractCode(Parser.Granularity.CLASS, original, Set.of("App", "String", "main"));
        Assertions.assertEquals(expected, result.getAbstracted());
        Assertions.assertEquals(7, result.getMapping().size());
    }
}