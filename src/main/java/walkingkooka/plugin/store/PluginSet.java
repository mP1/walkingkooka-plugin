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

package walkingkooka.plugin.store;

import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.plugin.PluginName;
import walkingkooka.plugin.PluginNameSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class PluginSet extends AbstractSet<Plugin> implements ImmutableSortedSetDefaults<PluginSet, Plugin> {

    /**
     * Empty {@link PluginSet}.
     */
    public final static PluginSet EMPTY = new PluginSet(SortedSets.empty());

    public static PluginSet with(final SortedSet<Plugin> set) {
        return withCopy(
                SortedSets.immutable(
                        Objects.requireNonNull(set, "set")
                )
        );
    }

    private static PluginSet withCopy(final SortedSet<Plugin> set) {
        return set.isEmpty() ?
                EMPTY :
                new PluginSet(set);
    }

    private PluginSet(final SortedSet<Plugin> set) {
        this.set = set;
    }

    @Override
    public Iterator<Plugin> iterator() {
        return this.set.iterator();
    }

    @Override
    public int size() {
        return this.set.size();
    }

    @Override
    public PluginSet setElements(final SortedSet<Plugin> set) {
        final ImmutableSortedSet<Plugin> copy = SortedSets.immutable(
                Objects.requireNonNull(set, "set")
        );
        return this.equals(copy) ?
                this :
                withCopy(copy);
    }

    @Override
    public SortedSet<Plugin> toSet() {
        return new TreeSet<>(this.set);
    }

    @Override
    public Comparator<? super Plugin> comparator() {
        return this.set.comparator();
    }

    @Override
    public PluginSet subSet(final Plugin after,
                            final Plugin before) {
        return withCopy(
                this.set.subSet(
                        after,
                        before
                )
        );
    }

    @Override
    public PluginSet headSet(final Plugin from) {
        return this.setElements(
                this.set.headSet(from)
        );
    }

    @Override
    public PluginSet tailSet(final Plugin to) {
        return this.setElements(
                this.set.tailSet(to)
        );
    }

    @Override
    public Plugin first() {
        return this.set.first();
    }

    @Override
    public Plugin last() {
        return this.set.last();
    }

    private final SortedSet<Plugin> set;

    // extra............................................................................................................

    /**
     * Returns a {@link Set} containing all the {@link PluginName names}.
     */
    public PluginNameSet names() {
        return PluginNameSet.with(
                this.set.stream()
                        .map(Plugin::name)
                        .collect(Collectors.toCollection(SortedSets::tree))
        );
    }

    // Json.............................................................................................................

    static PluginSet unmarshall(final JsonNode node,
                                final JsonNodeUnmarshallContext context) {
        return with(
                new TreeSet<>(
                        context.unmarshallSet(
                                node,
                                Plugin.class
                        )
                )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(PluginSet.class),
                PluginSet::unmarshall,
                PluginSet::marshall,
                PluginSet.class
        );
    }
}
