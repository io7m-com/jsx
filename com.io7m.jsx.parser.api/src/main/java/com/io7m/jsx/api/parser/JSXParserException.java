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

package com.io7m.jsx.api.parser;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.jsx.JSXException;

import java.nio.file.Path;

/**
 * The type of exceptions raised by the parser.
 */

public abstract class JSXParserException extends JSXException
{
  private static final long serialVersionUID = -5821503109066196034L;
  private final LexicalPosition<Path> lex;

  /**
   * Construct an exception.
   *
   * @param in_lex     The lexical information
   * @param in_message The exception message
   */

  public JSXParserException(
    final LexicalPosition<Path> in_lex,
    final String in_message)
  {
    super(NullCheck.notNull(in_message, "Message"));
    this.lex = NullCheck.notNull(in_lex, "Lexical information");
  }

  /**
   * Construct an exception.
   *
   * @param in_lex     The lexical information
   * @param in_message The exception message
   * @param in_cause   The cause
   */

  public JSXParserException(
    final LexicalPosition<Path> in_lex,
    final String in_message,
    final Throwable in_cause)
  {
    super(
      NullCheck.notNull(in_message, "Message"),
      NullCheck.notNull(in_cause, "Cause"));
    this.lex = NullCheck.notNull(in_lex, "Lexical information");
  }

  /**
   * @return The lexical information for the exception
   */

  public final LexicalPosition<Path> getLexicalInformation()
  {
    return this.lex;
  }
}
