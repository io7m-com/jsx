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

package com.io7m.jsx.tests.prettyprint;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerConfigurationType;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserConfigurationType;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.parser.JSXParser;
import com.io7m.jsx.prettyprint.JSXPrettyPrinterType;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public abstract class JSXPrettyPrinterContract
{
  private final int SIZES[] = {10, 20, 40, 80, 120, 9999};

  protected abstract JSXPrettyPrinterType newPrettyPrinter(
    Writer out,
    int width,
    int indent);

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

  private static JSXParserType parserForFile(final String s)
  {
    final JSXLexerConfigurationType lc =
      JSXPrettyPrinterContract.defaultLexerConfig();
    final InputStream is =
      JSXPrettyPrinterContract.class.getResourceAsStream(s);
    final InputStreamReader isr =
      new InputStreamReader(is);
    final UnicodeCharacterReaderPushBackType usr =
      UnicodeCharacterReader.newReader(isr);
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, usr);
    final JSXParserConfigurationType pc =
      JSXPrettyPrinterContract.defaultParserConfig();
    return JSXParser.newParser(pc, lex);
  }

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

  private void showFile(final String file)
    throws JSXParserException, IOException
  {
    final JSXParserType p =
      JSXPrettyPrinterContract.parserForFile(file);
    final SExpressionType e =
      p.parseExpression();

    for (final int width : this.SIZES) {
      System.out.println();
      System.out.println("--------");
      System.out.println("Width " + width);
      System.out.println("--------");

      final JSXPrettyPrinterType pp =
        this.newPrettyPrinter(new OutputStreamWriter(System.out), width, 2);

      pp.print(e);
      pp.close();
    }
  }
}
