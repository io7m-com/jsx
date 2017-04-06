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

package com.io7m.jsx.cmdline;

import com.beust.jcommander.IStringConverter;

import java.util.Objects;

/**
 * A converter for {@link JSXLogLevel} values.
 */

public final class JSXLogLevelConverter implements
  IStringConverter<JSXLogLevel>
{
  /**
   * Construct a new converter.
   */

  public JSXLogLevelConverter()
  {

  }

  @Override
  public JSXLogLevel convert(final String value)
  {
    for (final JSXLogLevel v : JSXLogLevel.values()) {
      if (Objects.equals(value, v.getName())) {
        return v;
      }
    }

    throw new JSXLogLevelUnrecognized(
      "Unrecognized verbosity level: " + value);
  }
}
