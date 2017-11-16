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

package com.io7m.jsx.cmdline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jlexing.core.LexicalPosition;
import java.util.Objects;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerConfigurationType;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.api.serializer.JSXSerializerType;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.parser.JSXParser;
import com.io7m.jsx.prettyprint.JSXPrettyPrinterCodeStyle;
import com.io7m.jsx.prettyprint.JSXPrettyPrinterMarkupStyle;
import com.io7m.jsx.prettyprint.JSXPrettyPrinterType;
import com.io7m.jsx.serializer.JSXSerializerTrivial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * The main command line program.
 */

public final class Main implements Runnable
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(Main.class);
  }

  private final Map<String, CommandType> commands;
  private final JCommander commander;
  private final String[] args;
  private int exit_code;

  private Main(final String[] in_args)
  {
    this.args = Objects.requireNonNull(in_args, "Command line arguments");

    final CommandRoot r = new CommandRoot();
    final CommandFormat format = new CommandFormat();

    this.commands = new HashMap<>(8);
    this.commands.put("format", format);

    this.commander = new JCommander(r);
    this.commander.setProgramName("jsx");
    this.commander.addCommand("format", format);
  }

  /**
   * The main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(final String[] args)
  {
    final Main cm = new Main(args);
    cm.run();
    System.exit(cm.exitCode());
  }

  /**
   * @return The program exit code
   */

  public int exitCode()
  {
    return this.exit_code;
  }

  @Override
  public void run()
  {
    try {
      this.commander.parse(this.args);

      final String cmd = this.commander.getParsedCommand();
      if (cmd == null) {
        final StringBuilder sb = new StringBuilder(128);
        this.commander.usage(sb);
        LOG.info("Arguments required.\n{}", sb.toString());
        return;
      }

      final CommandType command = this.commands.get(cmd);
      command.call();

    } catch (final ParameterException e) {
      final StringBuilder sb = new StringBuilder(128);
      this.commander.usage(sb);
      LOG.error("{}\n{}", e.getMessage(), sb.toString());
      this.exit_code = 1;
    } catch (final Exception e) {
      LOG.error("{}", e.getMessage(), e);
      this.exit_code = 1;
    }
  }

  private interface CommandType extends Callable<Void>
  {

  }

  private class CommandRoot implements CommandType
  {
    @Parameter(
      names = "-verbose",
      converter = JSXLogLevelConverter.class,
      description = "Set the minimum logging verbosity level")
    private JSXLogLevel verbose = JSXLogLevel.LOG_INFO;

    CommandRoot()
    {

    }

    @Override
    public Void call()
      throws Exception
    {
      final ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
          Logger.ROOT_LOGGER_NAME);
      root.setLevel(this.verbose.toLevel());
      return null;
    }
  }

  @Parameters(commandDescription = "Parse and format an s-expression")
  private final class CommandFormat extends CommandRoot
  {
    @Parameter(
      names = "-file",
      description = "Input file",
      required = true)
    private String file;

    @Parameter(
      names = "-lex-newlines-in-quoted-strings",
      description = "Allow newlines in quoted strings")
    private boolean lex_newlines_quoted = true;

    @Parameter(
      names = "-lex-square-brackets",
      description = "Allow square brackets to denote expressions")
    private boolean lex_square_brackets = true;

    @Parameter(
      names = "-pretty-printer",
      description = "The pretty printer that will be used")
    private JSXPrettyPrinterSelection pretty_printer =
      JSXPrettyPrinterSelection.CODE;

    @Parameter(
      names = "-pretty-printer-width",
      description = "The maximum width for the pretty printer")
    private int pretty_print_width = 80;

    @Parameter(
      names = "-pretty-printer-indent",
      description = "The indentation width for the pretty printer")
    private int pretty_print_indent = 2;

    CommandFormat()
    {

    }

    @Override
    public Void call()
      throws Exception
    {
      super.call();

      final Path path = Paths.get(this.file);
      try (InputStream stream = Files.newInputStream(path)) {
        try (InputStreamReader stream_reader =
               new InputStreamReader(stream, StandardCharsets.UTF_8)) {

          final UnicodeCharacterReaderPushBackType reader =
            UnicodeCharacterReader.newReader(stream_reader);

          final JSXLexerConfiguration.Builder lexer_config_builder =
            JSXLexerConfiguration.builder();

          lexer_config_builder.setFile(path);
          lexer_config_builder.setNewlinesInQuotedStrings(
            this.lex_newlines_quoted);
          lexer_config_builder.setSquareBrackets(
            this.lex_square_brackets);

          final JSXLexerConfigurationType lexer_config =
            lexer_config_builder.build();
          final JSXLexerType lexer =
            JSXLexer.newLexer(lexer_config, reader);

          final JSXParserConfiguration.Builder parser_config_builder =
            JSXParserConfiguration.builder();
          parser_config_builder.setPreserveLexical(true);

          final JSXParserType parser =
            JSXParser.newParser(parser_config_builder.build(), lexer);

          switch (this.pretty_printer) {
            case NONE:
              return this.writeSerializing(
                parser,
                JSXSerializerTrivial.newSerializer());
            case MARKUP:
              return this.writePrettyPrinting(
                parser,
                () -> JSXPrettyPrinterMarkupStyle.newPrinterWithWidthIndent(
                  new OutputStreamWriter(System.out, StandardCharsets.UTF_8),
                  this.pretty_print_width,
                  this.pretty_print_indent));
            case CODE:
              return this.writePrettyPrinting(
                parser,
                () -> JSXPrettyPrinterCodeStyle.newPrinterWithWidthIndent(
                  new OutputStreamWriter(System.out, StandardCharsets.UTF_8),
                  this.pretty_print_width,
                  this.pretty_print_indent));
          }
        }
      }

      return null;
    }

    private Void writeSerializing(
      final JSXParserType parser,
      final JSXSerializerType serial)
      throws IOException
    {
      final Collection<JSXParserException> errors = new ArrayList<>(8);

      while (true) {
        try {
          final Optional<SExpressionType> expr_opt =
            parser.parseExpressionOrEOF();
          if (expr_opt.isPresent()) {
            final SExpressionType expr = expr_opt.get();
            serial.serialize(expr, System.out);
            System.out.println();
            System.out.println();
          } else {
            break;
          }
        } catch (final JSXParserException e) {
          errors.add(e);
        }
      }

      this.showErrors(errors);
      return null;
    }

    private void showErrors(
      final Collection<JSXParserException> errors)
    {
      if (!errors.isEmpty()) {
        for (final JSXParserException e : errors) {
          final LexicalPosition<Path> lex =
            e.getLexicalInformation();
          LOG.error(
            "parse error: {}:{}:{}: {}",
            lex.file().orElse(Paths.get("")),
            Integer.valueOf(lex.line()),
            Integer.valueOf(lex.column()),
            e.getMessage());
        }
        Main.this.exit_code = 1;
      }
    }

    private Void writePrettyPrinting(
      final JSXParserType parser,
      final Supplier<JSXPrettyPrinterType> printers)
      throws IOException
    {
      final Collection<JSXParserException> errors = new ArrayList<>(8);

      while (true) {
        try {
          final Optional<SExpressionType> expr_opt =
            parser.parseExpressionOrEOF();
          if (expr_opt.isPresent()) {
            try (JSXPrettyPrinterType printer = printers.get()) {
              final SExpressionType expr = expr_opt.get();
              printer.print(expr);
            }
            System.out.println();
            System.out.println();
          } else {
            break;
          }
        } catch (final JSXParserException e) {
          errors.add(e);
        }
      }

      this.showErrors(errors);
      return null;
    }
  }
}
