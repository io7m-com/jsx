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

package com.io7m.jsx.parser;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SQuotedString;
import com.io7m.jsx.SExpressionType.SSymbol;
import com.io7m.jsx.api.lexer.JSXLexerException;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserGrammarException;
import com.io7m.jsx.api.parser.JSXParserLexicalException;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.api.tokens.TokenComment;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The default implementation of the {@link JSXParserType} type.
 */

public final class JSXParser implements JSXParserType
{
  private static final LexicalPosition<URI> LEX_DEFAULT =
    LexicalPosition.of(1, 0, Optional.empty());

  private final JSXParserConfiguration config;
  private final JSXLexerType lexer;

  private JSXParser(
    final JSXParserConfiguration in_config,
    final JSXLexerType in_lexer)
  {
    this.config = Objects.requireNonNull(in_config, "Configuration");
    this.lexer = Objects.requireNonNull(in_lexer, "Lexer");
  }

  private static SQuotedString completeQuotedString(
    final JSXParserConfiguration c,
    final TokenQuotedString t)
  {
    return new SQuotedString(getTokenLexical(c, t), t.text());
  }

  private static SExpressionType completeSymbol(
    final JSXParserConfiguration c,
    final TokenSymbol t)
  {
    return new SSymbol(getTokenLexical(c, t), t.text());
  }

  private static LexicalPosition<URI> getTokenLexical(
    final JSXParserConfiguration c,
    final TokenType t)
  {
    final LexicalPosition<URI> lex;
    if (!c.preserveLexical()) {
      lex = LEX_DEFAULT;
    } else {
      lex = t.lexical();
    }
    return lex;
  }

  private static JSXParserGrammarException errorUnexpectedEOF(
    final LexicalPosition<URI> lexical)
  {
    return new JSXParserGrammarException(
      lexical, "Unexpected EOF during list parsing");
  }

  private static JSXParserGrammarException errorUnexpectedRightParen(
    final TokenRightParenthesis t)
  {
    return new JSXParserGrammarException(
      t.lexical(), "Unbalanced parentheses (unexpected ')')");
  }

  private static JSXParserGrammarException
  errorUnexpectedRightParenWantedSquare(
    final TokenRightParenthesis t)
  {
    return new JSXParserGrammarException(
      t.lexical(),
      "Attempted to end a list started with '[' with ')' - unbalanced round/square brackets");
  }

  private static JSXParserGrammarException errorUnexpectedRightSquare(
    final TokenRightSquare t)
  {
    return new JSXParserGrammarException(
      t.lexical(), "Unbalanced parentheses (unexpected ']')");
  }

  private static JSXParserGrammarException
  errorUnexpectedRightSquareWantedParens(
    final TokenRightSquare t)
  {
    return new JSXParserGrammarException(
      t.lexical(),
      "Attempted to end a list started with '(' with ']' - unbalanced round/square brackets");
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
    final JSXParserConfiguration pc,
    final JSXLexerType lex)
  {
    return new JSXParser(pc, lex);
  }

  private static SExpressionType parseExpressionPeeked(
    final JSXParserConfiguration c,
    final JSXLexerType lexer,
    final TokenType peek)
    throws JSXLexerException, IOException, JSXParserGrammarException
  {
    if (peek instanceof TokenLeftParenthesis) {
      return parseListParens(c, lexer, (TokenLeftParenthesis) peek);
    }
    if (peek instanceof TokenLeftSquare) {
      return parseListSquares(c, lexer, (TokenLeftSquare) peek);
    }
    if (peek instanceof TokenRightSquare) {
      throw errorUnexpectedRightSquare((TokenRightSquare) peek);
    }
    if (peek instanceof TokenRightParenthesis) {
      throw errorUnexpectedRightParen((TokenRightParenthesis) peek);
    }
    if (peek instanceof TokenQuotedString) {
      return completeQuotedString(c, (TokenQuotedString) peek);
    }
    if (peek instanceof TokenSymbol) {
      return completeSymbol(c, (TokenSymbol) peek);
    }
    if (peek instanceof TokenEOF) {
      throw errorUnexpectedEOF(peek.lexical());
    }

    throw new UnreachableCodeException();
  }

  private static SExpressionType parseListParens(
    final JSXParserConfiguration c,
    final JSXLexerType lexer,
    final TokenLeftParenthesis peek)
    throws JSXLexerException, IOException, JSXParserGrammarException
  {
    final var subExpressions =
      new ArrayList<SExpressionType>(16);
    final var initialLocation =
      getTokenLexical(c, peek);

    while (true) {
      final TokenType t = lexer.token();
      if (t instanceof TokenEOF) {
        throw errorUnexpectedEOF(initialLocation);
      }
      if (t instanceof TokenComment) {
        continue;
      }
      if (t instanceof TokenRightParenthesis) {
        return new SList(initialLocation, false, subExpressions);
      }
      if (t instanceof TokenRightSquare) {
        throw errorUnexpectedRightSquareWantedParens(
          (TokenRightSquare) t);
      }
      subExpressions.add(parseExpressionPeeked(c, lexer, t));
    }
  }

  private static SExpressionType parseListSquares(
    final JSXParserConfiguration c,
    final JSXLexerType lexer,
    final TokenLeftSquare peek)
    throws JSXLexerException, IOException, JSXParserGrammarException
  {
    final var subExpressions =
      new ArrayList<SExpressionType>(16);
    final var initialLocation =
      getTokenLexical(c, peek);

    while (true) {
      final TokenType t = lexer.token();
      if (t instanceof TokenEOF) {
        throw errorUnexpectedEOF(initialLocation);
      }
      if (t instanceof TokenComment) {
        continue;
      }
      if (t instanceof TokenRightParenthesis) {
        throw errorUnexpectedRightParenWantedSquare(
          (TokenRightParenthesis) t);
      }
      if (t instanceof TokenRightSquare) {
        return new SList(initialLocation, true, subExpressions);
      }
      subExpressions.add(parseExpressionPeeked(c, lexer, t));
    }
  }

  @Override
  public SExpressionType parseExpression()
    throws JSXParserException, IOException
  {
    try {
      final TokenType peek = this.lexer.token();
      return parseExpressionPeeked(this.config, this.lexer, peek);
    } catch (final JSXLexerException e) {
      throw new JSXParserLexicalException(e);
    }
  }

  @Override
  public Optional<SExpressionType> parseExpressionOrEOF()
    throws JSXParserException, IOException
  {
    try {
      while (true) {
        final TokenType peek = this.lexer.token();
        if (peek instanceof TokenEOF) {
          return Optional.empty();
        }
        if (peek instanceof TokenComment) {
          continue;
        }
        return Optional.of(
          parseExpressionPeeked(this.config, this.lexer, peek));
      }
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
        if (peek instanceof TokenComment) {
          continue;
        }
        xs.add(parseExpressionPeeked(this.config, this.lexer, peek));
      }
    } catch (final JSXLexerException e) {
      throw new JSXParserLexicalException(e);
    }
  }
}
