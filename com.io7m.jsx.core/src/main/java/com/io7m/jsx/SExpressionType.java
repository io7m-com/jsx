/*
 * Copyright © 2016 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.jsx;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalType;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * The type of S-expressions.
 */

public sealed interface SExpressionType
  extends LexicalType<URI>
{
  /**
   * The type of S-expressions that are atoms.
   */

  sealed interface SAtomType extends SExpressionType
  {
    /**
     * @return The text of the symbol
     */

    String text();
  }

  /**
   * A symbol.
   *
   * @param lexical The lexical position of the symbol
   * @param text    The symbol text
   */

  record SSymbol(
    LexicalPosition<URI> lexical,
    String text
  ) implements SAtomType
  {
    /**
     * A symbol.
     */

    public SSymbol
    {
      Objects.requireNonNull(lexical, "lexical");
      Objects.requireNonNull(text, "text");
    }
  }

  /**
   * A quoted string.
   *
   * @param lexical The lexical position of the symbol
   * @param text    The symbol text
   */

  record SQuotedString(
    LexicalPosition<URI> lexical,
    String text
  ) implements SAtomType
  {
    /**
     * A quoted string.
     */

    public SQuotedString
    {
      Objects.requireNonNull(lexical, "lexical");
      Objects.requireNonNull(text, "text");
    }
  }

  /**
   * The type of lists.
   */

  non-sealed interface SListType
    extends SExpressionType, Iterable<SExpressionType>
  {
    /**
     * @param index The list index in the range {@code 0 <= index < {@link
     *              #size()}}
     *
     * @return The expression at {@code index}
     *
     * @throws IndexOutOfBoundsException Iff {@code index} is out of range
     */

    SExpressionType get(int index);

    /**
     * @return The number of elements in the list
     */

    int size();

    /**
     * @return {@code true} if the original list used square brackets
     */

    boolean isSquare();
  }

  /**
   * A list of expressions.
   *
   * @param lexical     The lexical position of the symbol
   * @param isSquare    {@code true} if the list uses square brackets
   * @param expressions The list of subexpressions
   */

  record SList(
    LexicalPosition<URI> lexical,
    boolean isSquare,
    List<SExpressionType> expressions
  ) implements SListType
  {
    /**
     * A list of expressions.
     */

    public SList
    {
      Objects.requireNonNull(lexical, "lexical");
      Objects.requireNonNull(expressions, "expressions");
    }

    @Override
    public SExpressionType get(
      final int index)
    {
      return this.expressions.get(index);
    }

    @Override
    public int size()
    {
      return this.expressions.size();
    }

    @Override
    public Iterator<SExpressionType> iterator()
    {
      return List.copyOf(this.expressions).iterator();
    }
  }
}
