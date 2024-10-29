[![Build Status](https://github.com/mP1/walkingkooka-plugin/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-plugin/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-plugin/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-plugin?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-plugin/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-plugin/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-plugin)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

# walkingkooka-plugin
Plugins are components that satisfy an interface and may be retrieved by a `PluginName` from a `PluginProvider`.
The primary goal of a PluginProvider is to JAR FILE supporting hot redeployment mostly controlled by the user via some admin type console or UI.
There are many `Provider(s)` in other repos they defer mostly in that they are always present on the classpath and not dynamically discoverable or provided.

## [PluginSelector](https://github.com/mP1/walkingkooka-plugin/blob/master/src/main/java/walkingkooka/plugin/PluginSelector.java)

A selector is a simple text based expression that is intended to select a plugin-component by name and optional parameters.

An example is that of a [SpreadsheetFormatterSelector](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatterSelector.java) which can be used to select
any available [SpreadsheetFormatter](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatter.java).

- `automatic`
- `date-format-pattern yyyy/mm/dd`
- `date-time-format-pattern yyyy/mm/dd hh:mm:ss`
- `number-format-pattern 0.00`
- `text-format-pattern @`
- `time-format-pattern hh:mm:ss`

Selectors are composed of a mini language which supports the following features with examples.

- $environment-value May be used to retrieve a environment including the enclosing [SpreadsheetMetadata](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadata.java)
- string literals, eg: `"Hello123"`
- double literals, eg: `2.5`
- plugin-component-by-name This is useful for assembling compound components, 
  eg 
  - `color-formatter 1 text-format-pattern("@")`
  - `link-formatter https://example.com text-format-pattern("@")` create a link using the provided url with the value formatted using `text-format pattern "@"`.