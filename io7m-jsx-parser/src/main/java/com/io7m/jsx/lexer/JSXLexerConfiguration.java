/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.jsx.lexer;

import com.io7m.jnull.NullCheck;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Immutable lexer configurations.
 */

public final class JSXLexerConfiguration
{
  private final boolean        square_brackets;
  private final boolean        string_newlines;
  private final Optional<Path> file;

  private JSXLexerConfiguration(
    final boolean in_string_newlines,
    final boolean in_square_brackets,
    final Optional<Path> in_file)
  {
    this.string_newlines = in_string_newlines;
    this.square_brackets = in_square_brackets;
    this.file = NullCheck.notNull(in_file);
  }

  /**
   * @return A new lexer configuration builder
   */

  public static JSXLexerConfigurationBuilderType newBuilder()
  {
    return new JSXLexerConfigurationBuilderType()
    {
      private Optional<Path> file = Optional.empty();
      private boolean square_brackets = true;
      private boolean string_newlines = true;

      @Override public JSXLexerConfiguration build()
      {
        return new JSXLexerConfiguration(
          this.string_newlines, this.square_brackets, this.file);
      }

      @Override public void setNewlinesInQuotedStrings(
        final boolean e)
      {
        this.string_newlines = e;
      }

      @Override public void setSquareBrackets(
        final boolean e)
      {
        this.square_brackets = e;
      }

      @Override public void setFile(final Optional<Path> in_file)
      {
        this.file = NullCheck.notNull(in_file);
      }
    };
  }

  /**
   * @return The file name that will be used in lexical information, if any
   */

  public Optional<Path> getFile()
  {
    return this.file;
  }

  /**
   * @return {@code true} iff newlines are allowed in quoted strings
   */

  public boolean allowNewlinesInQuotedStrings()
  {
    return this.string_newlines;
  }

  /**
   * @return {@code true} iff square brackets are allowed to denote lists
   */

  public boolean allowSquareBrackets()
  {
    return this.square_brackets;
  }
}
