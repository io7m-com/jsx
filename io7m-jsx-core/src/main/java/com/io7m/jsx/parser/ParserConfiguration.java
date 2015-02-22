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

package com.io7m.jsx.parser;

/**
 * Immutable parser configurations.
 */

public final class ParserConfiguration
{
  public static ParserConfigurationBuilderType newBuilder()
  {
    return new ParserConfigurationBuilderType() {
      private boolean preserve_lex = true;

      @Override public ParserConfiguration build()
      {
        return new ParserConfiguration(this.preserve_lex);
      }

      @Override public void preserveLexicalInformation(
        final boolean e)
      {
        this.preserve_lex = e;
      }
    };
  }

  private final boolean preserve_lex;

  private ParserConfiguration(
    final boolean in_preserve_lex)
  {
    this.preserve_lex = in_preserve_lex;
  }

  public boolean preserveLexicalInformation()
  {
    return this.preserve_lex;
  }
}
