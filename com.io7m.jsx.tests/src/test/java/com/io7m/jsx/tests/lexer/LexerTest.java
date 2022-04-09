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

package com.io7m.jsx.tests.lexer;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jsx.api.lexer.JSXLexerBareCarriageReturnException;
import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerInvalidCodePointException;
import com.io7m.jsx.api.lexer.JSXLexerNewLinesInStringsException;
import com.io7m.jsx.api.lexer.JSXLexerNotHexCharException;
import com.io7m.jsx.api.lexer.JSXLexerUnexpectedEOFException;
import com.io7m.jsx.api.lexer.JSXLexerUnknownEscapeCodeException;
import com.io7m.jsx.api.tokens.TokenComment;
import com.io7m.jsx.api.tokens.TokenEOF;
import com.io7m.jsx.api.tokens.TokenLeftParenthesis;
import com.io7m.jsx.api.tokens.TokenLeftSquare;
import com.io7m.jsx.api.tokens.TokenQuotedString;
import com.io7m.jsx.api.tokens.TokenRightParenthesis;
import com.io7m.jsx.api.tokens.TokenRightSquare;
import com.io7m.jsx.api.tokens.TokenSymbol;
import com.io7m.jsx.lexer.JSXLexer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.util.EnumSet;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public final class LexerTest
{
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

  @Test
  public void testLargeData()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        true,
        true,
        Optional.of(URI.create("file.txt")),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var is =
      LexerTest.class.getResourceAsStream("/com/io7m/jsx/tests/main.sdi.gz");
    final var zis =
      new GZIPInputStream(new BufferedInputStream(is));
    final var zis_r =
      new InputStreamReader(zis);
    final var cr =
      UnicodeCharacterReader.newReader(zis_r);

    final var lex = JSXLexer.newLexer(c, cr);

    var count = 0;
    while (true) {
      final var t = lex.token();
      ++count;
      if (t instanceof TokenEOF) {
        break;
      }
    }

    Assertions.assertEquals(40483L, count);
  }

  @Test
  public void testFile0()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.of(URI.create("file.txt")),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var lex = JSXLexer.newLexer(c, stringReader("("));
    final var t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assertions.assertEquals(
      Optional.of(URI.create("file.txt")),
      t.lexical().file());
    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testLeftParen0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(c, stringReader("("));
    final var t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testLeftSquare0()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var lex = JSXLexer.newLexer(
      c,
      stringReader("["));
    final var t = (TokenLeftSquare) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testLeftSquare1()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var lex = JSXLexer.newLexer(c, stringReader("["));
    final var t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testLeftSquare2()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );
    final var lex = JSXLexer.newLexer(c, stringReader("a["));
    final var t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testLeftSquare3()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );
    final var lex = JSXLexer.newLexer(c, stringReader("a["));
    final var t0 = (TokenSymbol) lex.token();
    final var t1 = (TokenLeftSquare) lex.token();
    System.out.println(t0);

    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
  }

  @Test
  public void testNewline0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(c, stringReader(" "));
    final var t = (TokenEOF) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testNewline1()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(c, stringReader("\n"));
    final var t = (TokenEOF) lex.token();
    System.out.println(t);

    Assertions.assertEquals(2L, t.lexical().line());
    Assertions.assertEquals(0L, t.lexical().column());
  }

  @Test
  public void testNewline2()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\r\n"));
    final var t = (TokenEOF) lex.token();
    System.out.println(t);

    Assertions.assertEquals(2L, t.lexical().line());
    Assertions.assertEquals(0L, t.lexical().column());
  }

  @Test
  public void testNewlineBad0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(c, stringReader("\r"));

    Assertions.assertThrows(JSXLexerUnexpectedEOFException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testNewlineBad1()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\r\r"));

    Assertions.assertThrows(JSXLexerBareCarriageReturnException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testQuoted0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"abcd\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "abcd");
  }

  @Test
  public void testQuoted1()
    throws Exception
  {
    final var c = defaultLexerConfig();

    final var sb = new StringBuilder();
    sb.append('"');
    sb.append("ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
    sb.append('"');

    final var s = sb.toString();
    final var lex = JSXLexer.newLexer(c, stringReader(s));
    final var t0 = (TokenQuotedString) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(
      text0,
      "ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
  }

  @Test
  public void testQuoted2()
    throws Exception
  {
    final var c = defaultLexerConfig();

    final var sb = new StringBuilder();
    sb.append('"');
    sb.append('\\');
    sb.append('\\');
    sb.append('"');

    final var s = sb.toString();
    final var lex = JSXLexer.newLexer(c, stringReader(s));
    final var t0 = (TokenQuotedString) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(text0, "\\");
  }

  @Test
  public void testQuotedCarriage0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\r\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\r");
  }

  @Test
  public void testQuotedEscapeBad0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\z\""));

    Assertions.assertThrows(JSXLexerUnknownEscapeCodeException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testQuotedNewline0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\n\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\n");
  }

  @Test
  public void testQuotedNewlineBad0()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var sb = new StringBuilder();
    sb.append('"');
    sb.append('\n');
    sb.append('"');

    final var lex =
      JSXLexer.newLexer(c, stringReader(sb.toString()));

    Assertions.assertThrows(JSXLexerNewLinesInStringsException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testQuotedNewlineBad1()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var sb = new StringBuilder();
    sb.append('"');
    sb.append('\r');
    sb.append('"');

    final var lex =
      JSXLexer.newLexer(c, stringReader(sb.toString()));

    Assertions.assertThrows(JSXLexerNewLinesInStringsException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testQuotedNewlineOK0()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        true,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var sb = new StringBuilder();
    sb.append('"');
    sb.append('\r');
    sb.append('"');

    final var lex =
      JSXLexer.newLexer(c, stringReader(sb.toString()));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\r");
  }

  @Test
  public void testQuotedNewlineOK1()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        true,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var sb = new StringBuilder();
    sb.append('"');
    sb.append('\n');
    sb.append('"');

    final var lex =
      JSXLexer.newLexer(c, stringReader(sb.toString()));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\n");
  }

  @Test
  public void testQuotedQuote0()
    throws Exception
  {
    final var sb = new StringBuilder();
    sb.append('"');
    sb.append('\\');
    sb.append('"');
    sb.append('"');
    final var s = sb.toString();

    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(c, stringReader(s));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "" + '"');
  }

  @Test
  public void testQuotedStringBad0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(c, stringReader("\""));

    Assertions.assertThrows(JSXLexerUnexpectedEOFException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testQuotedTab0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\t\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\t");
  }

  @Test
  public void testQuotedUnicode40()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\u0000\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\0");
  }

  @Test
  public void testQuotedUnicode41()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\uffff\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\uffff");
  }

  @Test
  public void testQuotedUnicode80()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\U00000000\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();
    Assertions.assertEquals(text, "\0");
  }

  @Test
  public void testQuotedUnicode81()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\U0002FFFF\""));
    final var t = (TokenQuotedString) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());

    final var text = t.text();

    final var sb = new StringBuilder();
    sb.appendCodePoint(0x2ffff);
    Assertions.assertEquals(text, sb.toString());
  }

  @Test
  public void testQuotedUnicodeBad40()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\uq\""));

    Assertions.assertThrows(JSXLexerNotHexCharException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testQuotedUnicodeBad80()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("\"\\Uffffffff\""));

    Assertions.assertThrows(JSXLexerInvalidCodePointException.class, () -> {
      lex.token();
    });
  }

  @Test
  public void testRightParen0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(c, stringReader(")"));
    final var t = (TokenRightParenthesis) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testRightSquare0()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var lex = JSXLexer.newLexer(c, stringReader("]"));
    final var t = (TokenRightSquare) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testRightSquare1()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );
    final var lex = JSXLexer.newLexer(c, stringReader("]"));
    final var t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testRightSquare2()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );
    final var lex = JSXLexer.newLexer(c, stringReader("a]"));
    final var t = (TokenSymbol) lex.token();
    System.out.println(t);

    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testRightSquare3()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        true,
        false,
        Optional.empty(),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );
    final var lex = JSXLexer.newLexer(c, stringReader("a]"));
    final var t0 = (TokenSymbol) lex.token();
    final var t1 = (TokenRightSquare) lex.token();
    System.out.println(t0);

    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
  }

  @Test
  public void testSymbol0()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("abcd efgh"));
    final var t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(text0, "abcd");

    final var t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assertions.assertEquals(1L, t1.lexical().line());
    Assertions.assertEquals(6L, t1.lexical().column());
    final var text1 = t1.text();
    Assertions.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol1()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("abcd\nefgh"));
    final var t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(text0, "abcd");

    final var t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assertions.assertEquals(2L, t1.lexical().line());
    Assertions.assertEquals(1L, t1.lexical().column());
    final var text1 = t1.text();
    Assertions.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol2()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("abcd\r\nefgh"));
    final var t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(text0, "abcd");

    final var t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assertions.assertEquals(2L, t1.lexical().line());
    Assertions.assertEquals(1L, t1.lexical().column());
    final var text1 = t1.text();
    Assertions.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol3()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("abcd(efgh"));
    final var t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(text0, "abcd");

    final var tlp0 = (TokenLeftParenthesis) lex.token();
    System.out.println(tlp0);
    Assertions.assertEquals(1L, tlp0.lexical().line());
    Assertions.assertEquals(6L, tlp0.lexical().column());

    final var t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assertions.assertEquals(1L, t1.lexical().line());
    Assertions.assertEquals(7L, t1.lexical().column());
    final var text1 = t1.text();
    Assertions.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol4()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("abcd)efgh"));
    final var t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(text0, "abcd");

    final var tlp0 = (TokenRightParenthesis) lex.token();
    System.out.println(tlp0);
    Assertions.assertEquals(1L, tlp0.lexical().line());
    Assertions.assertEquals(6L, tlp0.lexical().column());

    final var t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assertions.assertEquals(1L, t1.lexical().line());
    Assertions.assertEquals(7L, t1.lexical().column());
    final var text1 = t1.text();
    Assertions.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol5()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex =
      JSXLexer.newLexer(c, stringReader("abcd\"\"efgh"));
    final var t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(text0, "abcd");

    final var tlp0 = (TokenQuotedString) lex.token();
    System.out.println(tlp0);
    Assertions.assertEquals(1L, tlp0.lexical().line());
    Assertions.assertEquals(6L, tlp0.lexical().column());

    final var t1 = (TokenSymbol) lex.token();
    System.out.println(t1);
    Assertions.assertEquals(1L, t1.lexical().line());
    Assertions.assertEquals(8L, t1.lexical().column());
    final var text1 = t1.text();
    Assertions.assertEquals(text1, "efgh");
  }

  @Test
  public void testSymbol6()
    throws Exception
  {
    final var c = defaultLexerConfig();
    final var lex = JSXLexer.newLexer(
      c,
      stringReader("ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ"));
    final var t0 = (TokenSymbol) lex.token();
    System.out.println(t0);
    Assertions.assertEquals(1L, t0.lexical().line());
    Assertions.assertEquals(1L, t0.lexical().column());
    final var text0 = t0.text();
    Assertions.assertEquals(
      text0,
      "ຂອ້ຍກິນແກ້ວໄດ້ໂດຍທີ່ມັນບໍ່ໄດ້ເຮັດໃຫ້ຂອ້ຍເຈັບ");
  }

  @Test
  public void testComments0()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        true,
        true,
        Optional.of(URI.create("file.txt")),
        EnumSet.allOf(JSXLexerComment.class),
        1
      );

    final var is =
      LexerTest.class.getResourceAsStream("/com/io7m/jsx/tests/comments.txt");
    final var is_r =
      new InputStreamReader(is);
    final var cr =
      UnicodeCharacterReader.newReader(is_r);

    final var lex = JSXLexer.newLexer(c, cr);

    final var c0 = (TokenComment) lex.token();
    Assertions.assertEquals(" Hash", c0.text());
    Assertions.assertEquals(JSXLexerComment.COMMENT_HASH, c0.comment());

    final var c1 = (TokenComment) lex.token();
    Assertions.assertEquals(" Percent", c1.text());
    Assertions.assertEquals(JSXLexerComment.COMMENT_PERCENT, c1.comment());

    final var c2 = (TokenComment) lex.token();
    Assertions.assertEquals(" Semicolon", c2.text());
    Assertions.assertEquals(JSXLexerComment.COMMENT_SEMICOLON, c2.comment());

    final var t = (TokenEOF) lex.token();
  }

  @Test
  public void testStartAt0()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.of(URI.create("file.txt")),
        EnumSet.noneOf(JSXLexerComment.class),
        0
      );

    final var lex = JSXLexer.newLexer(c, stringReader("("));
    final var t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assertions.assertEquals(
      Optional.of(URI.create("file.txt")),
      t.lexical().file());
    Assertions.assertEquals(0L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testStartAt1()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.of(URI.create("file.txt")),
        EnumSet.noneOf(JSXLexerComment.class),
        1
      );

    final var lex = JSXLexer.newLexer(c, stringReader("("));
    final var t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assertions.assertEquals(
      Optional.of(URI.create("file.txt")),
      t.lexical().file());
    Assertions.assertEquals(1L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }

  @Test
  public void testStartAt100()
    throws Exception
  {
    final var c =
      new JSXLexerConfiguration(
        false,
        false,
        Optional.of(URI.create("file.txt")),
        EnumSet.noneOf(JSXLexerComment.class),
        100
      );

    final var lex = JSXLexer.newLexer(c, stringReader("("));
    final var t = (TokenLeftParenthesis) lex.token();
    System.out.println(t);

    Assertions.assertEquals(
      Optional.of(URI.create("file.txt")),
      t.lexical().file());
    Assertions.assertEquals(100L, t.lexical().line());
    Assertions.assertEquals(1L, t.lexical().column());
  }
}
