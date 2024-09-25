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

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.StringName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

final class TestPluginInfoSet extends AbstractSet<TestPluginInfo> implements PluginInfoSetLike<TestPluginInfoSet, TestPluginInfo, StringName> {

    public final static TestPluginInfoSet EMPTY = new TestPluginInfoSet(Sets.empty());

    static TestPluginInfoSet parse(final String text) {
        return PluginInfoSetLike.parse(
                text,
                TestPluginInfo::parse,
                TestPluginInfoSet::new
        );
    }

    TestPluginInfoSet(final Set<TestPluginInfo> infos) {
        this.infos = new TreeSet<>(infos);
    }

    @Override
    public Iterator<TestPluginInfo> iterator() {
        return Iterators.readOnly(
                this.infos.iterator()
        );
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    @Override
    public TestPluginInfoSet setElements(final Set<TestPluginInfo> infos) {
        // not worried about taking defensive copy of infos before equals test.
        return this.infos.equals(infos) ?
                this :
                new TestPluginInfoSet(
                        new TreeSet<>(infos)
                );
    }

    @Override
    public Set<TestPluginInfo> toSet() {
        return new TreeSet<>(this.infos);
    }

    private final Set<TestPluginInfo> infos;

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
