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

package com.io7m.jsx.serializer;

import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionSymbolType;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * A trivial serializer with no features.
 */

public final class JSXSerializerTrivial implements JSXSerializerType
{
  private JSXSerializerTrivial()
  {

  }

  /**
   * @return A new serializer
   */

  public static JSXSerializerType newSerializer()
  {
    return new JSXSerializerTrivial();
  }

  private static void serializeWithWriter(
    final SExpressionType e,
    final PrintWriter w)
    throws IOException
  {
    e.matchExpression(
      new SExpressionMatcherType<Integer, IOException>()
      {
        @Override public Integer list(
          final SExpressionListType xs)
          throws IOException
        {
          if (xs.isSquare()) {
            w.print("[");
          } else {
            w.print("(");
          }

          final int max = xs.size();
          for (int index = 0; index < max; ++index) {
            final SExpressionType es = xs.get(index);
            JSXSerializerTrivial.serializeWithWriter(es, w);
            if ((index + 1) < max) {
              w.print(" ");
            }
          }

          if (xs.isSquare()) {
            w.print("]");
          } else {
            w.print(")");
          }

          return Integer.valueOf(0);
        }

        @Override public Integer quotedString(
          final SExpressionQuotedStringType qs)
          throws IOException
        {
          w.print('"');
          w.print(qs.getText());
          w.print('"');
          return Integer.valueOf(0);
        }

        @Override public Integer symbol(
          final SExpressionSymbolType ss)
          throws IOException
        {
          w.print(ss.getText());
          return Integer.valueOf(0);
        }
      });
  }

  @Override public void serialize(
    final SExpressionType e,
    final OutputStream s)
    throws IOException
  {
    final BufferedOutputStream bs =
      new BufferedOutputStream(s);
    final OutputStreamWriter os =
      new OutputStreamWriter(bs, StandardCharsets.UTF_8);
    final PrintWriter w = new PrintWriter(os);
    JSXSerializerTrivial.serializeWithWriter(e, w);
    w.flush();
  }
}
