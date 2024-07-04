/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.text.CharSequences;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A helper that may be used to provide a view of a collection of providers. Instances of this class should be wrapped by implementations of the provider interface and simply delegate to the two provider methods.
 */
public final class ProviderCollection<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, P, IN, OUT> {
    public static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, P, IN, OUT> ProviderCollection<N, I, P, IN, OUT> with(final Function<IN, N> inputToName,
                                                                                                                                         final BiFunction<P, IN, Optional<OUT>> providerGetter,
                                                                                                                                         final Function<P, Set<I>> infoGetter,
                                                                                                                                         final String providedLabel,
                                                                                                                                         final Set<P> providers) {
        Objects.requireNonNull(inputToName, "inputToName");
        Objects.requireNonNull(providerGetter, "providerGetter");
        Objects.requireNonNull(infoGetter, "infoGetter");
        CharSequences.failIfNullOrEmpty(providedLabel, "providedLabel");
        Objects.requireNonNull(providers, "providers");

        return new ProviderCollection<>(
                inputToName,
                providerGetter,
                infoGetter,
                providedLabel,
                Sets.immutable(providers)
        );
    }

    private ProviderCollection(final Function<IN, N> inputToName,
                               final BiFunction<P, IN, Optional<OUT>> providerGetter,
                               final Function<P, Set<I>> infoGetter,
                               final String providedLabel,
                               final Set<P> providers) {
        if(providers.isEmpty()) {
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

        // complain if any duplicates in nameToProvider
        final String duplicates = nameToInfos.entrySet()
                .stream()
                .filter(ntp -> ntp.getValue().size() > 1)
                .map(this::nameAndInfosToString)
                .collect(Collectors.joining(", "));
        if (false == duplicates.isEmpty()) {
            throw new IllegalArgumentException("Found multiple " + providedLabel + " for " + duplicates);
        }

        this.inputToName = inputToName;
        this.providerGetter = providerGetter;
        this.nameToProvider = nameToProvider;
        this.infos = Sets.immutable(infos);

        this.providers = providers;
    }

    private String nameAndInfosToString(final Entry<N, Set<I>> nameAndInfos) {
        return nameAndInfos.getKey() +
                nameAndInfos.getValue()
                        .stream()
                        .map(HasAbsoluteUrl::url)
                        .map(Object::toString)
                        .collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * Gets the component identified by IN.
     */
    public Optional<OUT> get(final IN in) {
        Objects.requireNonNull(in, "in");

        // get the provided for the name and then call the provider getter with $IN.
        return Optional.ofNullable(
                this.nameToProvider.get(
                        this.inputToName.apply(in)
                )
        ).flatMap(p -> this.providerGetter.apply(p, in));
    }

    private final Function<IN, N> inputToName;

    private final Map<N, P> nameToProvider;

    private final BiFunction<P, IN, Optional<OUT>> providerGetter;

    /**
     * Returns a {@link Set} with an aggregation of all INFOS from all the provided Providers.
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
