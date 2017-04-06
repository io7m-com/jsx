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

import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionSymbolType;

import java.nio.file.Path;
import java.util.Optional;

final class PSymbol implements SExpressionSymbolType
{
  private final Optional<LexicalPositionType<Path>> lex;
  private final String text;

  PSymbol(
    final String t,
    final Optional<LexicalPositionType<Path>> in_lex)
  {
    this.text = NullCheck.notNull(t, "Text");
    this.lex = NullCheck.notNull(in_lex, "Lexical information");
  }

  @Override
  public Optional<LexicalPositionType<Path>> lexical()
  {
    return this.lex;
  }

  @Override
  public String text()
  {
    return this.text;
  }

  @Override
  public <A, E extends Exception> A matchExpression(
    final SExpressionMatcherType<A, E> m)
    throws E
  {
    return m.symbol(this);
  }
}
