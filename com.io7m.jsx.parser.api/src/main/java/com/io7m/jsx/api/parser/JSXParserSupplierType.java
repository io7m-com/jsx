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

package com.io7m.jsx.api.parser;

import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jsx.api.lexer.JSXLexerConfigurationType;
import com.io7m.jsx.api.lexer.JSXLexerSupplierType;
import com.io7m.jsx.api.lexer.JSXLexerType;
import org.osgi.annotation.versioning.ProviderType;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * The type of parser suppliers.
 */

@ProviderType
@FunctionalInterface
public interface JSXParserSupplierType
{
  /**
   * Create a new parser from the given configuration and lexer.
   *
   * @param configuration The configuration
   * @param lexer         The lexer
   *
   * @return A new parser
   */

  JSXParserType create(
    JSXParserConfigurationType configuration,
    JSXLexerType lexer);

  /**
   * Create a new parser from the given parser configuration and lexer.
   *
   * @param parser_configuration The parser configuration
   * @param lexer_supplier       A lexer supplier
   * @param lexer_configuration  A lexer configuration
   * @param reader               The reader
   *
   * @return A new parser
   */

  default JSXParserType createFromReader(
    final JSXParserConfigurationType parser_configuration,
    final JSXLexerConfigurationType lexer_configuration,
    final JSXLexerSupplierType lexer_supplier,
    final UnicodeCharacterReaderPushBackType reader)
  {
    Objects.requireNonNull(parser_configuration, "Parser configuration");
    Objects.requireNonNull(lexer_configuration, "Lexer configuration");
    Objects.requireNonNull(lexer_supplier, "Lexer supplier");
    Objects.requireNonNull(reader, "Reader");

    return this.create(
      parser_configuration, lexer_supplier.create(lexer_configuration, reader));
  }

  /**
   * Create a new parser from the given parser configuration and lexer.
   *
   * @param parser_configuration The parser configuration
   * @param lexer_supplier       A lexer supplier
   * @param lexer_configuration  A lexer configuration
   * @param charset              The character set of the stream data
   * @param stream               The stream
   *
   * @return A new parser
   */

  default JSXParserType createFromStream(
    final JSXParserConfigurationType parser_configuration,
    final JSXLexerConfigurationType lexer_configuration,
    final JSXLexerSupplierType lexer_supplier,
    final Charset charset,
    final InputStream stream)
  {
    Objects.requireNonNull(parser_configuration, "Parser configuration");
    Objects.requireNonNull(lexer_configuration, "Lexer configuration");
    Objects.requireNonNull(lexer_supplier, "Lexer supplier");
    Objects.requireNonNull(charset, "Charset");
    Objects.requireNonNull(stream, "Stream");

    return this.create(
      parser_configuration,
      lexer_supplier.createFromStream(lexer_configuration, charset, stream));
  }

  /**
   * Create a new parser from the given parser configuration and lexer. The
   * stream is parsed as UTF-8 data.
   *
   * @param parser_configuration The parser configuration
   * @param lexer_supplier       A lexer supplier
   * @param lexer_configuration  A lexer configuration
   * @param stream               The stream
   *
   * @return A new parser
   */

  default JSXParserType createFromStreamUTF8(
    final JSXParserConfigurationType parser_configuration,
    final JSXLexerConfigurationType lexer_configuration,
    final JSXLexerSupplierType lexer_supplier,
    final InputStream stream)
  {
    Objects.requireNonNull(parser_configuration, "Parser configuration");
    Objects.requireNonNull(lexer_configuration, "Lexer configuration");
    Objects.requireNonNull(lexer_supplier, "Lexer supplier");
    Objects.requireNonNull(stream, "Stream");

    return this.create(
      parser_configuration,
      lexer_supplier.createFromStreamUTF8(lexer_configuration, stream));
  }
}
