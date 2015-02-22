/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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

package com.io7m.jsx.tests.lexer;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jsx.lexer.Position;

@SuppressWarnings("static-method") public class PositionTest
{
  @Test public void testEquals()
  {
    final Position p = new Position(1, 1);
    Assert.assertEquals(p, p);
    Assert.assertEquals(new Position(0, 0), new Position(0, 0));
    Assert.assertNotEquals(new Position(1, 0), new Position(0, 0));
    Assert.assertNotEquals(new Position(0, 1), new Position(0, 0));
    Assert.assertNotEquals(new Position(1, 0), Integer.valueOf(23));
    Assert.assertNotEquals(new Position(0, 0), null);
  }

  @Test public void testHashcode()
  {
    final Position p = new Position(1, 1);
    Assert.assertEquals(p, p);
    Assert.assertEquals(
      new Position(0, 0).hashCode(),
      new Position(0, 0).hashCode());
    Assert.assertNotEquals(
      new Position(1, 0).hashCode(),
      new Position(0, 0).hashCode());
    Assert.assertNotEquals(
      new Position(0, 1).hashCode(),
      new Position(0, 0).hashCode());
  }
}
