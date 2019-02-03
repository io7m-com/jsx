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

package com.io7m.jsx;

/**
 * The type of lists.
 */

public interface SExpressionListType
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
