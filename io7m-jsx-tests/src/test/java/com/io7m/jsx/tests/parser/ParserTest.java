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
import com.io7m.jlexing.core.ImmutableLexicalPosition;
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
  public void testEOF_0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(lc, ParserTest.stringReader(""));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testEOF_1()
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
  public void testEOF_2()
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
  public void testEOF_3()
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
  public void testLexError_0()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        ImmutableLexicalPosition.newPosition(0, 0), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError_1()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        ImmutableLexicalPosition.newPosition(0, 0), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpressionOrEOF();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError_2()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        ImmutableLexicalPosition.newPosition(0, 0), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpressions();
  }

  @Test
  public void testParseList_0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    final LexicalPositionType<Path> sl0 = s.getLexicalInformation().get();
    Assert.assertEquals(0L, (long) sl0.getLine());
    Assert.assertEquals(1L, (long) sl0.getColumn());
    Assert.assertEquals(3L, (long) s.size());
    Assert.assertEquals(Optional.empty(), sl0.getFile());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      final LexicalPositionType<Path> sl = ss.getLexicalInformation().get();
      Assert.assertEquals(Optional.empty(), sl.getFile());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      final LexicalPositionType<Path> sl = ss.getLexicalInformation().get();
      Assert.assertEquals(Optional.empty(), sl.getFile());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      final LexicalPositionType<Path> sl = ss.getLexicalInformation().get();
      Assert.assertEquals(Optional.empty(), sl.getFile());
    }
  }

  @Test
  public void testParseListNoLex_0()
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
    Assert.assertFalse(s.getLexicalInformation().isPresent());
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }
  }

  @Test
  public void testParseListSquareNoLex_1()
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
    Assert.assertFalse(s.getLexicalInformation().isPresent());
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }
  }

  @Test
  public void testParseSquareList_0()
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
    final LexicalPositionType<Path> sl0 = s.getLexicalInformation().get();
    Assert.assertEquals(0L, (long) sl0.getLine());
    Assert.assertEquals(1L, (long) sl0.getColumn());
    Assert.assertEquals(3L, (long) s.size());
    Assert.assertEquals(Optional.empty(), sl0.getFile());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      final LexicalPositionType<Path> sl = ss.getLexicalInformation().get();
      Assert.assertEquals(Optional.empty(), sl.getFile());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      final LexicalPositionType<Path> sl = ss.getLexicalInformation().get();
      Assert.assertEquals(Optional.empty(), sl.getFile());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      final LexicalPositionType<Path> sl = ss.getLexicalInformation().get();
      Assert.assertEquals(Optional.empty(), sl.getFile());
    }
  }

  @Test
  public void testParseSymbol_0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("a"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionSymbolType s = (SExpressionSymbolType) p.parseExpression();
    final LexicalPositionType<Path> sl = s.getLexicalInformation().get();
    Assert.assertEquals(0L, (long) sl.getLine());
    Assert.assertEquals(1L, (long) sl.getColumn());
    Assert.assertEquals("a", s.getText());
    Assert.assertEquals(Optional.empty(), sl.getFile());
  }

  @Test
  public void testParseSymbolNoLex_0()
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
      s.getLexicalInformation();
    Assert.assertFalse(lex_opt.isPresent());
    Assert.assertEquals("a", s.getText());
  }

  @Test
  public void testQuotedString_0()
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
    final LexicalPositionType<Path> sl = s.getLexicalInformation().get();
    Assert.assertEquals(0L, (long) sl.getLine());
    Assert.assertEquals(1L, (long) sl.getColumn());
    Assert.assertEquals("a", s.getText());
    Assert.assertEquals(Optional.empty(), sl.getFile());
  }

  @Test
  public void testQuotedStringNoLex_0()
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
      s.getLexicalInformation();
    Assert.assertFalse(lex_opt.isPresent());
    Assert.assertEquals("a", s.getText());
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnbalancedRoundSquare_0()
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
  public void testUnbalancedRoundSquare_1()
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
  public void testUnexpectedRight_0()
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
  public void testUnexpectedRightSquare_0()
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
      Assert.assertEquals("a", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      Assert.assertFalse(ss.getLexicalInformation().isPresent());
    }
  }
}
