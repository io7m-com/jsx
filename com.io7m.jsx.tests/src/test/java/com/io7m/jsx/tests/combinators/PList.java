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

package com.io7m.jsx.tests.combinators;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionType;

import java.net.URI;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

final class PList extends AbstractList<SExpressionType>
  implements SExpressionListType
{
  private final List<SExpressionType> data;
  private final boolean square;
  private final LexicalPosition<URI> lex;

  PList(
    final List<SExpressionType> d,
    final LexicalPosition<URI> in_lex,
    final boolean in_square)
  {
    this.data = Objects.requireNonNull(d);
    this.lex = Objects.requireNonNull(in_lex);
    this.square = in_square;
  }

  @Override
  public void add(
    final int index,
    final SExpressionType element)
  {
    this.data.add(index, Objects.requireNonNull(element));
  }

  @Override
  public SExpressionType get(
    final int index)
  {
    return Objects.requireNonNull(this.data.get(index));
  }

  @Override
  public boolean isSquare()
  {
    return this.square;
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.lex;
  }

  @Override
  public <A, E extends Exception> A matchExpression(
    final SExpressionMatcherType<A, E> m)
    throws E
  {
    return m.list(this);
  }

  @Override
  public SExpressionType remove(
    final int index)
  {
    return Objects.requireNonNull(this.data.remove(index));
  }

  @Override
  public SExpressionType set(
    final int index,
    final SExpressionType element)
  {
    return Objects.requireNonNull(this.data.set(
      index,
      Objects.requireNonNull(element)));
  }

  @Override
  public int size()
  {
    return this.data.size();
  }
}
