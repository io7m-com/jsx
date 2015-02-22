/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

import java.io.File;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jsx.ListType;
import com.io7m.jsx.QuotedStringType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SymbolType;
import com.io7m.jsx.lexer.LexerException;
import com.io7m.jsx.lexer.LexerType;
import com.io7m.jsx.lexer.Position;
import com.io7m.jsx.tokens.TokenEOF;
import com.io7m.jsx.tokens.TokenLeftParenthesis;
import com.io7m.jsx.tokens.TokenQuotedString;
import com.io7m.jsx.tokens.TokenRightParenthesis;
import com.io7m.jsx.tokens.TokenSymbol;
import com.io7m.jsx.tokens.TokenType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The default implementation of the {@link ParserType} type.
 */

public final class Parser implements ParserType
{
  private static final class PList extends AbstractList<SExpressionType> implements
    ListType
  {
    private final List<SExpressionType> data;
    private final File                  file;
    private final Position              position;

    PList(
      final List<SExpressionType> d,
      final Position p,
      final File f)
    {
      this.data = NullCheck.notNull(d);
      this.position = NullCheck.notNull(p);
      this.file = NullCheck.notNull(f);
    }

    @Override public void add(
      final int index,
      final @Nullable SExpressionType element)
    {
      this.data.add(index, NullCheck.notNull(element));
    }

    @Override public SExpressionType get(
      final int index)
    {
      return NullCheck.notNull(this.data.get(index));
    }

    @Override public File getFile()
    {
      return this.file;
    }

    @Override public Position getPosition()
    {
      return this.position;
    }

    @Override public <A, E extends Exception> A matchExpression(
      final SExpressionMatcherType<A, E> m)
      throws E
    {
      return m.list(this);
    }

    @Override public SExpressionType remove(
      final int index)
    {
      return NullCheck.notNull(this.data.remove(index));
    }

    @Override public SExpressionType set(
      final int index,
      final @Nullable SExpressionType element)
    {
      return NullCheck.notNull(this.data.set(
        index,
        NullCheck.notNull(element)));
    }

    @Override public int size()
    {
      return this.data.size();
    }
  }

  private static final class PQuotedString implements QuotedStringType
  {
    private final File     file;
    private final Position position;
    private final String   text;

    PQuotedString(
      final String t,
      final Position p,
      final File f)
    {
      this.text = NullCheck.notNull(t);
      this.position = NullCheck.notNull(p);
      this.file = NullCheck.notNull(f);
    }

    @Override public File getFile()
    {
      return this.file;
    }

    @Override public Position getPosition()
    {
      return this.position;
    }

    @Override public String getText()
    {
      return this.text;
    }

    @Override public <A, E extends Exception> A matchExpression(
      final SExpressionMatcherType<A, E> m)
      throws E
    {
      return m.quotedString(this);
    }
  }

  private static final class PSymbol implements SymbolType
  {
    private final File     file;
    private final Position position;
    private final String   text;

    PSymbol(
      final String t,
      final Position p,
      final File f)
    {
      this.text = NullCheck.notNull(t);
      this.position = NullCheck.notNull(p);
      this.file = NullCheck.notNull(f);
    }

    @Override public File getFile()
    {
      return this.file;
    }

    @Override public Position getPosition()
    {
      return this.position;
    }

    @Override public String getText()
    {
      return this.text;
    }

    @Override public <A, E extends Exception> A matchExpression(
      final SExpressionMatcherType<A, E> m)
      throws E
    {
      return m.symbol(this);
    }
  }

  private static final File FILE_NONE = new File("<none>");

  private static QuotedStringType completeQuotedString(
    final ParserConfiguration c,
    final TokenQuotedString t)
  {
    if (c.preserveLexicalInformation() == false) {
      return new PQuotedString(t.getText(), Position.ZERO, Parser.FILE_NONE);
    }
    return new PQuotedString(t.getText(), t.getPosition(), t.getFile());
  }

  private static SExpressionType completeSymbol(
    final ParserConfiguration c,
    final TokenSymbol t)
  {
    if (c.preserveLexicalInformation() == false) {
      return new PSymbol(t.getText(), Position.ZERO, Parser.FILE_NONE);
    }
    return new PSymbol(t.getText(), t.getPosition(), t.getFile());
  }

  private static ParserGrammarException errorUnexpectedEOF(
    final TokenEOF t)
  {
    return new ParserGrammarException(
      t.getPosition(),
      t.getFile(),
      "Unexpected EOF during list parsing");
  }

  private static ParserGrammarException errorUnexpectedRightParen(
    final TokenRightParenthesis t)
  {
    return new ParserGrammarException(
      t.getPosition(),
      t.getFile(),
      "Unbalanced parentheses (unexpected ')')");
  }

  public static ParserType newParser(
    final ParserConfiguration pc,
    final LexerType lex)
  {
    return new Parser(pc, lex);
  }

  private static SExpressionType parseExpressionPeeked(
    final ParserConfiguration c,
    final LexerType lexer,
    final TokenType peek)
    throws LexerException,
      IOException,
      ParserGrammarException
  {
    if (peek instanceof TokenLeftParenthesis) {
      return Parser.parseList(c, lexer, (TokenLeftParenthesis) peek);
    }
    if (peek instanceof TokenRightParenthesis) {
      throw Parser.errorUnexpectedRightParen((TokenRightParenthesis) peek);
    }
    if (peek instanceof TokenQuotedString) {
      return Parser.completeQuotedString(c, (TokenQuotedString) peek);
    }
    if (peek instanceof TokenSymbol) {
      return Parser.completeSymbol(c, (TokenSymbol) peek);
    }
    if (peek instanceof TokenEOF) {
      throw Parser.errorUnexpectedEOF((TokenEOF) peek);
    }

    throw new UnreachableCodeException();
  }

  private static SExpressionType parseList(
    final ParserConfiguration c,
    final LexerType lexer,
    final TokenLeftParenthesis peek)
    throws LexerException,
      IOException,
      ParserGrammarException
  {
    final PList xs;
    if (c.preserveLexicalInformation()) {
      xs =
        new PList(
          new ArrayList<SExpressionType>(),
          peek.getPosition(),
          peek.getFile());
    } else {
      xs =
        new PList(
          new ArrayList<SExpressionType>(),
          Position.ZERO,
          Parser.FILE_NONE);
    }

    for (;;) {
      final TokenType t = lexer.token();
      if (t instanceof TokenEOF) {
        throw Parser.errorUnexpectedEOF((TokenEOF) t);
      }
      if (t instanceof TokenRightParenthesis) {
        return xs;
      }
      xs.add(Parser.parseExpressionPeeked(c, lexer, t));
    }
  }

  private final ParserConfiguration config;
  private final LexerType           lexer;

  private Parser(
    final ParserConfiguration in_config,
    final LexerType in_lexer)
  {
    this.config = NullCheck.notNull(in_config);
    this.lexer = NullCheck.notNull(in_lexer);
  }

  @Override public SExpressionType parseExpression()
    throws ParserException,
      IOException
  {
    try {
      final TokenType peek = this.lexer.token();
      return Parser.parseExpressionPeeked(this.config, this.lexer, peek);
    } catch (final LexerException e) {
      throw new ParserLexicalException(e);
    }
  }

  @Override public List<SExpressionType> parseExpressions()
    throws ParserException,
      IOException
  {
    try {
      final List<SExpressionType> xs = new ArrayList<SExpressionType>();
      for (;;) {
        final TokenType peek = this.lexer.token();
        if (peek instanceof TokenEOF) {
          return xs;
        }
        xs.add(Parser.parseExpressionPeeked(this.config, this.lexer, peek));
      }
    } catch (final LexerException e) {
      throw new ParserLexicalException(e);
    }
  }
}
