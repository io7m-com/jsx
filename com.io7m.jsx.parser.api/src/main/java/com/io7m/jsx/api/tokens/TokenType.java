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

import com.io7m.jlexing.core.LexicalPosition;

import java.net.URI;

/**
 * The type of tokens.
 */

public sealed interface TokenType permits
  TokenComment,
  TokenEOF,
  TokenLeftParenthesis,
  TokenLeftSquare,
  TokenQuotedString,
  TokenRightParenthesis,
  TokenRightSquare,
  TokenSymbol
{
  /**
   * @return The lexical information for the token
   */

  LexicalPosition<URI> lexical();

  /**
   * Match a token.
   *
   * @param m   The matcher
   * @param <A> The type of values returned by the matcher
   * @param <E> The type of exceptions raised by the matcher
   *
   * @return The value returned by the matcher
   *
   * @throws E If the matcher raises {@code E}
   */

  <A, E extends Exception> A matchToken(
    TokenMatcherType<A, E> m)
    throws E;
}
