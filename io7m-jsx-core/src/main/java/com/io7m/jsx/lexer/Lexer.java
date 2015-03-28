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

package com.io7m.jsx.lexer;

import java.io.File;
import java.io.IOException;

import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jnull.NullCheck;
import com.io7m.jsx.tokens.TokenEOF;
import com.io7m.jsx.tokens.TokenLeftParenthesis;
import com.io7m.jsx.tokens.TokenLeftSquare;
import com.io7m.jsx.tokens.TokenQuotedString;
import com.io7m.jsx.tokens.TokenRightParenthesis;
import com.io7m.jsx.tokens.TokenRightSquare;
import com.io7m.jsx.tokens.TokenSymbol;
import com.io7m.jsx.tokens.TokenType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The default implementation of the {@link LexerType} type.
 */

public final class Lexer implements LexerType
{
  private static enum State
  {
    STATE_IN_CRLF,
    STATE_IN_STRING_QUOTED,
    STATE_IN_SYMBOL,
    STATE_INITIAL
  }

  public static LexerType newLexer(
    final LexerConfiguration c,
    final UnicodeCharacterReaderPushBackType r)
  {
    return new Lexer(c, r);
  }

  private final StringBuilder                      buffer;
  private int                                      buffer_start_column;
  private int                                      buffer_start_line;
  private int                                      column;
  private final LexerConfiguration                 config;
  private File                                     file;
  private int                                      line;
  private final UnicodeCharacterReaderPushBackType reader;
  private State                                    state;

  private Lexer(
    final LexerConfiguration c,
    final UnicodeCharacterReaderPushBackType r)
  {
    this.config = NullCheck.notNull(c);
    this.reader = NullCheck.notNull(r);
    this.file = new File("<stdin>");
    this.state = State.STATE_INITIAL;
    this.line = 0;
    this.column = 0;
    this.buffer = new StringBuilder(256);
  }

  private void completeNewline()
  {
    this.state = State.STATE_INITIAL;
    ++this.line;
    this.column = 0;
  }

  private TokenType completeQuotedString()
  {
    this.state = State.STATE_INITIAL;
    final String text = NullCheck.notNull(this.buffer.toString());
    this.buffer.setLength(0);
    return new TokenQuotedString(this.getFile(), new Position(
      this.buffer_start_line,
      this.buffer_start_column), text);
  }

  private TokenType completeSymbol()
  {
    this.state = State.STATE_INITIAL;
    final String text = NullCheck.notNull(this.buffer.toString());
    this.buffer.setLength(0);
    return new TokenSymbol(this.getFile(), new Position(
      this.buffer_start_line,
      this.buffer_start_column), text);
  }

  private LexerBareCarriageReturnException errorBareCarriageReturn()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("Bare carriage return (U+000D) in source");
    final String s = NullCheck.notNull(sb.toString());
    return new LexerBareCarriageReturnException(
      this.getPosition(),
      this.file,
      s);
  }

  private LexerInvalidCodePointException errorInvalidCodePoint(
    final long cp)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("Invalid code point given in escape (U+");
    sb.append(Long.toHexString(cp & 0xFFFFFFFF));
    sb.append(")");
    final String s = NullCheck.notNull(sb.toString());
    return new LexerInvalidCodePointException(
      this.getPosition(),
      this.file,
      s);
  }

  private LexerNewLinesInStringsException errorNewLinesNotInQuotedStrings()
  {
    return new LexerNewLinesInStringsException(
      this.getPosition(),
      this.file,
      "Lexer configuration does not permit newlines (U+000A or U+000D) in quoted strings");
  }

  private LexerNotHexCharException errorNotHexChar(
    final int c)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("Expected a character [0123456789aAbBcCdDeEfF] (got ");
    sb.appendCodePoint(c);
    sb.append(")");
    final String s = NullCheck.notNull(sb.toString());
    final LexerNotHexCharException e =
      new LexerNotHexCharException(this.getPosition(), this.file, s);
    return e;
  }

  private LexerUnexpectedEOFException errorUnexpectedEOF()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("Unexpected EOF");
    final String s = NullCheck.notNull(sb.toString());
    return new LexerUnexpectedEOFException(this.getPosition(), this.file, s);
  }

  private LexerUnknownEscapeCodeException errorUnknownEscape(
    final int c)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("Unknown escape code (");
    sb.appendCodePoint(c);
    sb.append(")");
    final String s = NullCheck.notNull(sb.toString());
    final LexerUnknownEscapeCodeException ex =
      new LexerUnknownEscapeCodeException(this.getPosition(), this.file, s);
    return ex;
  }

  public File getFile()
  {
    return this.file;
  }

  private Position getPosition()
  {
    return new Position(this.line, this.column);
  }

  private void parseEscape()
    throws LexerException,
      IOException
  {
    final int c = this.readCharNotEOF();
    if (c == '"') {
      this.buffer.append('"');
      return;
    }
    if (c == '\\') {
      this.buffer.append('\\');
      return;
    }
    if (c == 'r') {
      this.buffer.append('\r');
      return;
    }
    if (c == 'n') {
      this.buffer.append("\n");
      return;
    }
    if (c == 't') {
      this.buffer.append("\t");
      return;
    }
    if (c == 'u') {
      this.parseUnicode4();
      return;
    }
    if (c == 'U') {
      this.parseUnicode8();
      return;
    }

    throw this.errorUnknownEscape(c);
  }

  private void parseUnicode4()
    throws LexerException,
      IOException
  {
    final StringBuilder hexbuf = new StringBuilder();
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    final String hex = NullCheck.notNull(hexbuf.toString());
    final int code = Integer.parseInt(hex, 16);
    this.buffer.appendCodePoint(code);
  }

  private void parseUnicode8()
    throws LexerException,
      IOException
  {
    final StringBuilder hexbuf = new StringBuilder();
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    final String hex = NullCheck.notNull(hexbuf.toString());
    final long code = Long.parseLong(hex, 16);
    final int cp = (int) (code & 0xFFFFFFFF);

    if (Character.isValidCodePoint(cp) == false) {
      throw this.errorInvalidCodePoint(code);
    }

    this.buffer.appendCodePoint(cp);
  }

  private int readChar()
    throws IOException
  {
    final int c = this.reader.readCodePoint();
    if (c != -1) {
      ++this.column;
    }
    return c;
  }

  private int readCharNotEOF()
    throws IOException,
      LexerUnexpectedEOFException
  {
    final int c = this.readChar();
    if (c == -1) {
      throw this.errorUnexpectedEOF();
    }
    return c;
  }

  private int readHexCharNotEOF()
    throws LexerException,
      IOException
  {
    final int c = this.readCharNotEOF();
    switch (c) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case 'a':
      case 'A':
      case 'b':
      case 'B':
      case 'c':
      case 'C':
      case 'd':
      case 'D':
      case 'e':
      case 'E':
      case 'f':
      case 'F':
        return c;
    }

    throw this.errorNotHexChar(c);
  }

  public void setFile(
    final File f)
  {
    this.file = NullCheck.notNull(f, "File");
  }

  private void startQuotedString()
  {
    this.state = State.STATE_IN_STRING_QUOTED;
    this.buffer_start_column = this.column;
    this.buffer_start_line = this.line;
    this.buffer.setLength(0);
  }

  private void startSymbol(
    final int c)
  {
    this.state = State.STATE_IN_SYMBOL;
    this.buffer_start_column = this.column;
    this.buffer_start_line = this.line;
    this.buffer.setLength(0);
    this.buffer.appendCodePoint(c);
  }

  @Override public TokenType token()
    throws IOException,
      LexerException
  {
    return this.tokenRead();
  }

  private TokenType tokenRead()
    throws IOException,
      LexerException,
      LexerUnexpectedEOFException,
      LexerBareCarriageReturnException,
      LexerNewLinesInStringsException
  {
    switch (this.state) {
      case STATE_INITIAL:
      {
        final int c = this.readChar();
        if (c == -1) {
          return new TokenEOF(this.getFile(), this.getPosition());
        }

        if (c == '\n') {
          this.completeNewline();
          return this.token();
        }
        if (c == '\r') {
          this.state = State.STATE_IN_CRLF;
          return this.token();
        }
        if (c == '"') {
          this.startQuotedString();
          return this.token();
        }
        if (c == '(') {
          return new TokenLeftParenthesis(this.getFile(), this.getPosition());
        }
        if (c == ')') {
          return new TokenRightParenthesis(this.getFile(), this.getPosition());
        }
        if (c == '[') {
          if (this.config.allowSquareBrackets()) {
            return new TokenLeftSquare(this.getFile(), this.getPosition());
          }
        }
        if (c == ']') {
          if (this.config.allowSquareBrackets()) {
            return new TokenRightSquare(this.getFile(), this.getPosition());
          }
        }

        if (Character.isSpaceChar(c)) {
          return this.token();
        }

        this.startSymbol(c);
        return this.token();
      }

      case STATE_IN_CRLF:
      {
        final int c = this.readCharNotEOF();

        if (c == '\n') {
          this.completeNewline();
          return this.token();
        }

        throw this.errorBareCarriageReturn();
      }

      case STATE_IN_STRING_QUOTED:
      {
        final int c = this.readCharNotEOF();
        if (c == '\\') {
          this.parseEscape();
          return this.token();
        }
        if ((c == '\r') || (c == '\n')) {
          if (this.config.allowNewlinesInQuotedStrings() == false) {
            throw this.errorNewLinesNotInQuotedStrings();
          }
        }
        if (c == '"') {
          return this.completeQuotedString();
        }

        this.buffer.appendCodePoint(c);
        return this.token();
      }

      case STATE_IN_SYMBOL:
      {
        final int c = this.readChar();
        if (c == -1) {
          return this.completeSymbol();
        }
        if (c == '\n') {
          this.completeNewline();
          return this.completeSymbol();
        }
        if (c == '\r') {
          this.state = State.STATE_IN_CRLF;
          return this.completeSymbol();
        }
        if (c == '"') {
          final TokenType s = this.completeSymbol();
          this.reader.pushCodePoint(c);
          return s;
        }
        if (c == '(') {
          this.reader.pushCodePoint(c);
          return this.completeSymbol();
        }
        if (c == ')') {
          this.reader.pushCodePoint(c);
          return this.completeSymbol();
        }
        if (c == '[') {
          if (this.config.allowSquareBrackets()) {
            this.reader.pushCodePoint(c);
            return this.completeSymbol();
          }
        }
        if (c == ']') {
          if (this.config.allowSquareBrackets()) {
            this.reader.pushCodePoint(c);
            return this.completeSymbol();
          }
        }

        if (Character.isSpaceChar(c)) {
          return this.completeSymbol();
        }

        this.buffer.appendCodePoint(c);
        return this.token();
      }
    }

    throw new UnreachableCodeException();
  }
}
