package com.io7m.jsx.tests.lexer;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jsx.lexer.Lexer;
import com.io7m.jsx.lexer.LexerBareCarriageReturnException;
import com.io7m.jsx.lexer.LexerConfiguration;
import com.io7m.jsx.lexer.LexerConfigurationBuilderType;
import com.io7m.jsx.lexer.LexerInvalidCodePointException;
import com.io7m.jsx.lexer.LexerNewLinesInStringsException;
import com.io7m.jsx.lexer.LexerNotHexCharException;
import com.io7m.jsx.lexer.LexerType;
import com.io7m.jsx.lexer.LexerUnexpectedEOFException;
import com.io7m.jsx.lexer.LexerUnknownEscapeCodeException;
import com.io7m.jsx.tokens.TokenEOF;
import com.io7m.jsx.tokens.TokenLeftParenthesis;
import com.io7m.jsx.tokens.TokenLeftSquare;
import com.io7m.jsx.tokens.TokenQuotedString;
import com.io7m.jsx.tokens.TokenRightParenthesis;
import com.io7m.jsx.tokens.TokenRightSquare;
import com.io7m.jsx.tokens.TokenSymbol;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

public final class LexerTest
{
  private static UnicodeCharacterReaderPushBackType stringReader(
    final String s)
  {
    return UnicodeCharacterReader.newReader(new StringReader(s));
  }

  private LexerConfiguration defaultLexerConfig()
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    final LexerConfiguration c = cb.build();
    return c;
  }

  @Test public void testLeftParen_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("("));
    final TokenLeftParenthesis t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testLeftSquare_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("["));
    final TokenLeftSquare t = (TokenLeftSquare) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testLeftSquare_1()
    throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setSquareBrackets(false);
    final LexerConfiguration c = cb.build();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("["));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testLeftSquare_2()
    throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setSquareBrackets(false);
    final LexerConfiguration c = cb.build();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("a["));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testLeftSquare_3()
    throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setSquareBrackets(true);
    final LexerConfiguration c = cb.build();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("a["));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    final TokenLeftSquare t1 = (TokenLeftSquare) lex.token();
    System.out.println(t0);

    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
  }

  @Test public void testNewline_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader(" "));
    final TokenEOF t = (TokenEOF) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testNewline_1()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("\n"));
    final TokenEOF t = (TokenEOF) lex.token();
    System.out.println(t);

    Assert.assertEquals(1L, (long) t.getPosition().getLine());
    Assert.assertEquals(0L, (long) t.getPosition().getColumn());
  }

  @Test public void testNewline_2()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("\r\n"));
    final TokenEOF t = (TokenEOF) lex.token();
    System.out.println(t);

    Assert.assertEquals(1L, (long) t.getPosition().getLine());
    Assert.assertEquals(0L, (long) t.getPosition().getColumn());
  }

  @Test(expected = LexerUnexpectedEOFException.class) public
    void
    testNewlineBad_0()
      throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("\r"));
    lex.token();
  }

  @Test(expected = LexerBareCarriageReturnException.class) public
    void
    testNewlineBad_1()
      throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("\r\r"));
    lex.token();
  }

  @Test public void testQuoted_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"abcd\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "abcd");
  }

  @Test public void testQuoted_1()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
    sb.append('"');

    final String s = sb.toString();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader(s));
    final TokenQuotedString t0 = (TokenQuotedString) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert
      .assertEquals(text0, "ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
  }

  @Test public void testQuoted_2()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\\');
    sb.append('\\');
    sb.append('"');

    final String s = sb.toString();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader(s));
    final TokenQuotedString t0 = (TokenQuotedString) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "\\");
  }

  @Test public void testQuotedCarriage_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\r\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\r");
  }

  @Test(expected = LexerUnknownEscapeCodeException.class) public
    void
    testQuotedEscapeBad_0()
      throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\z\""));
    lex.token();
  }

  @Test public void testQuotedNewline_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\n\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\n");
  }

  @Test(expected = LexerNewLinesInStringsException.class) public
    void
    testQuotedNewlineBad_0()
      throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setNewlinesInQuotedStrings(false);
    final LexerConfiguration c = cb.build();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\n');
    sb.append('"');

    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    lex.token();
  }

  @Test(expected = LexerNewLinesInStringsException.class) public
    void
    testQuotedNewlineBad_1()
      throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setNewlinesInQuotedStrings(false);
    final LexerConfiguration c = cb.build();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\r');
    sb.append('"');

    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    lex.token();
  }

  @Test public void testQuotedNewlineOK_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\r');
    sb.append('"');

    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\r");
  }

  @Test public void testQuotedNewlineOK_1()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();

    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\n');
    sb.append('"');

    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader(sb.toString()));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\n");
  }

  @Test public void testQuotedQuote_0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append('\\');
    sb.append('"');
    sb.append('"');
    final String s = sb.toString();

    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader(s));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "" + '"');
  }

  @Test(expected = LexerUnexpectedEOFException.class) public
    void
    testQuotedStringBad_0()
      throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("\""));
    lex.token();
  }

  @Test public void testQuotedTab_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\t\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\t");
  }

  @Test public void testQuotedUnicode4_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\u0000\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\0");
  }

  @Test public void testQuotedUnicode4_1()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\uffff\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\uffff");
  }

  @Test public void testQuotedUnicode8_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\U00000000\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();
    Assert.assertEquals(text, "\0");
  }

  @Test public void testQuotedUnicode8_1()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\U0002FFFF\""));
    final TokenQuotedString t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());

    final String text = t.getText();

    final StringBuilder sb = new StringBuilder();
    sb.appendCodePoint(0x2ffff);
    Assert.assertEquals(text, sb.toString());
  }

  @Test(expected = LexerNotHexCharException.class) public
    void
    testQuotedUnicodeBad4_0()
      throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\uq\""));
    lex.token();
  }

  @Test(expected = LexerInvalidCodePointException.class) public
    void
    testQuotedUnicodeBad8_0()
      throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("\"\\Uffffffff\""));
    lex.token();
  }

  @Test public void testRightParen_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader(")"));
    final TokenRightParenthesis t = (TokenRightParenthesis) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testRightSquare_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("]"));
    final TokenRightSquare t = (TokenRightSquare) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testRightSquare_1()
    throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setSquareBrackets(false);
    final LexerConfiguration c = cb.build();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("]"));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testRightSquare_2()
    throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setSquareBrackets(false);
    final LexerConfiguration c = cb.build();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("a]"));
    final TokenSymbol t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assert.assertEquals(0L, (long) t.getPosition().getLine());
    Assert.assertEquals(1L, (long) t.getPosition().getColumn());
  }

  @Test public void testRightSquare_3()
    throws Exception
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    cb.setSquareBrackets(true);
    final LexerConfiguration c = cb.build();
    final LexerType lex = Lexer.newLexer(c, LexerTest.stringReader("a]"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    final TokenRightSquare t1 = (TokenRightSquare) lex.token();
    System.out.println(t0);

    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
  }

  @Test public void testSymbol_0()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("abcd efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.getPosition().getLine());
    Assert.assertEquals(6L, (long) t1.getPosition().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test public void testSymbol_1()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("abcd\nefgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(1L, (long) t1.getPosition().getLine());
    Assert.assertEquals(1L, (long) t1.getPosition().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test public void testSymbol_2()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("abcd\r\nefgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(1L, (long) t1.getPosition().getLine());
    Assert.assertEquals(1L, (long) t1.getPosition().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test public void testSymbol_3()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("abcd(efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenLeftParenthesis tlp0 = (TokenLeftParenthesis) lex.token();
    System.out.println(tlp0);
    Assert.assertEquals(0L, (long) tlp0.getPosition().getLine());
    Assert.assertEquals(6L, (long) tlp0.getPosition().getColumn());

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.getPosition().getLine());
    Assert.assertEquals(7L, (long) t1.getPosition().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test public void testSymbol_4()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("abcd)efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenRightParenthesis tlp0 = (TokenRightParenthesis) lex.token();
    System.out.println(tlp0);
    Assert.assertEquals(0L, (long) tlp0.getPosition().getLine());
    Assert.assertEquals(6L, (long) tlp0.getPosition().getColumn());

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.getPosition().getLine());
    Assert.assertEquals(7L, (long) t1.getPosition().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test public void testSymbol_5()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest.stringReader("abcd\"\"efgh"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert.assertEquals(text0, "abcd");

    final TokenQuotedString tlp0 = (TokenQuotedString) lex.token();
    System.out.println(tlp0);
    Assert.assertEquals(0L, (long) tlp0.getPosition().getLine());
    Assert.assertEquals(6L, (long) tlp0.getPosition().getColumn());

    final TokenSymbol t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assert.assertEquals(0L, (long) t1.getPosition().getLine());
    Assert.assertEquals(8L, (long) t1.getPosition().getColumn());
    final String text1 = t1.getText();
    Assert.assertEquals(text1, "efgh");
  }

  @Test public void testSymbol_6()
    throws Exception
  {
    final LexerConfiguration c = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(c, LexerTest
        .stringReader("ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ"));
    final TokenSymbol t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assert.assertEquals(0L, (long) t0.getPosition().getLine());
    Assert.assertEquals(1L, (long) t0.getPosition().getColumn());
    final String text0 = t0.getText();
    Assert
      .assertEquals(text0, "ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
  }
}
