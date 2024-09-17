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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A mapper that supports merging a larger {@link PluginInfoSetLike} mappingInfos with a smaller {@link PluginInfoSetLike}
 * the providerInfos. Note that {@link #name(Name)} and {@link #selector(PluginSelectorLike)} will be translated if present
 * in the mapping with a different name from  the provider infos. The {@link #infos()} will contain all the original provider
 * infos with some names updated if they are present in the mapping with a different name.
 */
public final class FilteredProviderMapper<N extends Name & Comparable<N>,
        PS extends PluginSelectorLike<N>,
        I extends PluginInfoLike<I, N>,
        S extends PluginInfoSetLike<S, I, N>> {

    public static <N extends Name & Comparable<N>,
            PS extends PluginSelectorLike<N>,
            I extends PluginInfoLike<I, N>,
            S extends PluginInfoSetLike<S, I, N>>
    FilteredProviderMapper<N, PS, I, S> with(final S mappingInfos,
                                             final S providerInfos,
                                             final Function<N, RuntimeException> unknown) {
        return new FilteredProviderMapper<>(
                Objects.requireNonNull(mappingInfos, "mappingInfos"),
                Objects.requireNonNull(providerInfos, "providerInfos"),
                Objects.requireNonNull(unknown, "unknown")
        );
    }

    private FilteredProviderMapper(final S mappingInfos,
                                   final S providerInfos,
                                   final Function<N, RuntimeException> unknown) {
        final Map<AbsoluteUrl, I> urlToMappingInfos = this.urlToInfo(mappingInfos);
        final Map<AbsoluteUrl, I> urlToProviderInfos = this.urlToInfo(providerInfos);

        final Map<N, N> mappingNameToProviderName = Maps.sorted();

        for (final Entry<AbsoluteUrl, I> urlAndMappingInfo : urlToMappingInfos.entrySet()) {
            final AbsoluteUrl mappingInfoUrl = urlAndMappingInfo.getKey();
            final I providerInfo = urlToProviderInfos.get(mappingInfoUrl);
            if (null != providerInfo) {
                mappingNameToProviderName.put(
                        urlAndMappingInfo.getValue()
                                .name(),
                        providerInfo.name()
                );
            }
        }
        this.mappingNameToProviderName = mappingNameToProviderName;
        this.unknown = unknown;

        final Set<I> infos = Sets.hash();

        for (final I providerInfo : providerInfos) {
            final I mappingInfo = urlToMappingInfos.get(providerInfo.url());
            if (null != mappingInfo) {
                infos.add(mappingInfo);
            }
        }

        this.infos = providerInfos.setElements(infos);
    }

    private Map<AbsoluteUrl, I> urlToInfo(final S infos) {
        final Map<AbsoluteUrl, I> urlToInfo = Maps.hash();

        for (final I info : infos) {
            urlToInfo.put(
                    info.url(),
                    info
            );
        }

        return urlToInfo;
    }

    /**
     * Returns the {@link Name} after translating it as necessary by matching {@link AbsoluteUrl}.<br>
     * If the name is missing from the provider {@link PluginInfoSetLike} an exception will be thrown.
     */
    public N name(final N name) {
        Objects.requireNonNull(name, "name");

        final N nameOut = this.mappingNameToProviderName.get(name);
        if (null == nameOut) {
            throw this.unknown.apply(name);
        }

        return nameOut;
    }

    private final Map<N, N> mappingNameToProviderName;

    private final Function<N, RuntimeException> unknown;

    /**
     * Returns the {@link PluginSelectorLike} after translating it as necessary by matching {@link AbsoluteUrl}.<br>
     * If the name is missing from the provider {@link PluginInfoSetLike} an exception will be thrown.
     */
    public PS selector(final PS selector) {
        Objects.requireNonNull(selector, "selector");

        return (PS) selector.setName(
                this.name(
                        selector.name()
                )
        );
    }

    /**
     * Returns all {@link PluginInfoLike} that exist in both {@link PluginInfoSetLike}.<br>
     * Note the {@link Name} component returned will need translating before using on a {@link Provider}.
     */
    public S infos() {
        return this.infos;
    }

    private final S infos;

    @Override
    public String toString() {
        return this.infos.text();
    }
}
