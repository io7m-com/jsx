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

package com.io7m.jsx.tokens;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;

import java.nio.file.Path;

/**
 * End of file.
 */

public final class TokenEOF implements TokenType
{
  private final ImmutableLexicalPositionType<Path> lex;

  /**
   * @param in_lex The lexical information
   */

  public TokenEOF(
    final ImmutableLexicalPositionType<Path> in_lex)
  {
    this.lex = in_lex;
  }

  @Override public ImmutableLexicalPositionType<Path> getLexicalInformation()
  {
    return this.lex;
  }

  @Override public <A, E extends Exception> A matchToken(
    final TokenMatcherType<A, E> m)
    throws E
  {
    return m.eof(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder(64);
    builder.append("[TokenEOF ");
    builder.append(this.lex);
    builder.append("]");
    return NullCheck.notNull(builder.toString());
  }
}
