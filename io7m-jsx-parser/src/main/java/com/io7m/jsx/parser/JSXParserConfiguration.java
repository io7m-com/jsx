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

package com.io7m.jsx.parser;

/**
 * Immutable parser configurations.
 */

public final class JSXParserConfiguration
{
  private final boolean preserve_lex;

  private JSXParserConfiguration(
    final boolean in_preserve_lex)
  {
    this.preserve_lex = in_preserve_lex;
  }

  /**
   * @return A new parser configuration builder
   */

  public static JSXParserConfigurationBuilderType newBuilder()
  {
    return new JSXParserConfigurationBuilderType()
    {
      private boolean preserve_lex = true;

      @Override public JSXParserConfiguration build()
      {
        return new JSXParserConfiguration(this.preserve_lex);
      }

      @Override public void preserveLexicalInformation(
        final boolean e)
      {
        this.preserve_lex = e;
      }
    };
  }

  /**
   * @return {@code true} iff lexical information is being preserved
   */

  public boolean preserveLexicalInformation()
  {
    return this.preserve_lex;
  }
}
