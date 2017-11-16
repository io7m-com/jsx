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

package com.io7m.jsx.api.lexer;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import java.util.Objects;
import org.osgi.annotation.versioning.ProviderType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * The type of lexer suppliers.
 */

@ProviderType
@FunctionalInterface
public interface JSXLexerSupplierType
{
  /**
   * Create a new lexer from the given configuration and reader.
   *
   * @param configuration The configuration
   * @param reader        The reader
   *
   * @return A new lexer
   */

  JSXLexerType create(
    JSXLexerConfigurationType configuration,
    UnicodeCharacterReaderPushBackType reader);

  /**
   * Create a new lexer from the given configuration and reader.
   *
   * @param configuration The configuration
   * @param reader        The reader
   *
   * @return A new lexer
   */

  default JSXLexerType createFromReader(
    final JSXLexerConfigurationType configuration,
    final Reader reader)
  {
    Objects.requireNonNull(configuration, "Configuration");
    Objects.requireNonNull(reader, "Reader");
    return this.create(configuration, new UnicodeCharacterReader(reader));
  }

  /**
   * Create a new lexer from the given configuration and stream.
   *
   * @param configuration The configuration
   * @param charset       The character set of the stream data
   * @param stream        The stream
   *
   * @return A new lexer
   */

  default JSXLexerType createFromStream(
    final JSXLexerConfigurationType configuration,
    final Charset charset,
    final InputStream stream)
  {
    Objects.requireNonNull(configuration, "Configuration");
    Objects.requireNonNull(charset, "Charset");
    Objects.requireNonNull(stream, "Stream");

    return this.createFromReader(
      configuration, new InputStreamReader(stream, charset));
  }

  /**
   * Create a new lexer from the given configuration and stream. The stream is
   * parsed as UTF-8 data.
   *
   * @param configuration The configuration
   * @param stream        The stream
   *
   * @return A new lexer
   */

  default JSXLexerType createFromStreamUTF8(
    final JSXLexerConfigurationType configuration,
    final InputStream stream)
  {
    Objects.requireNonNull(configuration, "Configuration");
    Objects.requireNonNull(stream, "Stream");

    return this.createFromStream(configuration, StandardCharsets.UTF_8, stream);
  }
}
