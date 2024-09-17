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
 * A mapper {@link Provider} that renames any provider infos if the URL matches ignoring all other entries.
 */
public final class RenamingProviderMapper<N extends Name & Comparable<N>,
        PS extends PluginSelectorLike<N>,
        I extends PluginInfoLike<I, N>,
        S extends PluginInfoSetLike<S, I, N>> {

    public static <N extends Name & Comparable<N>,
            PS extends PluginSelectorLike<N>,
            I extends PluginInfoLike<I, N>,
            S extends PluginInfoSetLike<S, I, N>>
    RenamingProviderMapper<N, PS, I, S> with(final S renamingInfos,
                                             final S providerInfos,
                                             final Function<N, RuntimeException> unknown) {
        return new RenamingProviderMapper<>(
                Objects.requireNonNull(renamingInfos, "renamingInfos"),
                Objects.requireNonNull(providerInfos, "providerInfos"),
                Objects.requireNonNull(unknown, "unknown")
        );
    }

    private RenamingProviderMapper(final S renamingInfos,
                                   final S providerInfos,
                                   final Function<N, RuntimeException> unknown) {
        final Map<AbsoluteUrl, I> urlToRenamingInfos = this.urlToInfo(renamingInfos);
        final Map<AbsoluteUrl, I> urlToProviderInfos = this.urlToInfo(providerInfos);

        final Map<N, N> renamingNameToProviderName = Maps.sorted();

        for (final Entry<AbsoluteUrl, I> urlToProviderInfo : urlToProviderInfos.entrySet()) {
            final AbsoluteUrl providerInfoUrl = urlToProviderInfo.getKey();
            final N providerName = urlToProviderInfo.getValue()
                    .name();

            final I renamingInfo = urlToRenamingInfos.get(providerInfoUrl);
            renamingNameToProviderName.put(
                    null != renamingInfo ?
                            renamingInfo.name() :
                            providerName,
                    providerName
            );
        }
        this.renamingNameToProviderName = renamingNameToProviderName;
        this.unknown = unknown;

        final Set<I> infos = Sets.hash();

        for (final I providerInfo : providerInfos) {
            final I renamingInfo = urlToRenamingInfos.get(providerInfo.url());

            infos.add(
                    null != renamingInfo ?
                            renamingInfo :
                    providerInfo
            );
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
     * If the name is missing from the plugin {@link PluginInfoSetLike} an exception will be thrown.
     */
    public N name(final N name) {
        Objects.requireNonNull(name, "name");

        final N nameOut = this.renamingNameToProviderName.get(name);
        if (null == nameOut) {
            throw this.unknown.apply(name);
        }

        return nameOut;
    }

    private final Map<N, N> renamingNameToProviderName;

    private final Function<N, RuntimeException> unknown;

    /**
     * Returns the {@link PluginSelectorLike} after translating it as necessary by matching {@link AbsoluteUrl}.<br>
     * If the name is missing from the plugin {@link PluginInfoSetLike} an exception will be thrown.
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
     * Returns all {@link PluginInfoLike} that exist in the provider {@link PluginInfoSetLike}. If the {@link PluginInfoLike}
     * also exists in the renaming infos that will be returned.
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
