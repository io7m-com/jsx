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

package com.io7m.jsx.tests.prettyprint;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.parser.JSXParser;
import com.io7m.jsx.prettyprint.JSXPrettyPrinterType;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.EnumSet;
import java.util.Optional;

public abstract class JSXPrettyPrinterContract
{
  private final int[] SIZES = {10, 20, 40, 80, 120, 9999};

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
    return new JSXParserConfiguration(true);
  }

  private static JSXParserType parserForFile(
    final String s)
  {
    final var lc =
      defaultLexerConfig();
    final var is =
      JSXPrettyPrinterContract.class.getResourceAsStream(s);
    final var isr =
      new InputStreamReader(is);
    final var usr =
      UnicodeCharacterReader.newReader(isr);
    final var lex =
      JSXLexer.newLexer(lc, usr);
    final var pc =
      defaultParserConfig();
    return JSXParser.newParser(pc, lex);
  }

  private static void parseString(
    final String text)
    throws Exception
  {
    final var lc =
      defaultLexerConfig();
    final var reader =
      new StringReader(text);
    final var usr =
      UnicodeCharacterReader.newReader(reader);
    final var lex =
      JSXLexer.newLexer(lc, usr);
    final var pc =
      defaultParserConfig();
    JSXParser.newParser(pc, lex)
      .parseExpressions();
  }

  protected abstract JSXPrettyPrinterType newPrettyPrinter(
    Writer out,
    int width,
    int indent);

  @Test
  public final void testPrettyPrintBinomial()
    throws Exception
  {
    this.showFile("binomial.s");
  }

  @Test
  public final void testPrettyPrintLorem()
    throws Exception
  {
    this.showFile("lorem.s");
  }

  @Test
  public final void testPrettyPrintLoremShorter()
    throws Exception
  {
    this.showFile("lorem_shorter.s");
  }

  @Test
  public final void testEscapeQuotesCorrect()
    throws Exception
  {
    this.showFile("quotes.s");
  }

  @Test
  public final void testEscapeSlashesCorrect()
    throws Exception
  {
    this.showFile("slashes.s");
  }

  private void showFile(final String file)
    throws Exception
  {
    final var p =
      parserForFile(file);
    final var e =
      p.parseExpression();

    for (final var width : this.SIZES) {
      final var out = new StringWriter();

      System.out.println();
      System.out.println("--------");
      System.out.println("Width " + width);
      System.out.println("--------");

      final var pp =
        this.newPrettyPrinter(out, width, 2);

      pp.print(e);
      pp.close();

      System.out.println(out);
      parseString(out.toString());
    }
  }
}
