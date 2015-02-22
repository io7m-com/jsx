package com.io7m.jsx.lexer;

import java.io.File;

public final class LexerNewLinesInStringsException extends LexerException
{
  private static final long serialVersionUID = -8611411121946084698L;

  public LexerNewLinesInStringsException(
    final Position in_position,
    final File in_file,
    final String in_message)
  {
    super(in_position, in_file, in_message);
  }
}
