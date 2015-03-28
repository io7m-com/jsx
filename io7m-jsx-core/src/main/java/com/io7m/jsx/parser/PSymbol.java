package com.io7m.jsx.parser;

import java.io.File;

import com.io7m.jnull.NullCheck;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SymbolType;
import com.io7m.jsx.lexer.Position;

final class PSymbol implements SymbolType
{
  private final File     file;
  private final Position position;
  private final String   text;

  PSymbol(
    final String t,
    final Position p,
    final File f)
  {
    this.text = NullCheck.notNull(t);
    this.position = NullCheck.notNull(p);
    this.file = NullCheck.notNull(f);
  }

  @Override public File getFile()
  {
    return this.file;
  }

  @Override public Position getPosition()
  {
    return this.position;
  }

  @Override public String getText()
  {
    return this.text;
  }

  @Override public <A, E extends Exception> A matchExpression(
    final SExpressionMatcherType<A, E> m)
    throws E
  {
    return m.symbol(this);
  }
}