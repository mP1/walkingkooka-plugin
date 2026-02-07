[![Build Status](https://github.com/mP1/walkingkooka-plugin/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-plugin/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-plugin/badge.svg)](https://coveralls.io/github/mP1/walkingkooka-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-plugin/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-plugin/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-plugin)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

# walkingkooka-plugin

## Plugin
A plugin is a component that satisfies an interface and may be retrieved from a [PluginProvider](https://github.com/mP1/walkingkooka-plugin/blob/master/src/main/java/walkingkooka/plugin/PluginProvider.java).

## [PluginName](https://github.com/mP1/walkingkooka-plugin/blob/master/src/main/java/walkingkooka/plugin/PluginName.java)

Each plugin is identified by a unique plugin name. It may also be useful to include the plugin version following the
plugin name.

## [PluginSelector](https://github.com/mP1/walkingkooka-plugin/blob/master/src/main/java/walkingkooka/plugin/PluginSelector.java)

A selector is a simple text based expression that is intended to select a `Plugin` using its [PluginName](https://github.com/mP1/walkingkooka-plugin/blob/master/src/main/java/walkingkooka/plugin/PluginName.java)
followed by additional text which may encode values or parameters. `PluginProvider(s)` are free to interpret the text in any way.

## [PluginSelector#text](https://github.com/mP1/walkingkooka-plugin/blob/master/src/main/java/walkingkooka/plugin/PluginSelector.java)

Below are a few examples of [SpreadsheetFormatterSelector](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatterSelector.java) which identify a [SpreadsheetFormatter](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatter.java), which each
support formatting cell values after evaluating a cell formula.

- `automatic`
- `date-format-pattern yyyy/mm/dd`
- `date-time-format-pattern yyyy/mm/dd hh:mm:ss`
- `number-format-pattern 0.00`
- `text-format-pattern @`
- `time-format-pattern hh:mm:ss`

### PluginSelector expression language.

A simple mini expression language is also available and supports some simple constructs, such as values as literals and 
environment variables allowing config values.

- [EnvironmentValueName](https://github.com/mP1/walkingkooka-environment/blob/master/src/main/java/walkingkooka/environment/EnvironmentValueName.java), 
  useful to fetch config value etc. `$server-url` 
- `String literals` double quoted string literal values, eg `"Hello123`
- `Double number literals` are numbers or `java.lang.Double` values, eg `2.5`.

## [ClassLoaderPluginProvider](https://github.com/mP1/walkingkooka-plugin/blob/master/src/main/java/walkingkooka/plugin/ClassLoaderPluginProvider.java)

This particular provider supports loading a provider from a JAR file, with the `/META-INF/MANIFEST.MF` holding several entries.

```
MANIFEST-VERSION: 1.0\r\n
plugin-name: Sample-formatter-plugin
plugin-provider-factory-className sample.FormatterProviderFactory.
```