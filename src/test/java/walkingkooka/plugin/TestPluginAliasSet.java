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

import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.naming.StringName;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;

final class TestPluginAliasSet extends AbstractSet<TestPluginAlias>
        implements PluginAliasSetLike<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet>,
        ImmutableSortedSetDefaults<TestPluginAliasSet, TestPluginAlias> {

    public static TestPluginAliasSet parse(final String text) {
        return new TestPluginAliasSet(
                PluginAliasSet.parse(
                        text,
                        TestPluginHelper.INSTANCE
                )
        );
    }

    public static TestPluginAliasSet with(final SortedSet<TestPluginAlias> aliases) {
        return new TestPluginAliasSet(
                PluginAliasSet.with(aliases, TestPluginHelper.INSTANCE)
        );
    }

    public TestPluginAliasSet(final PluginAliasSet<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet> pluginAliasSet) {
        this.pluginAliasSet =
                Objects.requireNonNull(pluginAliasSet, "pluginAliasSet");
    }

    @Override
    public Iterator<TestPluginAlias> iterator() {
        return this.pluginAliasSet.iterator();
    }

    @Override
    public int size() {
        return this.pluginAliasSet.size();
    }

    @Override
    public Optional<TestPluginSelector> aliasSelector(final StringName alias) {
        return this.pluginAliasSet.aliasSelector(alias);
    }

    @Override
    public Optional<StringName> aliasOrName(final StringName name) {
        return this.pluginAliasSet.aliasOrName(name);
    }

    @Override
    public TestPluginInfoSet merge(final TestPluginInfoSet providerInfo) {
        return this.pluginAliasSet.merge(providerInfo);
    }

    @Override
    public boolean containsAliasOrName(final StringName nameOrAlias) {
        return this.pluginAliasSet.containsAliasOrName(nameOrAlias);
    }

    public TestPluginAliasSet concatOrReplace(final TestPluginAlias alias) {
        return this.setElements(
                this.pluginAliasSet.concatOrReplace(alias)
        );
    }

    @Override
    public TestPluginAliasSet concat(final TestPluginAlias alias) {
        return this.setElements(
                this.pluginAliasSet.concat(alias)
        );
    }

    @Override
    public TestPluginAliasSet concatAll(final Collection<TestPluginAlias> aliases) {
        return this.setElements(
                this.pluginAliasSet.concatAll(aliases)
        );
    }

    @Override
    public TestPluginAliasSet delete(final TestPluginAlias alias) {
        return this.setElements(
                this.pluginAliasSet.delete(alias)
        );
    }

    @Override
    public TestPluginAliasSet deleteAll(final Collection<TestPluginAlias> aliases) {
        return this.setElements(
                this.pluginAliasSet.deleteAll(aliases)
        );
    }

    @Override
    public TestPluginAliasSet deleteAliasOrNameAll(final Collection<StringName> aliasOrNames) {
        return this.setElements(
                this.pluginAliasSet.deleteAliasOrNameAll(aliasOrNames)
        );
    }

    @Override
    public TestPluginAliasSet keepAliasOrNameAll(final Collection<StringName> aliasOrNames) {
        return this.setElements(
                this.pluginAliasSet.keepAliasOrNameAll(aliasOrNames)
        );
    }

    @Override
    public TestPluginAliasSet replace(final TestPluginAlias oldAlias,
                                      final TestPluginAlias newAlias) {
        return this.setElements(
                this.pluginAliasSet.replace(
                        oldAlias,
                        newAlias
                )
        );
    }

    @Override
    public TestPluginAliasSet setElements(final SortedSet<TestPluginAlias> aliases) {
        // no need to take a defensive copy before testing for equality
        return aliases.equals(this) ?
                this :
                new TestPluginAliasSet(
                        this.pluginAliasSet.setElements(aliases)
                );
    }

    @Override
    public TestPluginAliasSet setElementsFailIfDifferent(final SortedSet<TestPluginAlias> aliases) {
        return new TestPluginAliasSet(
                this.pluginAliasSet.setElementsFailIfDifferent(aliases)
        );
    }

    @Override
    public SortedSet<TestPluginAlias> toSet() {
        return this.pluginAliasSet.toSet();
    }

    @Override
    public Comparator<? super TestPluginAlias> comparator() {
        return this.pluginAliasSet.comparator();
    }

    @Override
    public TestPluginAliasSet subSet(final TestPluginAlias after,
                                     final TestPluginAlias before) {
        return new TestPluginAliasSet(
                this.pluginAliasSet.subSet(
                        after,
                        before
                )
        );
    }

    @Override
    public TestPluginAliasSet headSet(final TestPluginAlias from) {
        return this.setElements(
                this.pluginAliasSet.headSet(from)
        );
    }

    @Override
    public TestPluginAliasSet tailSet(final TestPluginAlias to) {
        return this.setElements(
                this.pluginAliasSet.tailSet(to)
        );
    }

    @Override
    public TestPluginAlias first() {
        return this.pluginAliasSet.first();
    }

    @Override
    public TestPluginAlias last() {
        return this.pluginAliasSet.last();
    }

    @Override
    public String text() {
        return this.pluginAliasSet.text();
    }

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.pluginAliasSet.printTree(printer);
    }

    // @VisibleForTesting
    final PluginAliasSet<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet> pluginAliasSet;
}
