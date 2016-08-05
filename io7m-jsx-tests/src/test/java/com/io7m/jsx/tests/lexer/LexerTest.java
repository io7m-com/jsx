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

package com.io7m.jsx.tests.lexer;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jsx.api.lexer.JSXLexerBareCarriageReturnException;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerConfigurationType;
import com.io7m.jsx.api.lexer.JSXLexerInvalidCodePointException;
import com.io7m.jsx.api.lexer.JSXLexerNewLinesInStringsException;
import com.io7m.jsx.api.lexer.JSXLexerNotHexCharException;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.lexer.JSXLexerUnexpectedEOFException;
import com.io7m.jsx.api.lexer.JSXLexerUnknownEscapeCodeException;
import com.io7m.jsx.api.tokens.TokenEOF;
import com.io7m.jsx.api.tokens.TokenLeftParenthesis;
import com.io7m.jsx.api.tokens.TokenLeftSquare;
import com.io7m.jsx.api.tokens.TokenQuotedString;
import com.io7m.jsx.api.tokens.TokenRightParenthesis;
import com.io7m.jsx.api.tokens.TokenRightSquare;
import com.io7m.jsx.api.tokens.TokenSymbol;
import com.io7m.jsx.api.tokens.TokenType;
import com.io7m.jsx.lexer.JSXLexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public final class LexerTest
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

  @Test
  public void testLargeData()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setFile(Optional.of(Paths.get("file.txt")));
    cb.setNewlinesInQuotedStrings(true);
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration c = cb.build();

    final InputStream is =
      LexerTest.class.getResourceAsStream("/com/io7m/jsx/tests/main.sdi.gz");
    final GZIPInputStream zis =
      new GZIPInputStream(new BufferedInputStream(is));
    final InputStreamReader zis_r =
      new InputStreamReader(zis);
    final UnicodeCharacterReaderPushBackType cr =
      UnicodeCharacterReader.newReader(zis_r);

    final JSXLexerType lex = JSXLexer.newLexer(c, cr);

    int count = 0;
    while (true) {
      final TokenType t = lex.token();
      ++count;
      if (t instanceof TokenEOF) {
        break;
      }
    }

    Assert.assertEquals(40483L, (long) count);
  }

  @Test
  public void testFile_0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setFile(Optional.of(Paths.get("file.txt")));
    final JSXLexerConfiguration c = cb.build();

    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("("));
    final TokenLeftParenthesis t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assert.assertEquals(
      Optional.of(Paths.get("file.txt")), t.lexicalInformation().getFile());
    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testLeftParen_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("("));
    final TokenLeftParenthesis t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testLeftSquare_0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);

    final JSXLexerType lex = JSXLexer.newLexer(
      cb.build(),
      LexerTest.stringReader("["));
    final TokenLeftSquare t = (TokenLeftSquare) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testLeftSquare_1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(false);
    final JSXLexerConfigurationType c = cb.build();

    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("["));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testLeftSquare_2()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setSquareBrackets(false);
    final JSXLexerConfigurationType c = cb.build();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("a["));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testLeftSquare_3()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfigurationType c = cb.build();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("a["));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    final TokenLeftSquare t1 = (TokenLeftSquare) lex.token();
    System.out.println(t0);

    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
  }

  @Test
  public void testNewline_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader(" "));
    final TokenEOF t = (TokenEOF) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testNewline_1()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("\n"));
    final TokenEOF t = (TokenEOF) lex.token();
    System.out.println(t);

    Assert.assertEquals(1L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(0L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testNewline_2()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\r\n"));
    final TokenEOF t = (TokenEOF) lex.token();
    System.out.println(t);

    Assert.assertEquals(1L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(0L, (long) t.lexicalInformation().getColumn());
  }

  @Test(expected = JSXLexerUnexpectedEOFException.class)
  public void testNewlineBad_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("\r"));
    lex.token();
  }

  @Test(expected = JSXLexerBareCarriageReturnException.class)
  public void testNewlineBad_1()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\r\r"));
    lex.token();
  }

  @Test
  public void testQuoted_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"abcd\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "abcd");
  }

  @Test
  public void testQuoted_1()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
    sb.append('"');

    final String s = sb.toString();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader(s));
    final TokenQuotedString t0 = (TokenQuotedString) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
  }

  @Test
  public void testQuoted_2()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\\');
    sb.append('\\');
    sb.append('"');

    final String s = sb.toString();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader(s));
    final TokenQuotedString t0 = (TokenQuotedString) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "\\");
  }

  @Test
  public void testQuotedCarriage_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\r\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\r");
  }

  @Test(expected = JSXLexerUnknownEscapeCodeException.class)
  public void testQuotedEscapeBad_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\z\""));
    lex.token();
  }

  @Test
  public void testQuotedNewline_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\n\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\n");
  }

  @Test(expected = JSXLexerNewLinesInStringsException.class)
  public void testQuotedNewlineBad_0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setNewlinesInQuotedStrings(false);
    final JSXLexerConfigurationType c = cb.build();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\n');
    sb.append('"');

    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    lex.token();
  }

  @Test(expected = JSXLexerNewLinesInStringsException.class)
  public void testQuotedNewlineBad_1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setNewlinesInQuotedStrings(false);
    final JSXLexerConfigurationType c = cb.build();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\r');
    sb.append('"');

    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    lex.token();
  }

  @Test
  public void testQuotedNewlineOK_0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setNewlinesInQuotedStrings(true);
    final JSXLexerConfiguration c = cb.build();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\r');
    sb.append('"');

    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\r");
  }

  @Test
  public void testQuotedNewlineOK_1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setNewlinesInQuotedStrings(true);
    final JSXLexerConfiguration c = cb.build();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\n');
    sb.append('"');

    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\n");
  }

  @Test
  public void testQuotedQuote_0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\\');
    sb.append('"');
    sb.append('"');
    final String s = sb.toString();

    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader(s));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "" + '"');
  }

  @Test(expected = JSXLexerUnexpectedEOFException.class)
  public void testQuotedStringBad_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("\""));
    lex.token();
  }

  @Test
  public void testQuotedTab_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\t\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\t");
  }

  @Test
  public void testQuotedUnicode4_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\u0000\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\0");
  }

  @Test
  public void testQuotedUnicode4_1()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\uffff\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\uffff");
  }

  @Test
  public void testQuotedUnicode8_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\U00000000\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\0");
  }

  @Test
  public void testQuotedUnicode8_1()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\U0002FFFF\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());

    final String text = t.getText();

    final StringBuilder sb = new StringBuilder();
    sb.appendCodePoint(0x2ffff);
    Assert.assertEquals(text, sb.toString());
  }

  @Test(expected = JSXLexerNotHexCharException.class)
  public void testQuotedUnicodeBad4_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\uq\""));
    lex.token();
  }

  @Test(expected = JSXLexerInvalidCodePointException.class)
  public void testQuotedUnicodeBad8_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("\"\\Uffffffff\""));
    lex.token();
  }

  @Test
  public void testRightParen_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader(")"));
    final TokenRightParenthesis t = (TokenRightParenthesis) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testRightSquare_0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration c = cb.build();

    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("]"));
    final TokenRightSquare t = (TokenRightSquare) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testRightSquare_1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setSquareBrackets(false);
    final JSXLexerConfigurationType c = cb.build();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("]"));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testRightSquare_2()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setSquareBrackets(false);
    final JSXLexerConfigurationType c = cb.build();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("a]"));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t.lexicalInformation().getColumn());
  }

  @Test
  public void testRightSquare_3()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfigurationType c = cb.build();
    final JSXLexerType lex = JSXLexer.newLexer(c, LexerTest.stringReader("a]"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    final TokenRightSquare t1 = (TokenRightSquare) lex.token();
    System.out.println(t0);

    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
  }

  @Test
  public void testSymbol_0()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("abcd efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.lexicalInformation().getLine());
    Assert.assertEquals(6L, (long) t1.lexicalInformation().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol_1()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("abcd\nefgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(1L, (long) t1.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t1.lexicalInformation().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol_2()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("abcd\r\nefgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(1L, (long) t1.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t1.lexicalInformation().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol_3()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("abcd(efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenLeftParenthesis tlp0 = (TokenLeftParenthesis) lex.token();
    System.out.println(tlp0);
    Assert.assertEquals(0L, (long) tlp0.lexicalInformation().getLine());
    Assert.assertEquals(6L, (long) tlp0.lexicalInformation().getColumn());

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.lexicalInformation().getLine());
    Assert.assertEquals(7L, (long) t1.lexicalInformation().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol_4()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("abcd)efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenRightParenthesis tlp0 = (TokenRightParenthesis) lex.token();
    System.out.println(tlp0);
    Assert.assertEquals(0L, (long) tlp0.lexicalInformation().getLine());
    Assert.assertEquals(6L, (long) tlp0.lexicalInformation().getColumn());

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.lexicalInformation().getLine());
    Assert.assertEquals(7L, (long) t1.lexicalInformation().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol_5()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(c, LexerTest.stringReader("abcd\"\"efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenQuotedString tlp0 = (TokenQuotedString) lex.token();
    System.out.println(tlp0);
    Assert.assertEquals(0L, (long) tlp0.lexicalInformation().getLine());
    Assert.assertEquals(6L, (long) tlp0.lexicalInformation().getColumn());

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.lexicalInformation().getLine());
    Assert.assertEquals(8L, (long) t1.lexicalInformation().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol_6()
    throws Exception
  {
    final JSXLexerConfigurationType c = LexerTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(
      c,
      LexerTest.stringReader("ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.lexicalInformation().getLine());
    Assert.assertEquals(1L, (long) t0.lexicalInformation().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
  }
}
