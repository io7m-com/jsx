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

package com.io7m.jsx.prettyprint;

import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SQuotedString;
import com.io7m.jsx.SExpressionType.SSymbol;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.WriterBackend;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * A pretty printer tailored to printing expressions that represent tree-like
 * documents.
 */

public final class JSXPrettyPrinterMarkupStyle implements JSXPrettyPrinterType
{
  private final WriterBackend backend;
  private final PrinterMatcher matcher;

  private JSXPrettyPrinterMarkupStyle(
    final Writer in_out,
    final int width,
    final int indent)
  {
    final Writer out = Objects.requireNonNull(in_out, "Writer");
    this.backend = new WriterBackend(out, width);
    final Layouter<IOException> layout = new Layouter<>(this.backend, indent);
    this.matcher = new PrinterMatcher(layout);
  }

  /**
   * Create a new pretty printer.
   *
   * @param in_out    The output writer
   * @param in_width  The maximum output width (note that unbreakable lines may
   *                  exceed this width)
   * @param in_indent The indent for nested expressions
   *
   * @return A new printer
   */

  public static JSXPrettyPrinterType newPrinterWithWidthIndent(
    final Writer in_out,
    final int in_width,
    final int in_indent)
  {
    return new JSXPrettyPrinterMarkupStyle(in_out, in_width, in_indent);
  }

  @Override
  public void print(final SExpressionType e)
    throws IOException
  {
    this.matcher.match(e);
  }

  @Override
  public void close()
    throws IOException
  {
    this.backend.flush();
  }

  private static final class PrinterMatcher
  {
    private final Layouter<IOException> layout;

    PrinterMatcher(
      final Layouter<IOException> in_layout)
    {
      this.layout = Objects.requireNonNull(in_layout, "Layout");
    }

    public Void match(
      final SExpressionType e)
      throws IOException
    {
      if (e instanceof SQuotedString quotedString) {
        return this.quotedString(quotedString);
      } else if (e instanceof SSymbol symbol) {
        return this.symbol(symbol);
      } else if (e instanceof SExpressionType.SListType list) {
        return this.list(list);
      } else {
        throw new IllegalStateException();
      }
    }

    public Void list(final SExpressionType.SListType e)
      throws IOException
    {
      final Layouter<IOException> x = this.layout;

      x.begin(
        Layouter.BreakConsistency.INCONSISTENT,
        Layouter.IndentationBase.FROM_POS,
        0);

      if (e.isSquare()) {
        x.print("[");
      } else {
        x.print("(");
      }

      final int size = e.size();
      if (size > 0) {
        x.begin(
          Layouter.BreakConsistency.INCONSISTENT,
          Layouter.IndentationBase.FROM_POS,
          0);

        for (int index = 0; index < size; ++index) {
          final SExpressionType current = e.get(index);
          this.match(current);
          if (index + 1 < size) {
            x.brk();
          }
        }

        x.end();
      }

      if (e.isSquare()) {
        x.print("]");
      } else {
        x.print(")");
      }

      x.end();
      return null;
    }

    public Void quotedString(
      final SQuotedString e)
      throws IOException
    {
      this.layout.print(
        String.format("\"%s\"", StringEscapeUtils.escapeJava(e.text()))
      );
      return null;
    }

    public Void symbol(final SSymbol e)
      throws IOException
    {
      this.layout.print(e.text());
      return null;
    }
  }
}
