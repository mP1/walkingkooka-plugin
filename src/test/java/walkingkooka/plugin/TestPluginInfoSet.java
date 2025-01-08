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

import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

final class TestPluginInfoSet extends AbstractSet<TestPluginInfo> implements PluginInfoSetLike<StringName,
    TestPluginInfo,
    TestPluginInfoSet,
    TestPluginSelector,
    TestPluginAlias,
    TestPluginAliasSet> {

    public final static TestPluginInfoSet EMPTY = new TestPluginInfoSet(Sets.empty());

    static TestPluginInfoSet parse(final String text) {
        return new TestPluginInfoSet(
            PluginInfoSet.parse(
                text,
                TestPluginInfo::parse
            )
        );
    }

    TestPluginInfoSet(final Set<TestPluginInfo> pluginInfoSet) {
        this.pluginInfoSet = PluginInfoSet.with(pluginInfoSet);
    }

    // PluginInfoSetLike................................................................................................

    @Override
    public Set<StringName> names() {
        return this.pluginInfoSet.names();
    }

    @Override
    public Set<AbsoluteUrl> url() {
        return this.pluginInfoSet.url();
    }

    @Override
    public TestPluginInfoSet filter(final TestPluginInfoSet infos) {
        return this.setElements(
            this.pluginInfoSet.filter(
                infos.pluginInfoSet
            )
        );
    }

    @Override
    public TestPluginInfoSet renameIfPresent(TestPluginInfoSet renameInfos) {
        return this.setElements(
            this.pluginInfoSet.renameIfPresent(
                renameInfos.pluginInfoSet
            )
        );
    }

    @Override
    public TestPluginAliasSet aliasSet() {
        return TestPluginHelper.INSTANCE.toAliasSet(this);
    }

    @Override
    public TestPluginInfoSet concat(final TestPluginInfo info) {
        return this.setElements(
            this.pluginInfoSet.concat(info)
        );
    }

    @Override
    public TestPluginInfoSet concatAll(final Collection<TestPluginInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.concatAll(infos)
        );
    }

    @Override
    public TestPluginInfoSet delete(final TestPluginInfo info) {
        return this.setElements(
            this.pluginInfoSet.delete(info)
        );
    }

    @Override
    public TestPluginInfoSet deleteAll(final Collection<TestPluginInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.deleteAll(infos)
        );
    }

    @Override
    public TestPluginInfoSet deleteIf(final Predicate<? super TestPluginInfo> predicate) {
        return this.setElements(
            this.pluginInfoSet.deleteIf(predicate)
        );
    }

    @Override
    public TestPluginInfoSet replace(final TestPluginInfo oldInfo,
                                     final TestPluginInfo newInfo) {
        return this.setElements(
            this.pluginInfoSet.replace(
                oldInfo,
                newInfo
            )
        );
    }

    @Override
    public ImmutableSet<TestPluginInfo> setElementsFailIfDifferent(final Set<TestPluginInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.setElementsFailIfDifferent(
                infos
            )
        );
    }

    @Override
    public TestPluginInfoSet setElements(final Set<TestPluginInfo> infos) {
        final TestPluginInfoSet after = new TestPluginInfoSet(
            this.pluginInfoSet.setElements(infos)
        );
        return this.pluginInfoSet.equals(infos) ?
            this :
            after;
    }

    @Override
    public Set<TestPluginInfo> toSet() {
        return this.pluginInfoSet.toSet();
    }

    // TreePrintable....................................................................................................

    @Override
    public String text() {
        return this.pluginInfoSet.text();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            this.pluginInfoSet.printTree(printer);
        }
        printer.outdent();
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<TestPluginInfo> iterator() {
        return this.pluginInfoSet.iterator();
    }

    @Override
    public int size() {
        return this.pluginInfoSet.size();
    }

    private final PluginInfoSet<StringName, TestPluginInfo> pluginInfoSet;

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static TestPluginInfoSet unmarshall(final JsonNode node,
                                        final JsonNodeUnmarshallContext context) {
        return new TestPluginInfoSet(
            context.unmarshallSet(
                node,
                TestPluginInfo.class
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(TestPluginInfoSet.class),
            TestPluginInfoSet::unmarshall,
            TestPluginInfoSet::marshall,
            TestPluginInfoSet.class
        );
    }
}
