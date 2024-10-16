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

import walkingkooka.naming.Name;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A guard that should be used by all filtering providers to filter/verify any incoming {@link Name} and selectors.
 */
public final class FilteredProviderGuard<N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> {

    public static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<N, I, IS>,
            S extends PluginSelectorLike<N>,
            A extends PluginAliasLike<N, S, A>>
    FilteredProviderGuard<N, S> with(final Set<N> names,
                                     final PluginHelper<N, I, IS, S, A> helper) {
        Objects.requireNonNull(helper, "helper");

        return new FilteredProviderGuard<>(
                helper.names(names),
                helper.unknownName()
        );
    }

    private FilteredProviderGuard(final Set<N> names,
                                  final Function<N, RuntimeException> unknown) {
        this.names = names;
        this.unknown = unknown;
    }

    public N name(final N name) {
        Objects.requireNonNull(name, "name");

        if (false == this.names.contains(name)) {
            throw this.unknown.apply(name);
        }

        return name;
    }

    public S selector(final S selector) {
        Objects.requireNonNull(selector, "selector");

        this.name(selector.name());
        return selector;
    }

    private final Set<N> names;

    private final Function<N, RuntimeException> unknown;

    @Override
    public String toString() {
        return this.names.toString();
    }
}
