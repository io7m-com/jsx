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

import java.io.IOException;
import java.io.InputStreamReader;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.lexer.Lexer;
import com.io7m.jsx.lexer.LexerConfiguration;
import com.io7m.jsx.lexer.LexerConfigurationBuilderType;
import com.io7m.jsx.lexer.LexerType;
import com.io7m.jsx.serializer.SerializerTrivial;
import com.io7m.jsx.serializer.SerializerType;

/**
 * Simple parser demo that parses and then serializes.
 */

public final class ParserDemo
{
  /**
   * Main program.
   *
   * @param args
   *          Command line arguments
   * @throws IOException
   *           On I/O errors
   */

  public static void main(
    final String[] args)
    throws IOException
  {
    try {
      final LexerConfigurationBuilderType lcb =
        LexerConfiguration.newBuilder();
      lcb.setNewlinesInQuotedStrings(false);
      final LexerConfiguration lc = lcb.build();

      final UnicodeCharacterReaderPushBackType r =
        UnicodeCharacterReader.newReader(new InputStreamReader(System.in));
      final LexerType lex = Lexer.newLexer(lc, r);

      final ParserConfigurationBuilderType pcb =
        ParserConfiguration.newBuilder();
      pcb.preserveLexicalInformation(true);
      final ParserConfiguration pc = pcb.build();
      final ParserType p = Parser.newParser(pc, lex);

      final SerializerType s = SerializerTrivial.newSerializer();

      for (;;) {
        final SExpressionType e = p.parseExpression();
        s.serialize(e, System.out);
      }
    } catch (final ParserException e) {
      System.err.println("error: parse error: "
        + e.getFile()
        + ":"
        + e.getPosition()
        + ": "
        + e.getMessage());
    }
  }
}
