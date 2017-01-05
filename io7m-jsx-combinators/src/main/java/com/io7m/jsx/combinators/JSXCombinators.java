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

package com.io7m.jsx.combinators;

import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import com.io7m.junreachable.UnreachableCodeException;
import javaslang.Value;
import javaslang.collection.List;
import javaslang.control.Validation;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Convenient combinators for validating and extracting data from S-expressions.
 */

public final class JSXCombinators
{
  private JSXCombinators()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Retrieve a value from a list.
   *
   * @param e        The list expression
   * @param index    The list index
   * @param receiver The receiver
   * @param <T>      The type of returned values
   *
   * @return A validation value based upon the result of applying {@code
   * receiver} to the list element, or an invalid value if the list index is out
   * of range
   */

  public static <T>
  Validation<List<JSXValidationErrorType>, T>
  atListElement(
    final SExpressionListType e,
    final int index,
    final Function<SExpressionType, Validation<List<JSXValidationErrorType>, T>> receiver)
  {
    if (index >= 0 && index < e.size()) {
      return receiver.apply(e.get(index));
    }

    final StringBuilder sb = new StringBuilder(128);
    sb.append("List has fewer than the required number of elements.");
    sb.append(System.lineSeparator());

    sb.append("  Expected: A list with at least ");
    sb.append(index + 1);
    sb.append(" elements");
    sb.append(System.lineSeparator());

    sb.append("  Received: A list ");
    sb.append(e);
    sb.append(System.lineSeparator());

    return Validation.invalid(List.of(JSXValidationError.of(
      e.getLexicalInformation().orElse(
        ImmutableLexicalPosition.newPosition(0, 0)),
      sb.toString())));
  }

  /**
   * Assert that the given expression is a symbol with the given name.
   *
   * @param e    The expression
   * @param name The symbol name
   *
   * @return The symbol, or an invalid value if {@code e} is not a symbol with
   * the given name
   */

  public static Validation<List<JSXValidationErrorType>, SExpressionSymbolType>
  exactSymbol(
    final SExpressionType e,
    final String name)
  {
    return anySymbol(e).flatMap(s -> {
      if (name.equals(s.getText())) {
        return Validation.valid(s);
      }

      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a symbol '");
      sb.append(name);
      sb.append("' but received a symbol '");
      sb.append(s.getText());
      sb.append("'");
      sb.append(System.lineSeparator());

      sb.append("  Expected: A symbol '");
      sb.append(name);
      sb.append("'");
      sb.append(System.lineSeparator());

      sb.append("  Received: A symbol '");
      sb.append(s.getText());
      sb.append("'");
      sb.append(System.lineSeparator());

      return Validation.invalid(List.of(JSXValidationError.of(
        s.getLexicalInformation().orElse(
          ImmutableLexicalPosition.newPosition(0, 0)),
        sb.toString())));
    });
  }

  /**
   * Assert that the given expression is a symbol with any of the given names.
   *
   * @param e     The expression
   * @param names The symbol names
   *
   * @return The symbol, or an invalid value if {@code e} is not a symbol with
   * the given name
   */

  public static Validation<List<JSXValidationErrorType>, SExpressionSymbolType>
  anyOfSymbol(
    final SExpressionType e,
    final List<String> names)
  {
    return anySymbol(e).flatMap(s -> {
      if (!names.filter(name -> Objects.equals(s.getText(), name)).isEmpty()) {
        return Validation.valid(s);
      }

      final String options =
        names.toJavaStream().collect(Collectors.joining("|"));

      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a symbol ");
      sb.append(options);
      sb.append(" but received a symbol '");
      sb.append(s.getText());
      sb.append("'");
      sb.append(System.lineSeparator());

      sb.append("  Expected: ");
      sb.append(options);
      sb.append(System.lineSeparator());

      sb.append("  Received: A symbol '");
      sb.append(s.getText());
      sb.append("'");
      sb.append(System.lineSeparator());

      return Validation.invalid(List.of(JSXValidationError.of(
        s.getLexicalInformation().orElse(
          ImmutableLexicalPosition.newPosition(0, 0)),
        sb.toString())));
    });
  }

  /**
   * Assert that the given expression is a quoted string with the given name.
   *
   * @param e    The expression
   * @param name The quoted string name
   *
   * @return The quoted string, or an invalid value if {@code e} is not a quoted
   * string with the given name
   */

  public static Validation<List<JSXValidationErrorType>, SExpressionQuotedStringType>
  exactQuotedString(
    final SExpressionType e,
    final String name)
  {
    return anyQuotedString(e).flatMap(s -> {
      if (name.equals(s.getText())) {
        return Validation.valid(s);
      }

      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a quoted string \"");
      sb.append(name);
      sb.append("\" but received a quoted string \"");
      sb.append(s.getText());
      sb.append("\"");
      sb.append(System.lineSeparator());

      sb.append("  Expected: A quoted string \"");
      sb.append(name);
      sb.append("\"");
      sb.append(System.lineSeparator());

      sb.append("  Received: A quoted string \"");
      sb.append(s.getText());
      sb.append("\"");
      sb.append(System.lineSeparator());

      return Validation.invalid(List.of(JSXValidationError.of(
        s.getLexicalInformation().orElse(
          ImmutableLexicalPosition.newPosition(0, 0)),
        sb.toString())));
    });
  }

  /**
   * Assert that the given expression is a symbol with any of the given names.
   *
   * @param e     The expression
   * @param texts The quoted string texts
   *
   * @return The symbol, or an invalid value if {@code e} is not a symbol with
   * the given name
   */

  public static Validation<List<JSXValidationErrorType>, SExpressionQuotedStringType>
  anyOfQuotedString(
    final SExpressionType e,
    final List<String> texts)
  {
    return anyQuotedString(e).flatMap(s -> {
      if (!texts.filter(text -> Objects.equals(s.getText(), text)).isEmpty()) {
        return Validation.valid(s);
      }

      final String options =
        texts.toJavaStream()
          .map(str -> "\"" + str + "\"")
          .collect(Collectors.joining("|"));

      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a quoted string ");
      sb.append(options);
      sb.append(" but received a quoted string \"");
      sb.append(s.getText());
      sb.append("\"");
      sb.append(System.lineSeparator());

      sb.append("  Expected: ");
      sb.append(options);
      sb.append(System.lineSeparator());

      sb.append("  Received: A symbol '");
      sb.append(s.getText());
      sb.append("'");
      sb.append(System.lineSeparator());

      return Validation.invalid(List.of(JSXValidationError.of(
        s.getLexicalInformation().orElse(
          ImmutableLexicalPosition.newPosition(0, 0)),
        sb.toString())));
    });
  }

  /**
   * Assert that the given expression is a symbol.
   *
   * @param e The expression
   *
   * @return The symbol, or an invalid value if {@code e} is not a symbol
   */

  public static Validation<List<JSXValidationErrorType>, SExpressionSymbolType>
  anySymbol(
    final SExpressionType e)
  {
    return e.matchExpression(
      new SExpressionMatcherType<
        Validation<List<JSXValidationErrorType>,
          SExpressionSymbolType>,
        UnreachableCodeException>()
      {
        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionSymbolType>
        list(final SExpressionListType ex)
          throws UnreachableCodeException
        {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Expected a symbol but got a list.");
          sb.append(System.lineSeparator());

          sb.append("  Expected: A symbol");
          sb.append(System.lineSeparator());

          sb.append("  Received: ");
          sb.append(ex);
          sb.append(System.lineSeparator());

          return Validation.invalid(List.of(JSXValidationError.of(
            ex.getLexicalInformation().orElse(
              ImmutableLexicalPosition.newPosition(0, 0)),
            sb.toString())));
        }

        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionSymbolType>
        quotedString(final SExpressionQuotedStringType ex)
          throws UnreachableCodeException
        {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Expected a symbol but got a quoted string.");
          sb.append(System.lineSeparator());

          sb.append("  Expected: A symbol");
          sb.append(System.lineSeparator());

          sb.append("  Received: A quoted string \"");
          sb.append(ex);
          sb.append("\"");
          sb.append(System.lineSeparator());

          return Validation.invalid(List.of(JSXValidationError.of(
            ex.getLexicalInformation().orElse(
              ImmutableLexicalPosition.newPosition(0, 0)),
            sb.toString())));
        }

        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionSymbolType>
        symbol(final SExpressionSymbolType ex)
          throws UnreachableCodeException
        {
          return Validation.valid(ex);
        }
      });
  }

  /**
   * Assert that the given expression is a list.
   *
   * @param e The expression
   *
   * @return The list, or an invalid value if {@code e} is not a list
   */

  public static Validation<List<JSXValidationErrorType>, SExpressionListType>
  anyList(final SExpressionType e)
  {
    return e.matchExpression(
      new SExpressionMatcherType<
        Validation<List<JSXValidationErrorType>,
          SExpressionListType>,
        UnreachableCodeException>()
      {
        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionListType>
        list(final SExpressionListType ex)
          throws UnreachableCodeException
        {
          return Validation.valid(ex);
        }

        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionListType>
        quotedString(final SExpressionQuotedStringType ex)
          throws UnreachableCodeException
        {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Expected a list but got a quoted string.");
          sb.append(System.lineSeparator());

          sb.append("  Expected: A list");
          sb.append(System.lineSeparator());

          sb.append("  Received: A quoted string \"");
          sb.append(ex);
          sb.append("\"");
          sb.append(System.lineSeparator());

          return Validation.invalid(List.of(JSXValidationError.of(
            ex.getLexicalInformation().orElse(
              ImmutableLexicalPosition.newPosition(0, 0)),
            sb.toString())));
        }

        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionListType>
        symbol(final SExpressionSymbolType ex)
          throws UnreachableCodeException
        {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Expected a list but got a symbol.");
          sb.append(System.lineSeparator());

          sb.append("  Expected: A list");
          sb.append(System.lineSeparator());

          sb.append("  Received: A symbol '");
          sb.append(ex);
          sb.append("'");
          sb.append(System.lineSeparator());

          return Validation.invalid(List.of(JSXValidationError.of(
            ex.getLexicalInformation().orElse(
              ImmutableLexicalPosition.newPosition(0, 0)),
            sb.toString())));
        }
      });
  }

  /**
   * Assert that the given expression is a quoted string.
   *
   * @param e The expression
   *
   * @return The quoted string, or an invalid value if {@code e} is not a quoted
   * string
   */

  public static Validation<List<JSXValidationErrorType>, SExpressionQuotedStringType>
  anyQuotedString(final SExpressionType e)
  {
    return e.matchExpression(
      new SExpressionMatcherType<
        Validation<List<JSXValidationErrorType>,
          SExpressionQuotedStringType>,
        UnreachableCodeException>()
      {
        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionQuotedStringType>
        list(final SExpressionListType ex)
          throws UnreachableCodeException
        {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Expected a quoted string but got a list.");
          sb.append(System.lineSeparator());

          sb.append("  Expected: A quoted string");
          sb.append(System.lineSeparator());

          sb.append("  Received: A list '");
          sb.append(ex);
          sb.append("'");
          sb.append(System.lineSeparator());

          return Validation.invalid(List.of(JSXValidationError.of(
            ex.getLexicalInformation().orElse(
              ImmutableLexicalPosition.newPosition(0, 0)),
            sb.toString())));
        }

        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionQuotedStringType>
        quotedString(final SExpressionQuotedStringType ex)
          throws UnreachableCodeException
        {
          return Validation.valid(ex);
        }

        @Override
        public Validation<List<JSXValidationErrorType>, SExpressionQuotedStringType>
        symbol(final SExpressionSymbolType ex)
          throws UnreachableCodeException
        {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Expected a quoted string but got a symbol.");
          sb.append(System.lineSeparator());

          sb.append("  Expected: A quoted string");
          sb.append(System.lineSeparator());

          sb.append("  Received: A symbol '");
          sb.append(ex);
          sb.append("'");
          sb.append(System.lineSeparator());

          return Validation.invalid(List.of(JSXValidationError.of(
            ex.getLexicalInformation().orElse(
              ImmutableLexicalPosition.newPosition(0, 0)),
            sb.toString())));
        }
      });
  }

  /**
   * Apply a validation function to each element of the given list expression.
   *
   * @param e        The list expression
   * @param receiver The validation function
   * @param <T>      The type of returned elements
   *
   * @return A valid value if all of the elements were validated, otherwise an
   * invalid value
   */

  public static <T> Validation<List<JSXValidationErrorType>, List<T>>
  listMap(
    final SExpressionListType e,
    final Function<SExpressionType, Validation<List<JSXValidationErrorType>, T>> receiver)
  {
    List<Validation<List<JSXValidationErrorType>, T>> xs = List.empty();
    for (int index = 0; index < e.size(); ++index) {
      xs = xs.append(receiver.apply(e.get(index)));
    }

    return Validation.sequence(xs).map(Value::toList);
  }
}
