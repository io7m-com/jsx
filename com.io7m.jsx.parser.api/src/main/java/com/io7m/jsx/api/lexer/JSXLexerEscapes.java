/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.jsx.api.lexer;

import java.util.stream.Collectors;

/**
 * Functions to escape strings.
 */

public final class JSXLexerEscapes
{
  private JSXLexerEscapes()
  {

  }

  /**
   * Create an appropriate escape for the given codepoint.
   *
   * @param codePoint The codepoint
   *
   * @return The escaped string
   */

  public static String escapeCodePoint(
    final int codePoint)
  {
    return switch (codePoint) {
      case '"' -> ("\\" + '"');
      case '\\' -> ("\\" + "\\");
      case '\r' -> ("\\" + "r");
      case '\n' -> ("\\" + "n");
      case '\t' -> ("\\" + "t");
      default -> new StringBuilder(1).appendCodePoint(codePoint).toString();
    };
  }

  /**
   * Escape everything in a given string.
   *
   * @param text The text
   *
   * @return A string containing escapes
   */

  public static String escapeString(
    final String text)
  {
    return text.codePoints()
      .mapToObj(JSXLexerEscapes::escapeCodePoint)
      .collect(Collectors.joining());
  }
}
