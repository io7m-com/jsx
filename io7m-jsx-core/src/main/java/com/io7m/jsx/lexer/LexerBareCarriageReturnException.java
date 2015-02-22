package com.io7m.jsx.lexer;

import java.io.File;

public final class LexerBareCarriageReturnException extends LexerException
{
  private static final long serialVersionUID = -2322358295952666451L;

  public LexerBareCarriageReturnException(
    final Position in_position,
    final File in_file,
    final String in_message)
  {
    super(in_position, in_file, in_message);
  }
}
