package com.io7m.jsx.lexer;

import java.io.File;

public final class LexerInvalidCodePointException extends LexerException
{
  private static final long serialVersionUID = -6011440080486643662L;

  public LexerInvalidCodePointException(
    final Position in_position,
    final File in_file,
    final String in_message)
  {
    super(in_position, in_file, in_message);
  }
}
