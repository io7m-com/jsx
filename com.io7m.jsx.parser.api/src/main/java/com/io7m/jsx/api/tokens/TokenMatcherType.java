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

package com.io7m.jsx.api.tokens;

/**
 * The type of token matchers.
 *
 * @param <A> The type of returned values
 * @param <E> The type of raised exceptions
 */

public interface TokenMatcherType<A, E extends Exception>
{
  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A eof(
    TokenEOF t)
    throws E;

  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A leftParen(
    TokenLeftParenthesis t)
    throws E;

  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A leftSquare(
    TokenLeftSquare t)
    throws E;

  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A quotedString(
    TokenQuotedString t)
    throws E;

  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A rightParen(
    TokenRightParenthesis t)
    throws E;

  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A rightSquare(
    TokenRightSquare t)
    throws E;

  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A symbol(
    TokenSymbol t)
    throws E;

  /**
   * Match a token.
   *
   * @param t The token
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A comment(
    TokenComment t)
    throws E;
}
