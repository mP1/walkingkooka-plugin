/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.plugin;

import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Optional;

public final class TestPluginAlias implements PluginAliasLike<StringName, TestPluginSelector, TestPluginAlias> {

    static TestPluginAlias with(final StringName name,
                                final Optional<TestPluginSelector> selector,
                                final Optional<AbsoluteUrl> url) {
        return with(
                PluginAlias.with(
                        name,
                        selector,
                        url
                )
        );
    }

    static TestPluginAlias with(final PluginAlias<StringName, TestPluginSelector> pluginAlias) {
        return new TestPluginAlias(pluginAlias);
    }

    private TestPluginAlias(final PluginAlias<StringName, TestPluginSelector> pluginAlias) {
        this.pluginAlias = pluginAlias;
    }

    @Override
    public StringName name() {
        return this.pluginAlias.name();
    }

    @Override
    public Optional<TestPluginSelector> selector() {
        return this.pluginAlias.selector();
    }

    @Override
    public Optional<AbsoluteUrl> url() {
        return this.pluginAlias.url();
    }

    @Override
    public int compareTo(final TestPluginAlias other) {
        return this.pluginAlias.compareTo(other.pluginAlias);
    }

    @Override
    public String text() {
        return this.pluginAlias.text();
    }

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.pluginAlias.printTree(printer);
    }

    @Override
    public int hashCode() {
        return this.pluginAlias.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof TestPluginAlias && this.equals0((TestPluginAlias) other);
    }

    private boolean equals0(final TestPluginAlias other) {
        return this.pluginAlias.equals(other.pluginAlias);
    }

    @Override
    public String toString() {
        return this.pluginAlias.toString();
    }

    private final PluginAlias<StringName, TestPluginSelector> pluginAlias;
}
