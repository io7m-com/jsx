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

package com.io7m.jsx.lexer;

import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jlexing.core.MutableLexicalPosition;
import com.io7m.jlexing.core.MutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jsx.tokens.TokenEOF;
import com.io7m.jsx.tokens.TokenLeftParenthesis;
import com.io7m.jsx.tokens.TokenLeftSquare;
import com.io7m.jsx.tokens.TokenQuotedString;
import com.io7m.jsx.tokens.TokenRightParenthesis;
import com.io7m.jsx.tokens.TokenRightSquare;
import com.io7m.jsx.tokens.TokenSymbol;
import com.io7m.jsx.tokens.TokenType;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The default implementation of the {@link JSXLexerType} type.
 */

public final class JSXLexer implements JSXLexerType
{
  private final StringBuilder                      buffer;
  private final JSXLexerConfiguration              config;
  private final UnicodeCharacterReaderPushBackType reader;
  private final MutableLexicalPositionType<Path>   position;
  private final MutableLexicalPositionType<Path>   buffer_position;
  private       State                              state;

  private JSXLexer(
    final JSXLexerConfiguration c,
    final UnicodeCharacterReaderPushBackType r)
  {
    this.config = NullCheck.notNull(c);
    this.reader = NullCheck.notNull(r);
    this.state = State.STATE_INITIAL;
    this.buffer = new StringBuilder(256);
    this.position = MutableLexicalPosition.newPosition(0, 0);
    this.buffer_position = MutableLexicalPosition.newPosition(0, 0);

    this.position.setFile(c.getFile());
    this.buffer_position.setFile(c.getFile());
  }

  /**
   * Construct a new lexer.
   *
   * @param c The lexer configuration
   * @param r The unicode character reader
   *
   * @return A new lexer
   */

  public static JSXLexerType newLexer(
    final JSXLexerConfiguration c,
    final UnicodeCharacterReaderPushBackType r)
  {
    return new JSXLexer(c, r);
  }

  private void completeNewline()
  {
    this.state = State.STATE_INITIAL;
    this.position.setLine(this.position.getLine() + 1);
    this.position.setColumn(0);
  }

  private TokenType completeQuotedString()
  {
    this.state = State.STATE_INITIAL;
    final String text = NullCheck.notNull(this.buffer.toString());
    this.buffer.setLength(0);
    return new TokenQuotedString(
      ImmutableLexicalPosition.newFrom(this.buffer_position), text);
  }

  private TokenType completeSymbol()
  {
    this.state = State.STATE_INITIAL;
    final String text = NullCheck.notNull(this.buffer.toString());
    this.buffer.setLength(0);
    return new TokenSymbol(
      ImmutableLexicalPosition.newFrom(this.buffer_position), text);
  }

  private JSXLexerBareCarriageReturnException errorBareCarriageReturn()
  {
    final StringBuilder sb = new StringBuilder(32);
    sb.append("Bare carriage return (U+000D) in source");
    final String s = NullCheck.notNull(sb.toString());
    return new JSXLexerBareCarriageReturnException(
      this.snapshotPosition(), s);
  }

  private JSXLexerInvalidCodePointException errorInvalidCodePoint(
    final long cp)
  {
    final StringBuilder sb = new StringBuilder(32);
    sb.append("Invalid code point given in escape (U+");
    sb.append(Long.toUnsignedString(cp, 16));
    sb.append(")");
    final String s = NullCheck.notNull(sb.toString());
    return new JSXLexerInvalidCodePointException(this.snapshotPosition(), s);
  }

  private JSXLexerNewLinesInStringsException errorNewLinesNotInQuotedStrings()
  {
    return new JSXLexerNewLinesInStringsException(
      this.snapshotPosition(),
      "Lexer configuration does not permit newlines (U+000A or U+000D) in "
      + "quoted strings");
  }

  private JSXLexerNotHexCharException errorNotHexChar(
    final int c)
  {
    final StringBuilder sb = new StringBuilder(16);
    sb.append("Expected a character [0123456789aAbBcCdDeEfF] (got ");
    sb.appendCodePoint(c);
    sb.append(")");
    final String s = NullCheck.notNull(sb.toString());
    return new JSXLexerNotHexCharException(this.snapshotPosition(), s);
  }

  private JSXLexerUnexpectedEOFException errorUnexpectedEOF()
  {
    final StringBuilder sb = new StringBuilder(32);
    sb.append("Unexpected EOF");
    final String s = NullCheck.notNull(sb.toString());
    return new JSXLexerUnexpectedEOFException(this.snapshotPosition(), s);
  }

  private JSXLexerUnknownEscapeCodeException errorUnknownEscape(
    final int c)
  {
    final StringBuilder sb = new StringBuilder(64);
    sb.append("Unknown escape code (");
    sb.appendCodePoint(c);
    sb.append(")");
    final String s = NullCheck.notNull(sb.toString());
    return new JSXLexerUnknownEscapeCodeException(
      this.snapshotPosition(), s);
  }

  private void parseEscape()
    throws JSXLexerException, IOException
  {
    final int c = this.readCharNotEOF();
    if (c == (int) '"') {
      this.buffer.append('"');
      return;
    }
    if (c == (int) '\\') {
      this.buffer.append('\\');
      return;
    }
    if (c == (int) 'r') {
      this.buffer.append('\r');
      return;
    }
    if (c == (int) 'n') {
      this.buffer.append("\n");
      return;
    }
    if (c == (int) 't') {
      this.buffer.append("\t");
      return;
    }
    if (c == (int) 'u') {
      this.parseUnicode4();
      return;
    }
    if (c == (int) 'U') {
      this.parseUnicode8();
      return;
    }

    throw this.errorUnknownEscape(c);
  }

  private void parseUnicode4()
    throws JSXLexerException, IOException
  {
    final StringBuilder hexbuf = new StringBuilder(16);
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    final String hex = NullCheck.notNull(hexbuf.toString());
    final int code = Integer.parseInt(hex, 16);
    this.buffer.appendCodePoint(code);
  }

  private void parseUnicode8()
    throws JSXLexerException, IOException
  {
    final StringBuilder hexbuf = new StringBuilder(16);
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    final String hex = NullCheck.notNull(hexbuf.toString());
    final long code = Long.parseUnsignedLong(hex, 16);
    final int cp = (int) code;

    if (!Character.isValidCodePoint(cp)) {
      throw this.errorInvalidCodePoint(code);
    }

    this.buffer.appendCodePoint(cp);
  }

  private int readChar()
    throws IOException
  {
    final int c = this.reader.readCodePoint();
    if (c != -1) {
      this.position.setColumn(this.position.getColumn() + 1);
    }
    return c;
  }

  private int readCharNotEOF()
    throws IOException, JSXLexerUnexpectedEOFException
  {
    final int c = this.readChar();
    if (c == -1) {
      throw this.errorUnexpectedEOF();
    }
    return c;
  }

  private int readHexCharNotEOF()
    throws JSXLexerException, IOException
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

  private void startQuotedString()
  {
    this.state = State.STATE_IN_STRING_QUOTED;
    this.buffer_position.setColumn(this.position.getColumn());
    this.buffer_position.setLine(this.position.getLine());
    this.buffer.setLength(0);
  }

  private void startSymbol(
    final int c)
  {
    this.state = State.STATE_IN_SYMBOL;
    this.buffer_position.setColumn(this.position.getColumn());
    this.buffer_position.setLine(this.position.getLine());
    this.buffer.setLength(0);
    this.buffer.appendCodePoint(c);
  }

  @Override public TokenType token()
    throws IOException, JSXLexerException
  {
    return this.tokenRead();
  }

  private TokenType tokenRead()
    throws
    IOException,
    JSXLexerException,
    JSXLexerUnexpectedEOFException,
    JSXLexerBareCarriageReturnException,
    JSXLexerNewLinesInStringsException
  {
    while (true) {
      switch (this.state) {
        case STATE_INITIAL: {
          final int c = this.readChar();
          if (c == -1) {
            return new TokenEOF(this.snapshotPosition());
          }

          if (c == (int) '\n') {
            this.completeNewline();
            continue;
          }
          if (c == (int) '\r') {
            this.state = State.STATE_IN_CRLF;
            continue;
          }
          if (c == (int) '"') {
            this.startQuotedString();
            continue;
          }
          if (c == (int) '(') {
            return new TokenLeftParenthesis(this.snapshotPosition());
          }
          if (c == (int) ')') {
            return new TokenRightParenthesis(this.snapshotPosition());
          }
          if (c == (int) '[') {
            if (this.config.allowSquareBrackets()) {
              return new TokenLeftSquare(this.snapshotPosition());
            }
          }
          if (c == (int) ']') {
            if (this.config.allowSquareBrackets()) {
              return new TokenRightSquare(this.snapshotPosition());
            }
          }

          if (Character.isSpaceChar(c)) {
            continue;
          }

          this.startSymbol(c);
          continue;
        }

        case STATE_IN_CRLF: {
          final int c = this.readCharNotEOF();

          if (c == (int) '\n') {
            this.completeNewline();
            continue;
          }

          throw this.errorBareCarriageReturn();
        }

        case STATE_IN_STRING_QUOTED: {
          final int c = this.readCharNotEOF();
          if (c == (int) '\\') {
            this.parseEscape();
            continue;
          }
          if ((c == (int) '\r') || (c == (int) '\n')) {
            if (!this.config.allowNewlinesInQuotedStrings()) {
              throw this.errorNewLinesNotInQuotedStrings();
            }
          }
          if (c == (int) '"') {
            return this.completeQuotedString();
          }

          this.buffer.appendCodePoint(c);
          continue;
        }

        case STATE_IN_SYMBOL: {
          final int c = this.readChar();
          if (c == -1) {
            return this.completeSymbol();
          }
          if (c == (int) '\n') {
            this.completeNewline();
            return this.completeSymbol();
          }
          if (c == (int) '\r') {
            this.state = State.STATE_IN_CRLF;
            return this.completeSymbol();
          }
          if (c == (int) '"') {
            final TokenType s = this.completeSymbol();
            this.reader.pushCodePoint(c);
            return s;
          }
          if (c == (int) '(') {
            this.reader.pushCodePoint(c);
            return this.completeSymbol();
          }
          if (c == (int) ')') {
            this.reader.pushCodePoint(c);
            return this.completeSymbol();
          }
          if (c == (int) '[') {
            if (this.config.allowSquareBrackets()) {
              this.reader.pushCodePoint(c);
              return this.completeSymbol();
            }
          }
          if (c == (int) ']') {
            if (this.config.allowSquareBrackets()) {
              this.reader.pushCodePoint(c);
              return this.completeSymbol();
            }
          }

          if (Character.isSpaceChar(c)) {
            return this.completeSymbol();
          }

          this.buffer.appendCodePoint(c);
        }
      }
    }
  }

  private ImmutableLexicalPositionType<Path> snapshotPosition()
  {
    return ImmutableLexicalPosition.newFrom(this.position);
  }

  private enum State
  {
    STATE_IN_CRLF,
    STATE_IN_STRING_QUOTED,
    STATE_IN_SYMBOL,
    STATE_INITIAL
  }
}
