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

import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.lexer.JSXLexerException;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfigurationType;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserGrammarException;
import com.io7m.jsx.api.parser.JSXParserLexicalException;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.api.tokens.TokenEOF;
import com.io7m.jsx.api.tokens.TokenLeftParenthesis;
import com.io7m.jsx.api.tokens.TokenLeftSquare;
import com.io7m.jsx.api.tokens.TokenQuotedString;
import com.io7m.jsx.api.tokens.TokenRightParenthesis;
import com.io7m.jsx.api.tokens.TokenRightSquare;
import com.io7m.jsx.api.tokens.TokenSymbol;
import com.io7m.jsx.api.tokens.TokenType;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The default implementation of the {@link JSXParserType} type.
 */

public final class JSXParser implements JSXParserType
{
  private final JSXParserConfigurationType config;
  private final JSXLexerType lexer;

  private JSXParser(
    final JSXParserConfigurationType in_config,
    final JSXLexerType in_lexer)
  {
    this.config = NullCheck.notNull(in_config);
    this.lexer = NullCheck.notNull(in_lexer);
  }

  private static SExpressionQuotedStringType completeQuotedString(
    final JSXParserConfigurationType c,
    final TokenQuotedString t)
  {
    return new PQuotedString(t.getText(), JSXParser.getTokenLexical(c, t));
  }

  private static SExpressionType completeSymbol(
    final JSXParserConfigurationType c,
    final TokenSymbol t)
  {
    final Optional<LexicalPositionType<Path>> lex =
      JSXParser.getTokenLexical(c, t);
    return new PSymbol(t.getText(), lex);
  }

  private static Optional<LexicalPositionType<Path>> getTokenLexical(
    final JSXParserConfigurationType c,
    final TokenType t)
  {
    final Optional<LexicalPositionType<Path>> lex;
    if (!c.preserveLexical()) {
      lex = Optional.empty();
    } else {
      lex = Optional.of(t.lexicalInformation());
    }
    return lex;
  }

  private static JSXParserGrammarException errorUnexpectedEOF(
    final TokenEOF t)
  {
    return new JSXParserGrammarException(
      t.lexicalInformation(), "Unexpected EOF during list parsing");
  }

  private static JSXParserGrammarException errorUnexpectedRightParen(
    final TokenRightParenthesis t)
  {
    return new JSXParserGrammarException(
      t.lexicalInformation(), "Unbalanced parentheses (unexpected ')')");
  }

  private static JSXParserGrammarException
  errorUnexpectedRightParenWantedSquare(
    final TokenRightParenthesis t)
  {
    return new JSXParserGrammarException(
      t.lexicalInformation(),
      "Attempted to end a list started with '[' with ')' - unbalanced "
        + "round/square brackets");
  }

  private static JSXParserGrammarException errorUnexpectedRightSquare(
    final TokenRightSquare t)
  {
    return new JSXParserGrammarException(
      t.lexicalInformation(), "Unbalanced parentheses (unexpected ']')");
  }

  private static JSXParserGrammarException
  errorUnexpectedRightSquareWantedParens(
    final TokenRightSquare t)
  {
    return new JSXParserGrammarException(
      t.lexicalInformation(),
      "Attempted to end a list started with '(' with ']' - unbalanced "
        + "round/square brackets");
  }

  /**
   * Construct a new parser.
   *
   * @param pc  The parser configuration
   * @param lex A lexer
   *
   * @return A new parser
   */

  public static JSXParserType newParser(
    final JSXParserConfigurationType pc,
    final JSXLexerType lex)
  {
    return new JSXParser(pc, lex);
  }

  private static SExpressionType parseExpressionPeeked(
    final JSXParserConfigurationType c,
    final JSXLexerType lexer,
    final TokenType peek)
    throws JSXLexerException, IOException, JSXParserGrammarException
  {
    if (peek instanceof TokenLeftParenthesis) {
      return JSXParser.parseListParens(c, lexer, (TokenLeftParenthesis) peek);
    }
    if (peek instanceof TokenLeftSquare) {
      return JSXParser.parseListSquares(c, lexer, (TokenLeftSquare) peek);
    }
    if (peek instanceof TokenRightSquare) {
      throw JSXParser.errorUnexpectedRightSquare((TokenRightSquare) peek);
    }
    if (peek instanceof TokenRightParenthesis) {
      throw JSXParser.errorUnexpectedRightParen((TokenRightParenthesis) peek);
    }
    if (peek instanceof TokenQuotedString) {
      return JSXParser.completeQuotedString(c, (TokenQuotedString) peek);
    }
    if (peek instanceof TokenSymbol) {
      return JSXParser.completeSymbol(c, (TokenSymbol) peek);
    }
    if (peek instanceof TokenEOF) {
      throw JSXParser.errorUnexpectedEOF((TokenEOF) peek);
    }

    throw new UnreachableCodeException();
  }

  private static SExpressionType parseListParens(
    final JSXParserConfigurationType c,
    final JSXLexerType lexer,
    final TokenLeftParenthesis peek)
    throws JSXLexerException, IOException, JSXParserGrammarException
  {
    final PList xs = new PList(
      new ArrayList<>(16), JSXParser.getTokenLexical(c, peek), false);

    while (true) {
      final TokenType t = lexer.token();
      if (t instanceof TokenEOF) {
        throw JSXParser.errorUnexpectedEOF((TokenEOF) t);
      }
      if (t instanceof TokenRightParenthesis) {
        return xs;
      }
      if (t instanceof TokenRightSquare) {
        throw JSXParser.errorUnexpectedRightSquareWantedParens(
          (TokenRightSquare) t);
      }
      xs.add(JSXParser.parseExpressionPeeked(c, lexer, t));
    }
  }

  private static SExpressionType parseListSquares(
    final JSXParserConfigurationType c,
    final JSXLexerType lexer,
    final TokenLeftSquare peek)
    throws JSXLexerException, IOException, JSXParserGrammarException
  {
    final PList xs = new PList(
      new ArrayList<>(16), JSXParser.getTokenLexical(c, peek), true);

    while (true) {
      final TokenType t = lexer.token();
      if (t instanceof TokenEOF) {
        throw JSXParser.errorUnexpectedEOF((TokenEOF) t);
      }
      if (t instanceof TokenRightParenthesis) {
        throw JSXParser.errorUnexpectedRightParenWantedSquare(
          (TokenRightParenthesis) t);
      }
      if (t instanceof TokenRightSquare) {
        return xs;
      }
      xs.add(JSXParser.parseExpressionPeeked(c, lexer, t));
    }
  }

  @Override
  public SExpressionType parseExpression()
    throws JSXParserException, IOException
  {
    try {
      final TokenType peek = this.lexer.token();
      return JSXParser.parseExpressionPeeked(this.config, this.lexer, peek);
    } catch (final JSXLexerException e) {
      throw new JSXParserLexicalException(e);
    }
  }

  @Override
  public Optional<SExpressionType> parseExpressionOrEOF()
    throws JSXParserException, IOException
  {
    try {
      final TokenType peek = this.lexer.token();
      if (peek instanceof TokenEOF) {
        return Optional.empty();
      }

      return Optional.of(
        JSXParser.parseExpressionPeeked(this.config, this.lexer, peek));
    } catch (final JSXLexerException e) {
      throw new JSXParserLexicalException(e);
    }
  }

  @Override
  public List<SExpressionType> parseExpressions()
    throws JSXParserException, IOException
  {
    try {
      final List<SExpressionType> xs = new ArrayList<>(64);
      while (true) {
        final TokenType peek = this.lexer.token();
        if (peek instanceof TokenEOF) {
          return xs;
        }
        xs.add(JSXParser.parseExpressionPeeked(this.config, this.lexer, peek));
      }
    } catch (final JSXLexerException e) {
      throw new JSXParserLexicalException(e);
    }
  }
}
