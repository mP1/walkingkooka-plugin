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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KSELECTORD, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.plugin;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A helper that may be used to provide a view of a collection of providers.
 * Instances of this class should be wrapped by implementations of the provider interface and simply delegate to the two provider methods.
 * If a {@link Name} appears twice then both will NOT be available when fetched by the getter method.
 */
public final class ProviderCollection<P extends Provider, N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, SELECTOR extends PluginSelectorLike<N>, OUT> {
    public static <P extends Provider,
            N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            SELECTOR extends PluginSelectorLike<N>,
            OUT> ProviderCollection<P, N, I, SELECTOR, OUT> with(final ProviderCollectionProviderGetter<P, N, SELECTOR, OUT> providerGetter,
                                                                 final Function<P, Set<I>> infoGetter,
                                                                 final String providedLabel,
                                                                 final Set<P> providers) {
        Objects.requireNonNull(providerGetter, "providerGetter");
        Objects.requireNonNull(infoGetter, "infoGetter");
        CharSequences.failIfNullOrEmpty(providedLabel, "providedLabel");
        Objects.requireNonNull(providers, "providers");

        return new ProviderCollection<>(
                providerGetter,
                infoGetter,
                providedLabel,
                Sets.immutable(providers)
        );
    }

    private ProviderCollection(final ProviderCollectionProviderGetter<P, N, SELECTOR, OUT> providerGetter,
                               final Function<P, Set<I>> infoGetter,
                               final String providedLabel,
                               final Set<P> providers) {
        if (providers.isEmpty()) {
            throw new IllegalArgumentException("Empty " + providedLabel + " providers");
        }
        final Set<I> infos = Sets.sorted();
        final Map<N, P> nameToProvider = Maps.sorted();
        final Map<N, Set<I>> nameToInfos = Maps.sorted(); // used to detect duplicates.

        for (final P provider : providers) {
            for (final I info : infoGetter.apply(provider)) {
                final N name = info.name();

                Set<I> infosForName = nameToInfos.get(name);
                if (null == infosForName) {
                    infosForName = Sets.hash();
                    nameToInfos.put(
                            name,
                            infosForName
                    );
                }
                infosForName.add(info);

                nameToProvider.put(
                        name,
                        provider
                );

                infos.add(info);
            }
        }

        // remove duplicates
        for(final Entry<N, Set<I>> nameAndInfos : nameToInfos.entrySet()) {
            final Set<I> info = nameAndInfos.getValue();
            if(info.size() > 1) {
                nameToProvider.remove(
                        nameAndInfos.getKey()
                );
            }
        }

        this.providerGetter = providerGetter;
        this.nameToProvider = nameToProvider;
        this.infos = Sets.immutable(infos);

        this.providers = providers;
    }

    /**
     * Gets the component identified by {@link SELECTOR} with the given parameter values.
     */
    public OUT get(final SELECTOR selector,
                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.providerGetter.get(
                        this.provider(selector.name()),
                        selector,
                        context
                );
    }

    /**
     * Gets the component identified by SELECTOR.
     */
    public OUT get(final N name,
                   final List<?> values,
                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(context, "context");

        return this.providerGetter.get(
                        this.provider(name),
                        name,
                        values,
                        context
        );
    }

    private P provider(final N name) {
        final P provider = this.nameToProvider.get(name);
        if(null == provider) {
            throw new IllegalArgumentException("Unknown " + name);
        }
        return provider;
    }

    private final Map<N, P> nameToProvider;

    private final ProviderCollectionProviderGetter<P, N, SELECTOR, OUT> providerGetter;

    /**
     * Returns a {@link Set} with an aggregation of all SELECTORFOS from all the provided Providers.
     */
    public Set<I> infos() {
        return this.infos;
    }

    private Set<I> infos;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.providers.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof ProviderCollection && this.equals0((ProviderCollection<?, ?, ?, ?, ?>) other);
    }

    private boolean equals0(final ProviderCollection<?, ?, ?, ?, ?> other) {
        return this.providers.equals(other.providers);
    }

    @Override
    public String toString() {
        return this.providers.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private final Set<P> providers;
}
