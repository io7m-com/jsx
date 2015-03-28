package com.io7m.jsx.tests.parser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jfunctional.None;
import com.io7m.jfunctional.Some;
import com.io7m.jsx.ListType;
import com.io7m.jsx.QuotedStringType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SymbolType;
import com.io7m.jsx.lexer.Lexer;
import com.io7m.jsx.lexer.LexerBareCarriageReturnException;
import com.io7m.jsx.lexer.LexerConfiguration;
import com.io7m.jsx.lexer.LexerConfigurationBuilderType;
import com.io7m.jsx.lexer.LexerException;
import com.io7m.jsx.lexer.LexerType;
import com.io7m.jsx.lexer.Position;
import com.io7m.jsx.parser.Parser;
import com.io7m.jsx.parser.ParserConfiguration;
import com.io7m.jsx.parser.ParserConfigurationBuilderType;
import com.io7m.jsx.parser.ParserGrammarException;
import com.io7m.jsx.parser.ParserLexicalException;
import com.io7m.jsx.parser.ParserType;
import com.io7m.jsx.tokens.TokenType;

@SuppressWarnings({ "null", "static-method" }) public final class ParserTest
{
  private static UnicodeCharacterReaderPushBackType stringReader(
    final String s)
  {
    return UnicodeCharacterReader.newReader(new StringReader(s));
  }

  private LexerConfiguration defaultLexerConfig()
  {
    final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
    final LexerConfiguration c = cb.build();
    return c;
  }

  private ParserConfiguration defaultParserConfig()
  {
    final ParserConfigurationBuilderType cb =
      ParserConfiguration.newBuilder();
    final ParserConfiguration c = cb.build();
    return c;
  }

  @Test(expected = ParserGrammarException.class) public void testEOF_0()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(lc, ParserTest.stringReader(""));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = ParserGrammarException.class) public void testEOF_1()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader("(a b "));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test public void testEOF_2()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    final Some<SExpressionType> r0 =
      (Some<SExpressionType>) p.parseExpressionOrEOF();
    final ListType r0l = (ListType) r0.get();
    final None<SExpressionType> r1 =
      (None<SExpressionType>) p.parseExpressionOrEOF();
  }

  @Test(expected = ParserGrammarException.class) public void testEOF_3()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader("[a b "));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = ParserLexicalException.class) public void testLexError_0()
    throws Exception
  {
    final LexerType lex = new LexerType() {
      @Override public TokenType token()
        throws IOException,
          LexerException
      {
        throw new LexerBareCarriageReturnException(Position.ZERO, new File(
          "<stdin>"), "Error!");
      }
    };
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test public void testParseList_0()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);

    final ListType s = (ListType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(1, s.getPosition().getColumn());
    Assert.assertEquals(3, s.size());
    Assert.assertEquals(new File("<stdin>"), s.getFile());

    {
      final SymbolType ss = (SymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      Assert.assertEquals(new File("<stdin>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      Assert.assertEquals(new File("<stdin>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      Assert.assertEquals(new File("<stdin>"), ss.getFile());
    }
  }

  @Test public void testParseListNoLex_0()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final ParserConfigurationBuilderType pcb =
      ParserConfiguration.newBuilder();
    pcb.preserveLexicalInformation(false);
    final ParserConfiguration pc = pcb.build();
    final ParserType p = Parser.newParser(pc, lex);

    final ListType s = (ListType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(0, s.getPosition().getColumn());
    Assert.assertEquals(3, s.size());
    Assert.assertEquals(new File("<none>"), s.getFile());

    {
      final SymbolType ss = (SymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      Assert.assertEquals(new File("<none>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      Assert.assertEquals(new File("<none>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      Assert.assertEquals(new File("<none>"), ss.getFile());
    }
  }

  @Test public void testParseListSquareNoLex_1()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader("[a b c]"));
    final ParserConfigurationBuilderType pcb =
      ParserConfiguration.newBuilder();
    pcb.preserveLexicalInformation(false);
    final ParserConfiguration pc = pcb.build();
    final ParserType p = Parser.newParser(pc, lex);

    final ListType s = (ListType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(0, s.getPosition().getColumn());
    Assert.assertEquals(3, s.size());
    Assert.assertEquals(new File("<none>"), s.getFile());

    {
      final SymbolType ss = (SymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      Assert.assertEquals(new File("<none>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      Assert.assertEquals(new File("<none>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      Assert.assertEquals(new File("<none>"), ss.getFile());
    }
  }

  @Test public void testParseSquareList_0()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader("[a b c]"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);

    final ListType s = (ListType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(1, s.getPosition().getColumn());
    Assert.assertEquals(3, s.size());
    Assert.assertEquals(new File("<stdin>"), s.getFile());

    {
      final SymbolType ss = (SymbolType) s.get(0);
      Assert.assertEquals("a", ss.getText());
      Assert.assertEquals(new File("<stdin>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(1);
      Assert.assertEquals("b", ss.getText());
      Assert.assertEquals(new File("<stdin>"), ss.getFile());
    }

    {
      final SymbolType ss = (SymbolType) s.get(2);
      Assert.assertEquals("c", ss.getText());
      Assert.assertEquals(new File("<stdin>"), ss.getFile());
    }
  }

  @Test public void testParseSymbol_0()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(lc, ParserTest.stringReader("a"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);

    final SymbolType s = (SymbolType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(1, s.getPosition().getColumn());
    Assert.assertEquals("a", s.getText());
    Assert.assertEquals(new File("<stdin>"), s.getFile());
  }

  @Test public void testParseSymbolNoLex_0()
    throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(lc, ParserTest.stringReader("a"));
    final ParserConfigurationBuilderType pcb =
      ParserConfiguration.newBuilder();
    pcb.preserveLexicalInformation(false);
    final ParserConfiguration pc = pcb.build();
    final ParserType p = Parser.newParser(pc, lex);

    final SymbolType s = (SymbolType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(0, s.getPosition().getColumn());
    Assert.assertEquals("a", s.getText());
    Assert.assertEquals(new File("<none>"), s.getFile());
  }

  @Test public void testQuotedString_0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader(sb.toString()));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);

    final QuotedStringType s = (QuotedStringType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(1, s.getPosition().getColumn());
    Assert.assertEquals("a", s.getText());
    Assert.assertEquals(new File("<stdin>"), s.getFile());
  }

  @Test public void testQuotedStringNoLex_0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex =
      Lexer.newLexer(lc, ParserTest.stringReader(sb.toString()));

    final ParserConfigurationBuilderType pcb =
      ParserConfiguration.newBuilder();
    pcb.preserveLexicalInformation(false);
    final ParserConfiguration pc = pcb.build();
    final ParserType p = Parser.newParser(pc, lex);

    final QuotedStringType s = (QuotedStringType) p.parseExpression();
    Assert.assertEquals(0, s.getPosition().getLine());
    Assert.assertEquals(0, s.getPosition().getColumn());
    Assert.assertEquals("a", s.getText());
    Assert.assertEquals(new File("<none>"), s.getFile());
  }

  @Test(expected = ParserGrammarException.class) public
    void
    testUnbalancedRoundSquare_0()
      throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(lc, ParserTest.stringReader("(]"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = ParserGrammarException.class) public
    void
    testUnbalancedRoundSquare_1()
      throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(lc, ParserTest.stringReader("[)"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = ParserGrammarException.class) public
    void
    testUnexpectedRight_0()
      throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(lc, ParserTest.stringReader(")"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = ParserGrammarException.class) public
    void
    testUnexpectedRightSquare_0()
      throws Exception
  {
    final LexerConfiguration lc = this.defaultLexerConfig();
    final LexerType lex = Lexer.newLexer(lc, ParserTest.stringReader("]"));
    final ParserConfiguration pc = this.defaultParserConfig();
    final ParserType p = Parser.newParser(pc, lex);
    p.parseExpression();
  }
}
