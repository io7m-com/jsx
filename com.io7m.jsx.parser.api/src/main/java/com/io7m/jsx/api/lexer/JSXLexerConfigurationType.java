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

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;
import java.util.EnumSet;
import java.util.Optional;

import static org.immutables.value.Value.Immutable;

/**
 * The type of lexer configurations.
 */

@ImmutablesStyleType
@Immutable
public interface JSXLexerConfigurationType
{
  /**
   * @return {@code true} iff square brackets are allowed to denote lists
   */

  @Value.Parameter(order = 0)
  @Value.Default
  default boolean squareBrackets()
  {
    return false;
  }

  /**
   * @return {@code true} iff newlines are allowed in quoted strings
   */

  @Value.Parameter(order = 1)
  @Value.Default
  default boolean newlinesInQuotedStrings()
  {
    return false;
  }

  /**
   * @return The URI that will be used in lexical information, if any
   */

  @Value.Parameter(order = 2)
  Optional<URI> file();

  /**
   * @return The string(s) used to start line comments
   */

  @Value.Parameter(order = 3)
  @Value.Default
  default EnumSet<JSXLexerComment> comments()
  {
    return EnumSet.noneOf(JSXLexerComment.class);
  }

  /**
   * @return The starting line number (for lexical information)
   */

  @Value.Parameter(order = 4)
  @Value.Default
  default int startAtLine()
  {
    return 0;
  }
}
