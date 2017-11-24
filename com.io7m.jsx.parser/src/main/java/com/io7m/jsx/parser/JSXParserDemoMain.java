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

package com.io7m.jsx.parser;

import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerSupplierType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserSupplierType;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.api.serializer.JSXSerializerType;
import com.io7m.jsx.lexer.JSXLexerSupplier;
import com.io7m.jsx.serializer.JSXSerializerTrivial;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Simple parser demo that parses and then serializes.
 */

public final class JSXParserDemoMain
{
  private JSXParserDemoMain()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Main program.
   *
   * @param args Command line arguments
   *
   * @throws IOException On I/O errors
   */

  public static void main(
    final String[] args)
    throws IOException
  {
    final JSXLexerConfiguration.Builder lexer_config_builder =
      JSXLexerConfiguration.builder();
    lexer_config_builder.setNewlinesInQuotedStrings(true);
    lexer_config_builder.setComments(EnumSet.allOf(JSXLexerComment.class));
    lexer_config_builder.setSquareBrackets(true);

    final JSXLexerConfiguration lexer_config =
      lexer_config_builder.build();

    final JSXParserConfiguration.Builder pcb = JSXParserConfiguration.builder();
    pcb.setPreserveLexical(true);
    final JSXParserConfiguration parser_config = pcb.build();

    final JSXLexerSupplierType lexer_supplier =
      new JSXLexerSupplier();
    final JSXParserSupplierType parser_supplier =
      new JSXParserSupplier();

    final JSXParserType parser =
      parser_supplier.createFromStreamUTF8(
        parser_config, lexer_config, lexer_supplier, System.in);

    final JSXSerializerType serializer = JSXSerializerTrivial.newSerializer();

    while (true) {
      try {
        final Optional<SExpressionType> e_opt = parser.parseExpressionOrEOF();
        if (e_opt.isPresent()) {
          final SExpressionType e = e_opt.get();
          serializer.serialize(e, System.out);
          System.out.println();
        } else {
          break;
        }
      } catch (final JSXParserException x) {
        System.err.println(
          "error: parse error: "
            + x.getLexicalInformation()
            + ": "
            + x.getMessage());
      }
    }
  }
}
