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

package com.io7m.jsx.parser;

import com.io7m.jnull.NullCheck;
import com.io7m.jsx.lexer.LexerException;

/**
 * The type of parser errors caused by {@link LexerException} throws.
 */

public final class ParserLexicalException extends ParserException
{
  private static final long serialVersionUID = 7904039867120509695L;

  /**
   * Construct an exception.
   *
   * @param in_cause The cause
   */

  public ParserLexicalException(
    final LexerException in_cause)
  {
    super(
      in_cause.getPosition(),
      in_cause.getFile(),
      NullCheck.notNull(in_cause.getMessage()),
      in_cause);
  }
}
