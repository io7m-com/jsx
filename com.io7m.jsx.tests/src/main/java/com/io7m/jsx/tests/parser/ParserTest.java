/*
 * Copyright © 2016 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jsx.tests.parser;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jsx.SExpressionType.SListType;
import com.io7m.jsx.SExpressionType.SQuotedString;
import com.io7m.jsx.SExpressionType.SSymbol;
import com.io7m.jsx.api.lexer.JSXLexerBareCarriageReturnException;
import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserGrammarException;
import com.io7m.jsx.api.parser.JSXParserLexicalException;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.parser.JSXParser;
import com.io7m.jsx.serializer.JSXSerializerTrivial;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Optional;

import static com.io7m.jlexing.core.LexicalPositions.zero;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ParserTest
{
  private static final LexicalPosition<URI> DEFAULT_LEX =
    LexicalPosition.of(1, 0, Optional.empty());

  private static UnicodeCharacterReaderPushBackType stringReader(
    final String s)
  {
    return UnicodeCharacterReader.newReader(new StringReader(s));
  }

  private static JSXLexerConfiguration defaultLexerConfig()
  {
    return new JSXLexerConfiguration(
      false,
      false,
      Optional.empty(),
      EnumSet.noneOf(JSXLexerComment.class),
      1
    );
  }

  private static JSXParserConfiguration defaultParserConfig()
  {
    return new JSXParserConfiguration(
      true
    );
  }

  @Test
  public void testEOF0()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(lc, stringReader(""));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserGrammarException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testEOF1()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(
      lc, stringReader("(a b "));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserGrammarException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testEOF2()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader("(a b c)"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);
    final var r0 = p.parseExpressionOrEOF();
    Assertions.assertTrue(r0.isPresent());
    final var r0l = (SListType) r0.get();
    final var r1 = p.parseExpressionOrEOF();
    Assertions.assertFalse(r1.isPresent());
  }

  @Test
  public void testEOF3()
    throws Exception
  {
    final var lc =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var lex = JSXLexer.newLexer(
      lc, stringReader("[a b "));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserGrammarException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testLexError0()
    throws Exception
  {
    final var lex = (JSXLexerType) () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserLexicalException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testLexError1()
    throws Exception
  {
    final var lex = (JSXLexerType) () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserLexicalException.class, () -> {
      p.parseExpressionOrEOF();
    });
  }

  @Test
  public void testLexError2()
    throws Exception
  {
    final var lex = (JSXLexerType) () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserLexicalException.class, () -> {
      p.parseExpressions();
    });
  }

  @Test
  public void testParseList0()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader("(a b c)"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    final var s = (SListType) p.parseExpression();
    final var sl0 = s.lexical();
    assertEquals(1L, sl0.line());
    assertEquals(1L, sl0.column());
    assertEquals(3L, s.size());
    assertEquals(Optional.empty(), sl0.file());

    {
      final var ss = (SSymbol) s.get(0);
      assertEquals("a", ss.text());
      final var sl = ss.lexical();
      assertEquals(Optional.empty(), sl.file());
    }

    {
      final var ss = (SSymbol) s.get(1);
      assertEquals("b", ss.text());
      final var sl = ss.lexical();
      assertEquals(Optional.empty(), sl.file());
    }

    {
      final var ss = (SSymbol) s.get(2);
      assertEquals("c", ss.text());
      final var sl = ss.lexical();
      assertEquals(Optional.empty(), sl.file());
    }
  }

  @Test
  public void testParseListNoLex0()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader("(a b c)"));

    final var pc = new JSXParserConfiguration(false);
    final var p = JSXParser.newParser(pc, lex);

    final var s = (SListType) p.parseExpression();
    assertEquals(DEFAULT_LEX, s.lexical());
    assertEquals(3L, s.size());

    {
      final var ss = (SSymbol) s.get(0);
      assertEquals("a", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final var ss = (SSymbol) s.get(1);
      assertEquals("b", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final var ss = (SSymbol) s.get(2);
      assertEquals("c", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }
  }

  @Test
  public void testParseListSquareNoLex1()
    throws Exception
  {
    final var lc = new JSXLexerConfiguration(
      true,
      false,
      Optional.empty(),
      EnumSet.noneOf(JSXLexerComment.class),
      1
    );

    final var lex =
      JSXLexer.newLexer(lc, stringReader("[a b c]"));

    final var pc = new JSXParserConfiguration(false);
    final var p = JSXParser.newParser(pc, lex);

    final var s = (SListType) p.parseExpression();
    assertEquals(DEFAULT_LEX, s.lexical());
    assertEquals(3L, s.size());

    {
      final var ss = (SSymbol) s.get(0);
      assertEquals("a", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final var ss = (SSymbol) s.get(1);
      assertEquals("b", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final var ss = (SSymbol) s.get(2);
      assertEquals("c", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }
  }

  @Test
  public void testParseSquareList0()
    throws Exception
  {
    final var lc =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var lex =
      JSXLexer.newLexer(lc, stringReader("[a b c]"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    final var s = (SListType) p.parseExpression();
    final var sl0 = s.lexical();
    assertEquals(1L, sl0.line());
    assertEquals(1L, sl0.column());
    assertEquals(3L, s.size());
    assertEquals(Optional.empty(), sl0.file());

    {
      final var ss = (SSymbol) s.get(0);
      assertEquals("a", ss.text());
      final var sl = ss.lexical();
      assertEquals(Optional.empty(), sl.file());
    }

    {
      final var ss = (SSymbol) s.get(1);
      assertEquals("b", ss.text());
      final var sl = ss.lexical();
      assertEquals(Optional.empty(), sl.file());
    }

    {
      final var ss = (SSymbol) s.get(2);
      assertEquals("c", ss.text());
      final var sl = ss.lexical();
      assertEquals(Optional.empty(), sl.file());
    }
  }

  @Test
  public void testParseSymbol0()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader("a"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    final var s = (SSymbol) p.parseExpression();
    final var sl = s.lexical();
    assertEquals(1L, sl.line());
    assertEquals(1L, sl.column());
    assertEquals("a", s.text());
    assertEquals(Optional.empty(), sl.file());
  }

  @Test
  public void testParseSymbolNoLex0()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader("a"));
    final var pc =
      new JSXParserConfiguration(false);
    final var p =
      JSXParser.newParser(pc, lex);

    final var s = (SSymbol) p.parseExpression();
    assertEquals(DEFAULT_LEX, s.lexical());
    assertEquals("a", s.text());
  }

  @Test
  public void testQuotedString0()
    throws Exception
  {
    final var sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader(sb.toString()));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    final var s = (SQuotedString) p.parseExpression();
    final var sl = s.lexical();
    assertEquals(1L, sl.line());
    assertEquals(1L, sl.column());
    assertEquals("a", s.text());
    assertEquals(Optional.empty(), sl.file());
  }

  @Test
  public void testQuotedStringNoLex0()
    throws Exception
  {
    final var sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader(sb.toString()));

    final var pc =
      new JSXParserConfiguration(false);
    final var p =
      JSXParser.newParser(pc, lex);

    final var s =
      (SQuotedString) p.parseExpression();
    assertEquals(DEFAULT_LEX, s.lexical());
    assertEquals("a", s.text());
  }

  @Test
  public void testUnbalancedRoundSquare0()
    throws Exception
  {
    final var lc = new JSXLexerConfiguration(
      true,
      false,
      Optional.empty(),
      EnumSet.noneOf(JSXLexerComment.class),
      1);

    final var lex =
      JSXLexer.newLexer(lc, stringReader("(]"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserGrammarException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testUnbalancedRoundSquare1()
    throws Exception
  {
    final var lc =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1);

    final var lex =
      JSXLexer.newLexer(lc, stringReader("[)"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserGrammarException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testUnexpectedRight0()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader(")"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserGrammarException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testUnexpectedRightSquare0()
    throws Exception
  {
    final var lc =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1);

    final var lex =
      JSXLexer.newLexer(lc, stringReader("]"));
    final var pc = defaultParserConfig();
    final var p = JSXParser.newParser(pc, lex);

    assertThrows(JSXParserGrammarException.class, () -> {
      p.parseExpression();
    });
  }

  @Test
  public void testParseExpressions()
    throws Exception
  {
    final var lc = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(lc, stringReader("a b c"));
    final var pc =
      new JSXParserConfiguration(false);
    final var p =
      JSXParser.newParser(pc, lex);

    final var s = p.parseExpressions();
    assertEquals(3L, s.size());

    {
      final var ss = (SSymbol) s.get(0);
      assertEquals("a", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final var ss = (SSymbol) s.get(1);
      assertEquals("b", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final var ss = (SSymbol) s.get(2);
      assertEquals("c", ss.text());
      assertEquals(DEFAULT_LEX, ss.lexical());
    }
  }

  @Test
  public void testParseCommented()
    throws Exception
  {
    final var lc =
      new JSXLexerConfiguration(
        true,
        true,
        Optional.empty(),
        EnumSet.allOf(JSXLexerComment.class),
        1
      );

    try (Reader reader =
           new InputStreamReader(
             ParserTest.class.getResourceAsStream(
               "/com/io7m/jsx/tests/commented0.txt"),
             StandardCharsets.UTF_8)) {

      final var lex =
        JSXLexer.newLexer(lc, UnicodeCharacterReader.newReader(reader));

      final var pc =
        new JSXParserConfiguration(false);
      final var p = JSXParser.newParser(pc, lex);

      final var s = p.parseExpressions();
      assertEquals(23L, s.size());
    }
  }

  @Test
  public void testParseCommentedOrEOF()
    throws Exception
  {
    final var lc =
      new JSXLexerConfiguration(
        true,
        true,
        Optional.empty(),
        EnumSet.allOf(JSXLexerComment.class),
        1
      );

    try (Reader reader =
           new InputStreamReader(
             ParserTest.class.getResourceAsStream(
               "/com/io7m/jsx/tests/commented0.txt"),
             StandardCharsets.UTF_8)) {

      final var lex =
        JSXLexer.newLexer(lc, UnicodeCharacterReader.newReader(reader));

      final var pc =
        new JSXParserConfiguration(false);
      final var p =
        JSXParser.newParser(pc, lex);

      var count = 0L;
      while (true) {
        final var s = p.parseExpressionOrEOF();
        if (s.isPresent()) {
          ++count;
        } else {
          break;
        }
      }

      assertEquals(23L, count);
    }
  }

  @Test
  public void testBug12()
    throws Exception
  {
    final var lc =
      new JSXLexerConfiguration(
        true,
        true,
        Optional.empty(),
        EnumSet.allOf(JSXLexerComment.class),
        1
      );

    try (Reader reader =
           new InputStreamReader(
             ParserTest.class.getResourceAsStream(
               "/com/io7m/jsx/tests/bug12.cbs"),
             StandardCharsets.UTF_8)) {

      final var lex =
        JSXLexer.newLexer(lc, UnicodeCharacterReader.newReader(reader));

      final var pc =
        new JSXParserConfiguration(true);
      final var p =
        JSXParser.newParser(pc, lex);
      final var ex =
        assertThrows(JSXParserException.class, p::parseExpressions);

      assertEquals(94, ex.lexical().line());
    }
  }

  @Test
  public void testEmptyQuoted()
    throws Exception
  {
    final var bao =
      new ByteArrayOutputStream();

    final var expr =
      new SQuotedString(LexicalPosition.of(1, 0, Optional.empty()), "");
    JSXSerializerTrivial.newSerializer()
      .serialize(expr, bao);

    final var lc =
      new JSXLexerConfiguration(
        true,
        true,
        Optional.empty(),
        EnumSet.allOf(JSXLexerComment.class),
        0
      );
    final var lex =
      JSXLexer.newLexer(lc, UnicodeCharacterReader.newReader(
        new StringReader(bao.toString(StandardCharsets.UTF_8))))
      ;
    final var pc =
      new JSXParserConfiguration(false);
    final var p =
      JSXParser.newParser(pc, lex);

    assertEquals(expr, p.parseExpression());
  }
}
