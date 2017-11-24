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

import com.io7m.jlexing.core.LexicalPositionType;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of S-expressions.
 */

public interface SExpressionType
{
  /**
   * @return The lexical information for the expression, if any
   */

  Optional<LexicalPositionType<Path>> lexical();

  /**
   * Match an expression.
   *
   * @param m   The matcher
   * @param <A> The type of values returned by the matcher
   * @param <E> The type of exceptions raised by the matcher
   *
   * @return The value returned by the matcher
   *
   * @throws E If the matcher raises {@code E}
   */

  <A, E extends Exception> A matchExpression(
    SExpressionMatcherType<A, E> m)
    throws E;
}
