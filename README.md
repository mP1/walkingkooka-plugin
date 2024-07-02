[![Build Status](https://github.com/mP1/walkingkooka-plugin/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-plugin/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-plugin/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-plugin?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-plugin/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-plugin/alerts/)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

# walkingkooka-plugin
Plugins are components that satisfy an interface and may be retrieved by a `PluginName` from a `PluginProvider`.
The primary goal of a PluginProvider is to JAR FILE supporting hot redeployment mostly controlled by the user via some admin type console or UI.
There are many `Provider(s)` in other repos they defer mostly in that they are always present on the classpath and not dynamically discoverable or provided.