/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
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
import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.lexer.JSXLexerBareCarriageReturnException;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerConfigurationType;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserConfigurationType;
import com.io7m.jsx.api.parser.JSXParserGrammarException;
import com.io7m.jsx.api.parser.JSXParserLexicalException;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.parser.JSXParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ParserTest
{
  private static UnicodeCharacterReaderPushBackType stringReader(
    final String s)
  {
    return UnicodeCharacterReader.newReader(new StringReader(s));
  }

  private static JSXLexerConfigurationType defaultLexerConfig()
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    final JSXLexerConfiguration c = cb.build();
    return c;
  }

  private static JSXParserConfigurationType defaultParserConfig()
  {
    final JSXParserConfiguration.Builder cb =
      JSXParserConfiguration.builder();
    return cb.build();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testEOF0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(lc, ParserTest.stringReader(""));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testEOF1()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(
      lc, ParserTest.stringReader("(a b "));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test
  public void testEOF2()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    final Optional<SExpressionType> r0 = p.parseExpressionOrEOF();
    Assert.assertTrue(r0.isPresent());
    final SExpressionListType r0l = (SExpressionListType) r0.get();
    final Optional<SExpressionType> r1 = p.parseExpressionOrEOF();
    Assert.assertFalse(r1.isPresent());
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testEOF3()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex = JSXLexer.newLexer(
      lc, ParserTest.stringReader("[a b "));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError0()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError1()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpressionOrEOF();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError2()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpressions();
  }

  @Test
  public void testParseList0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    final LexicalPositionType<Path> sl0 = s.lexical().get();
    Assert.assertEquals(0L, (long) sl0.line());
    Assert.assertEquals(1L, (long) sl0.column());
    Assert.assertEquals(3L, (long) s.size());
    Assert.assertEquals(Optional.empty(), sl0.file());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      final LexicalPositionType<Path> sl = ss.lexical().get();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      final LexicalPositionType<Path> sl = ss.lexical().get();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      final LexicalPositionType<Path> sl = ss.lexical().get();
      Assert.assertEquals(Optional.empty(), sl.file());
    }
  }

  @Test
  public void testParseListNoLex0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    Assert.assertFalse(s.lexical().isPresent());
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }
  }

  @Test
  public void testParseListSquareNoLex1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("[a b c]"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    Assert.assertFalse(s.lexical().isPresent());
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }
  }

  @Test
  public void testParseSquareList0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("[a b c]"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    final LexicalPositionType<Path> sl0 = s.lexical().get();
    Assert.assertEquals(0L, (long) sl0.line());
    Assert.assertEquals(1L, (long) sl0.column());
    Assert.assertEquals(3L, (long) s.size());
    Assert.assertEquals(Optional.empty(), sl0.file());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      final LexicalPositionType<Path> sl = ss.lexical().get();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      final LexicalPositionType<Path> sl = ss.lexical().get();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      final LexicalPositionType<Path> sl = ss.lexical().get();
      Assert.assertEquals(Optional.empty(), sl.file());
    }
  }

  @Test
  public void testParseSymbol0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("a"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionSymbolType s = (SExpressionSymbolType) p.parseExpression();
    final LexicalPositionType<Path> sl = s.lexical().get();
    Assert.assertEquals(0L, (long) sl.line());
    Assert.assertEquals(1L, (long) sl.column());
    Assert.assertEquals("a", s.text());
    Assert.assertEquals(Optional.empty(), sl.file());
  }

  @Test
  public void testParseSymbolNoLex0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("a"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionSymbolType s = (SExpressionSymbolType) p.parseExpression();
    final Optional<LexicalPositionType<Path>> lex_opt =
      s.lexical();
    Assert.assertFalse(lex_opt.isPresent());
    Assert.assertEquals("a", s.text());
  }

  @Test
  public void testQuotedString0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader(sb.toString()));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionQuotedStringType s =
      (SExpressionQuotedStringType) p.parseExpression();
    final LexicalPositionType<Path> sl = s.lexical().get();
    Assert.assertEquals(0L, (long) sl.line());
    Assert.assertEquals(1L, (long) sl.column());
    Assert.assertEquals("a", s.text());
    Assert.assertEquals(Optional.empty(), sl.file());
  }

  @Test
  public void testQuotedStringNoLex0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader(sb.toString()));

    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionQuotedStringType s =
      (SExpressionQuotedStringType) p.parseExpression();
    final Optional<LexicalPositionType<Path>> lex_opt =
      s.lexical();
    Assert.assertFalse(lex_opt.isPresent());
    Assert.assertEquals("a", s.text());
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnbalancedRoundSquare0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(]"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnbalancedRoundSquare1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("[)"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnexpectedRight0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader(")"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnexpectedRightSquare0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("]"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test
  public void testParseExpressions()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("a b c"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final List<SExpressionType> s = p.parseExpressions();
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      Assert.assertFalse(ss.lexical().isPresent());
    }
  }
}
