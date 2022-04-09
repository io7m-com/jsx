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

package com.io7m.jsx.lexer;

import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerException;
import com.io7m.jsx.api.lexer.JSXLexerSupplierType;
import com.io7m.jsx.api.tokens.TokenEOF;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Simple lexer demo that prints all tokens.
 */

public final class JSXLexerDemoMain
{
  private JSXLexerDemoMain()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Main program.
   *
   * @param args Command line arguments
   *
   * @throws IOException On I/O errors
   */

  public static void main(
    final String[] args)
    throws IOException
  {
    try {
      final var lexer_config =
        new JSXLexerConfiguration(
          true,
          false,
          Optional.empty(),
          EnumSet.of(JSXLexerComment.COMMENT_HASH),
          1
        );

      final JSXLexerSupplierType lexer_supplier =
        new JSXLexerSupplier();
      final var lexer =
        lexer_supplier.createFromStreamUTF8(lexer_config, System.in);

      while (true) {
        final var t = lexer.token();
        System.out.println(t);
        if (t instanceof TokenEOF) {
          break;
        }
      }
    } catch (final JSXLexerException e) {
      System.err.println(
        "error: lexical error: "
          + e.lexical()
          + ": "
          + e.getMessage());
    }
  }
}
