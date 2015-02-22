package com.io7m.jsx.lexer;

import java.io.File;

public final class LexerNotHexCharException extends LexerException
{
  private static final long serialVersionUID = 1211949039006857988L;

  public LexerNotHexCharException(
    final Position in_position,
    final File in_file,
    final String in_message)
  {
    super(in_position, in_file, in_message);
  }
}
