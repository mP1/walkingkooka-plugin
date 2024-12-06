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

import walkingkooka.plugin.PluginName;
import walkingkooka.predicate.Predicates;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;
import walkingkooka.text.CaseSensitivity;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class TreeMapPluginStore implements PluginStore {

    static TreeMapPluginStore empty() {
        return new TreeMapPluginStore();
    }

    private TreeMapPluginStore() {
        this.store = Stores.treeMap(
                Comparator.naturalOrder(),
                TreeMapPluginStore::idSetter
        );
    }

    private static Plugin idSetter(final PluginName id,
                                   final Plugin plugin) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Plugin> load(final PluginName id) {
        return this.store.load(id);
    }

    @Override
    public Plugin save(final Plugin plugin) {
        return this.store.save(plugin);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<Plugin> watcher) {
        return this.store.addSaveWatcher(watcher);
    }

    @Override
    public void delete(final PluginName id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<PluginName> watcher) {
        return this.store.addDeleteWatcher(watcher);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<PluginName> ids(final int from,
                         final int count) {
        return this.store.ids(
                from,
                count
        );
    }

    @Override
    public List<Plugin> values(final int from,
                               final int count) {
        return this.store.values(
                from,
                count
        );
    }

    @Override
    public List<Plugin> between(final PluginName from,
                                final PluginName to) {
        return this.store.between(
                from,
                to
        );
    }

    @Override
    public List<Plugin> filter(final String query,
                               final int offset,
                               final int count) {
        Objects.requireNonNull(query, "query");
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset " + offset + " < 0");
        }
        if (count < 0) {
            throw new IllegalArgumentException("Invalid count " + count + " < 0");
        }

        final Predicate<CharSequence> globPattern = Predicates.globPatterns(
                query,
                CaseSensitivity.INSENSITIVE
        );

        return this.store.all()
                .stream()
                .filter(p -> globPattern.test(p.name().value()))
                .skip(offset)
                .limit(count)
                .collect(Collectors.toList());
    }

    private final Store<PluginName, Plugin> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
