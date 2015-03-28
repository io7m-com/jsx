package com.io7m.jsx.parser;

import java.io.File;
import java.util.AbstractList;
import java.util.List;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jsx.ListType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.lexer.Position;

final class PList extends AbstractList<SExpressionType> implements ListType
{
  private final List<SExpressionType> data;
  private final File                  file;
  private final Position              position;
  private final boolean               square;

  PList(
    final List<SExpressionType> d,
    final Position p,
    final File f,
    final boolean in_square)
  {
    this.data = NullCheck.notNull(d);
    this.position = NullCheck.notNull(p);
    this.file = NullCheck.notNull(f);
    this.square = in_square;
  }

  @Override public void add(
    final int index,
    final @Nullable SExpressionType element)
  {
    this.data.add(index, NullCheck.notNull(element));
  }

  @Override public SExpressionType get(
    final int index)
  {
    return NullCheck.notNull(this.data.get(index));
  }

  @Override public File getFile()
  {
    return this.file;
  }

  @Override public Position getPosition()
  {
    return this.position;
  }

  @Override public boolean isSquare()
  {
    return this.square;
  }

  @Override public <A, E extends Exception> A matchExpression(
    final SExpressionMatcherType<A, E> m)
    throws E
  {
    return m.list(this);
  }

  @Override public SExpressionType remove(
    final int index)
  {
    return NullCheck.notNull(this.data.remove(index));
  }

  @Override public SExpressionType set(
    final int index,
    final @Nullable SExpressionType element)
  {
    return NullCheck
      .notNull(this.data.set(index, NullCheck.notNull(element)));
  }

  @Override public int size()
  {
    return this.data.size();
  }
}
