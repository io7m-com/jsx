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

package com.io7m.jsx.tests.combinators;

import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.combinators.JSXCombinators;
import com.io7m.jsx.combinators.JSXValidationError;
import com.io7m.jsx.combinators.JSXValidationErrorType;
import javaslang.collection.List;
import javaslang.control.Validation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class JSXCombinatorsTest
{
  @Test
  public void testAnySymbol()
  {
    Assert.assertTrue(
      JSXCombinators.anySymbol(new PSymbol("x", Optional.empty())).isValid());

    Assert.assertFalse(
      JSXCombinators.anySymbol(
        new PList(new ArrayList<>(1), Optional.empty(), false)).isValid());

    Assert.assertFalse(
      JSXCombinators.anySymbol(
        new PQuotedString("xyz", Optional.empty())).isValid());
  }

  @Test
  public void testAnyOfSymbol()
  {
    Assert.assertTrue(
      JSXCombinators.anyOfSymbol(
        new PSymbol("x", Optional.empty()), List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfSymbol(
        new PSymbol("x", Optional.empty()), List.of("y")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfSymbol(
        new PList(new ArrayList<>(1), Optional.empty(), false),
        List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfSymbol(
        new PQuotedString("xyz", Optional.empty()),
        List.of("x")).isValid());
  }

  @Test
  public void testAnyList()
  {
    Assert.assertFalse(
      JSXCombinators.anyList(new PSymbol("x", Optional.empty())).isValid());

    Assert.assertTrue(
      JSXCombinators.anyList(
        new PList(new ArrayList<>(1), Optional.empty(), false)).isValid());

    Assert.assertFalse(
      JSXCombinators.anyList(
        new PQuotedString("xyz", Optional.empty())).isValid());
  }

  @Test
  public void testAnyQuotedString()
  {
    Assert.assertFalse(
      JSXCombinators.anyQuotedString(new PSymbol("x", Optional.empty())).isValid());

    Assert.assertFalse(
      JSXCombinators.anyQuotedString(
        new PList(new ArrayList<>(1), Optional.empty(), false)).isValid());

    Assert.assertTrue(
      JSXCombinators.anyQuotedString(
        new PQuotedString("xyz", Optional.empty())).isValid());
  }

  @Test
  public void testAnyOfQuotedString()
  {
    Assert.assertTrue(
      JSXCombinators.anyOfQuotedString(
        new PQuotedString("x", Optional.empty()), List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfQuotedString(
        new PQuotedString("x", Optional.empty()), List.of("y")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfQuotedString(
        new PList(new ArrayList<>(1), Optional.empty(), false),
        List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfQuotedString(
        new PSymbol("xyz", Optional.empty()),
        List.of("x")).isValid());
  }

  @Test
  public void testListAtEmpty()
  {
    final PList list = new PList(new ArrayList<>(1), Optional.empty(), false);

    final Function<SExpressionType, Validation<List<JSXValidationErrorType>, Integer>> receiver =
      e -> Validation.valid(Integer.valueOf(23));

    Assert.assertFalse(
      JSXCombinators.atListElement(list, -1, receiver).isValid());
    Assert.assertFalse(
      JSXCombinators.atListElement(list, 0, receiver).isValid());
    Assert.assertFalse(
      JSXCombinators.atListElement(list, 1, receiver).isValid());
  }

  @Test
  public void testListAtOK()
  {
    final PList list = new PList(new ArrayList<>(1), Optional.empty(), false);
    list.add(new PSymbol("23", Optional.empty()));

    final AtomicInteger calls = new AtomicInteger(0);
    final Function<SExpressionType, Validation<List<JSXValidationErrorType>, Integer>> receiver =
      e -> JSXCombinators.anySymbol(e).flatMap(s -> {
        calls.incrementAndGet();
        return Validation.valid(Integer.valueOf(s.getText()));
      });

    Assert.assertFalse(
      JSXCombinators.atListElement(list, -1, receiver).isValid());
    Assert.assertTrue(
      JSXCombinators.atListElement(list, 0, receiver).isValid());
    Assert.assertFalse(
      JSXCombinators.atListElement(list, 1, receiver).isValid());

    Assert.assertEquals(1L, (long) calls.get());
  }

  @Test
  public void testExactSymbol()
  {
    Assert.assertTrue(
      JSXCombinators.exactSymbol(
        new PSymbol("x", Optional.empty()),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactSymbol(
        new PSymbol("y", Optional.empty()),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactSymbol(
        new PList(new ArrayList<>(1), Optional.empty(), false),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactSymbol(
        new PQuotedString("xyz", Optional.empty()),
        "x").isValid());
  }

  @Test
  public void testExactQuotedString()
  {
    Assert.assertTrue(
      JSXCombinators.exactQuotedString(
        new PQuotedString("x", Optional.empty()),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactQuotedString(
        new PQuotedString("y", Optional.empty()),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactQuotedString(
        new PList(new ArrayList<>(1), Optional.empty(), false),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactQuotedString(
        new PSymbol("x", Optional.empty()),
        "x").isValid());
  }

  @Test
  public void testListMap()
  {
    final ArrayList<SExpressionType> xs = new ArrayList<>(3);
    xs.add(new PSymbol("x", Optional.empty()));
    xs.add(new PSymbol("y", Optional.empty()));
    xs.add(new PSymbol("z", Optional.empty()));

    Assert.assertTrue(
      JSXCombinators.listMap(
        new PList(xs, Optional.empty(), false),
        x -> Validation.valid(Integer.valueOf(23))).isValid());

    final Function<SExpressionType, Validation<List<JSXValidationErrorType>, Integer>> fail =
      e -> {
        final JSXValidationError err = JSXValidationError.of(
          ImmutableLexicalPosition.newPosition(0, 0),
          "Error");
        return Validation.invalid(List.of(err));
      };

    Assert.assertFalse(
      JSXCombinators.listMap(
        new PList(xs, Optional.empty(), false),
        fail).isValid());
  }
}
