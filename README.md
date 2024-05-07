jsx
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jsx/com.io7m.jsx.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jsx%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.jsx/com.io7m.jsx?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/jsx/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/jsx.svg?style=flat-square)](https://codecov.io/gh/io7m-com/jsx)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.jsx](./src/site/resources/jsx.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jsx/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/jsx/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jsx/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/jsx/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jsx/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/jsx/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jsx/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/jsx/actions?query=workflow%3Amain.windows.temurin.lts)|

## jsx

A general-purpose, configurable S-expression parser.

## Features

* Hand-coded lexer and parser with full support for tokens using characters outside of the Unicode BMP.
* Optional square brackets `[f (g [x y])]`.
* Optional multi-line strings.
* Configurable comment characters (`#`, `%`, or `;`).
* Configurable pretty printing of expressions.
* High coverage test suite.
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## Documentation

See the [user manual](https://www.io7m.com/software/jsx).

## Usage

### Parsing

Give the configuration for the lexer:

```
var squareBrackets = true;
var newlinesInQuotedStrings = true;
var startAtLine = 1;

final var lexConfiguration =
  new JSXLexerConfiguration(
    squareBrackets,
    newlinesInQuotedStrings,
    Optional.of(URI.create("file.txt")),
    EnumSet.noneOf(JSXLexerComment.class),
    startAtLine
  );
```

Instantiate a parser using a parser and lexer configuration:

```
var preserveLexicalInfo = true;

final var parserConfig =
  new JSXParserConfiguration(preserveLexicalInfo);

final JSXParserSupplierType parsers =
  new JSXParserSupplier();

final var parser =
  parsers.createFromStreamUTF8(
    parserConfig,
    lexConfiguration,
    lexers,
    System.in
  );
```

Parse expressions:

```
final var exprOpt = parser.parseExpressionOrEOF();
if (exprOpt.isPresent()) {
  final var e = exprOpt.get();
  System.out.println(e);
}
```

