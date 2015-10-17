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

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of exceptions indicating that an unknown escape code was provided.
 */

public final class LexerUnknownEscapeCodeException extends LexerException
{
  private static final long serialVersionUID = 7866161529601875642L;

  /**
   * Construct an exception.
   *
   * @param in_position The position
   * @param in_file     The file, if any
   * @param in_message  The exception message
   */

  public LexerUnknownEscapeCodeException(
    final Position in_position,
    final Optional<Path> in_file,
    final String in_message)
  {
    super(in_position, in_file, in_message);
  }
}
