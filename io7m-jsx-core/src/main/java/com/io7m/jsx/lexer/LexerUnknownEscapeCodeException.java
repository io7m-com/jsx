package com.io7m.jsx.lexer;

import java.io.File;

public final class LexerUnknownEscapeCodeException extends LexerException
{
  private static final long serialVersionUID = 7866161529601875642L;

  public LexerUnknownEscapeCodeException(
    final Position in_position,
    final File in_file,
    final String in_message)
  {
    super(in_position, in_file, in_message);
  }
}
