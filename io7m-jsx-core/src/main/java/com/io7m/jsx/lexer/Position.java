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

import com.io7m.jequality.annotations.EqualityStructural;
import com.io7m.jnull.Nullable;

/**
 * The type of positions in source code. Specifically, a line and column
 * number.
 */

@EqualityStructural public final class Position
{
  /**
   * Location <code>(0, 0)</code>.
   */

  public static final Position ZERO;

  static {
    ZERO = new Position(0, 0);
  }

  private final int            column;
  private final int            line;

  /**
   * Construct a new position.
   *
   * @param in_line
   *          The line
   * @param in_column
   *          The column
   */

  public Position(
    final int in_line,
    final int in_column)
  {
    this.line = in_line;
    this.column = in_column;
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Position other = (Position) obj;
    if (this.column != other.column) {
      return false;
    }
    if (this.line != other.line) {
      return false;
    }
    return true;
  }

  public int getColumn()
  {
    return this.column;
  }

  public int getLine()
  {
    return this.line;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.column;
    result = (prime * result) + this.line;
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.line);
    builder.append(":");
    builder.append(this.column);
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
