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

import com.io7m.jsx.api.serializer.JSXSerializerSupplierType;
import com.io7m.jsx.api.serializer.JSXSerializerType;
import org.osgi.service.component.annotations.Component;

/**
 * A supplier that supplies trivial serializers.
 */

@Component
public final class JSXSerializerTrivialSupplier
  implements JSXSerializerSupplierType
{
  /**
   * Instantiate a supplier.
   */

  public JSXSerializerTrivialSupplier()
  {

  }

  /**
   * @return A new serializer supplier
   */

  public static JSXSerializerSupplierType createSupplier()
  {
    return new JSXSerializerTrivialSupplier();
  }

  @Override
  public JSXSerializerType create()
  {
    return JSXSerializerTrivial.newSerializer();
  }
}
