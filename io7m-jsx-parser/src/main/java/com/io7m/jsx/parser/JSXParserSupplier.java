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

import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfigurationType;
import com.io7m.jsx.api.parser.JSXParserSupplierType;
import com.io7m.jsx.api.parser.JSXParserType;
import org.osgi.service.component.annotations.Component;

/**
 * The default implementation of the {@link JSXParserSupplierType} interface.
 */

@Component
public final class JSXParserSupplier implements JSXParserSupplierType
{
  private JSXParserSupplier()
  {

  }

  /**
   * @return A new parser supplier
   */

  public static JSXParserSupplierType createSupplier()
  {
    return new JSXParserSupplier();
  }

  @Override
  public JSXParserType create(
    final JSXParserConfigurationType configuration,
    final JSXLexerType lexer)
  {
    return JSXParser.newParser(configuration, lexer);
  }
}
