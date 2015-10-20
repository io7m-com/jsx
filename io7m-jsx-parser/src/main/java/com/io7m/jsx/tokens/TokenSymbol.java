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

package com.io7m.jsx.tokens;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;

import java.nio.file.Path;

/**
 * A symbol.
 */

public final class TokenSymbol implements TokenType
{
  private final ImmutableLexicalPositionType<Path> lex;
  private final String                             text;

  /**
   * @param in_lex  The lexical information
   * @param in_text The token text
   */

  public TokenSymbol(
    final ImmutableLexicalPositionType<Path> in_lex,
    final String in_text)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.text = NullCheck.notNull(in_text);
  }

  @Override public ImmutableLexicalPositionType<Path> getLexicalInformation()
  {
    return this.lex;
  }

  /**
   * @return The text content of the token
   */

  public String getText()
  {
    return this.text;
  }

  @Override public <A, E extends Exception> A matchToken(
    final TokenMatcherType<A, E> m)
    throws E
  {
    return m.symbol(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder(64);
    builder.append("[TokenSymbol ");
    builder.append(this.lex);
    builder.append(": ");
    builder.append(this.text);
    builder.append("]");
    return NullCheck.notNull(builder.toString());
  }
}
