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

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.combinators.JSXCombinators;
import com.io7m.jsx.combinators.JSXValidationError;
import com.io7m.jsx.combinators.JSXValidationErrorType;
import io.vavr.collection.List;
import io.vavr.control.Validation;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class JSXCombinatorsTest
{
  private static final LexicalPosition<URI> DEFAULT_LEX =
    LexicalPosition.of(1, 0, Optional.empty());

  @Test
  public void testAnySymbol()
  {
    Assert.assertTrue(
      JSXCombinators.anySymbol(new PSymbol("x", DEFAULT_LEX)).isValid());

    Assert.assertFalse(
      JSXCombinators.anySymbol(
        new PList(new ArrayList<>(1), DEFAULT_LEX, false)).isValid());

    Assert.assertFalse(
      JSXCombinators.anySymbol(
        new PQuotedString("xyz", DEFAULT_LEX)).isValid());
  }

  @Test
  public void testAnyOfSymbol()
  {
    Assert.assertTrue(
      JSXCombinators.anyOfSymbol(
        new PSymbol("x", DEFAULT_LEX), List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfSymbol(
        new PSymbol("x", DEFAULT_LEX), List.of("y")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfSymbol(
        new PList(new ArrayList<>(1), DEFAULT_LEX, false),
        List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfSymbol(
        new PQuotedString("xyz", DEFAULT_LEX),
        List.of("x")).isValid());
  }

  @Test
  public void testAnyList()
  {
    Assert.assertFalse(
      JSXCombinators.anyList(new PSymbol("x", DEFAULT_LEX)).isValid());

    Assert.assertTrue(
      JSXCombinators.anyList(
        new PList(new ArrayList<>(1), DEFAULT_LEX, false)).isValid());

    Assert.assertFalse(
      JSXCombinators.anyList(
        new PQuotedString("xyz", DEFAULT_LEX)).isValid());
  }

  @Test
  public void testAnyQuotedString()
  {
    Assert.assertFalse(
      JSXCombinators.anyQuotedString(new PSymbol(
        "x",
        DEFAULT_LEX)).isValid());

    Assert.assertFalse(
      JSXCombinators.anyQuotedString(
        new PList(new ArrayList<>(1), DEFAULT_LEX, false)).isValid());

    Assert.assertTrue(
      JSXCombinators.anyQuotedString(
        new PQuotedString("xyz", DEFAULT_LEX)).isValid());
  }

  @Test
  public void testAnyOfQuotedString()
  {
    Assert.assertTrue(
      JSXCombinators.anyOfQuotedString(
        new PQuotedString("x", DEFAULT_LEX), List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfQuotedString(
        new PQuotedString("x", DEFAULT_LEX), List.of("y")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfQuotedString(
        new PList(new ArrayList<>(1), DEFAULT_LEX, false),
        List.of("x")).isValid());

    Assert.assertFalse(
      JSXCombinators.anyOfQuotedString(
        new PSymbol("xyz", DEFAULT_LEX),
        List.of("x")).isValid());
  }

  @Test
  public void testListAtEmpty()
  {
    final PList list = new PList(new ArrayList<>(1), DEFAULT_LEX, false);

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
    final PList list = new PList(new ArrayList<>(1), DEFAULT_LEX, false);
    list.add(new PSymbol("23", DEFAULT_LEX));

    final AtomicInteger calls = new AtomicInteger(0);
    final Function<SExpressionType, Validation<List<JSXValidationErrorType>, Integer>> receiver =
      e -> JSXCombinators.anySymbol(e).flatMap(s -> {
        calls.incrementAndGet();
        return Validation.valid(Integer.valueOf(s.text()));
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
        new PSymbol("x", DEFAULT_LEX),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactSymbol(
        new PSymbol("y", DEFAULT_LEX),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactSymbol(
        new PList(new ArrayList<>(1), DEFAULT_LEX, false),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactSymbol(
        new PQuotedString("xyz", DEFAULT_LEX),
        "x").isValid());
  }

  @Test
  public void testExactQuotedString()
  {
    Assert.assertTrue(
      JSXCombinators.exactQuotedString(
        new PQuotedString("x", DEFAULT_LEX),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactQuotedString(
        new PQuotedString("y", DEFAULT_LEX),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactQuotedString(
        new PList(new ArrayList<>(1), DEFAULT_LEX, false),
        "x").isValid());

    Assert.assertFalse(
      JSXCombinators.exactQuotedString(
        new PSymbol("x", DEFAULT_LEX),
        "x").isValid());
  }

  @Test
  public void testListMap()
  {
    final ArrayList<SExpressionType> xs = new ArrayList<>(3);
    xs.add(new PSymbol("x", DEFAULT_LEX));
    xs.add(new PSymbol("y", DEFAULT_LEX));
    xs.add(new PSymbol("z", DEFAULT_LEX));

    Assert.assertTrue(
      JSXCombinators.listMap(
        new PList(xs, DEFAULT_LEX, false),
        x -> Validation.valid(Integer.valueOf(23))).isValid());

    final Function<SExpressionType, Validation<List<JSXValidationErrorType>, Integer>> fail =
      e -> {
        final JSXValidationError err =
          JSXValidationError.of(DEFAULT_LEX, "Error");
        return Validation.invalid(List.of(err));
      };

    Assert.assertFalse(
      JSXCombinators.listMap(
        new PList(xs, DEFAULT_LEX, false),
        fail).isValid());
  }
}
