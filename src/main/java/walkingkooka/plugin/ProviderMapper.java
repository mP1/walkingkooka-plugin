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
 * A mapper {@link Provider} that maps and filters {@link Name}, {@link PluginInfoLike} and {@link PluginInfoSetLike}.
 */
public final class ProviderMapper<N extends Name & Comparable<N>, PS extends PluginSelectorLike<N>, I extends PluginInfoLike<I, N>, S extends PluginInfoSetLike<S, I, N>> {
    public static <N extends Name & Comparable<N>, PS extends PluginSelectorLike<N>, I extends PluginInfoLike<I, N>, S extends PluginInfoSetLike<S, I, N>> ProviderMapper<N, PS, I, S> with(final S in,
                                                                                                                                                                                            final S out,
                                                                                                                                                                                            final Function<N, RuntimeException> unknown) {
        return new ProviderMapper<>(
                Objects.requireNonNull(in, "in"),
                Objects.requireNonNull(out, "out"),
                Objects.requireNonNull(unknown, "unknown")
        );
    }

    private ProviderMapper(final S in,
                           final S out,
                           final Function<N, RuntimeException> unknown) {
        final Map<AbsoluteUrl, I> urlToIn = this.urlToInfo(in);
        final Map<AbsoluteUrl, I> urlToOut = this.urlToInfo(out);

        final Map<N, N> inToOut = Maps.sorted();

        for (final Entry<AbsoluteUrl, I> urlAndIn : urlToIn.entrySet()) {
            final AbsoluteUrl inUrl = urlAndIn.getKey();
            final I outInfo = urlToOut.get(inUrl);
            if (null != outInfo) {
                inToOut.put(
                        urlAndIn.getValue()
                                .name(),
                        outInfo.name()
                );
            }
        }
        this.inToOut = inToOut;
        this.unknown = unknown;

        final Set<I> infos = Sets.hash();

        for (final I outInfo : out) {
            final I inInfo = urlToIn.get(outInfo.url());
            if (null != inInfo) {
                infos.add(inInfo);
            }
        }

        this.infos = out.setElements(infos);
    }

    private Map<AbsoluteUrl, I> urlToInfo(final S set) {
        final Map<AbsoluteUrl, I> urlToInfo = Maps.hash();

        for (final I info : set) {
            urlToInfo.put(
                    info.url(),
                    info
            );
        }

        return urlToInfo;
    }

    public N name(final N name) {
        Objects.requireNonNull(name, "name");

        final N out = this.inToOut.get(name);
        if (null == out) {
            throw this.unknown.apply(name);
        }

        return out;
    }

    private final Map<N, N> inToOut;

    private final Function<N, RuntimeException> unknown;

    public PS selector(final PS selector) {
        Objects.requireNonNull(selector, "selector");

        return (PS) selector.setName(
                this.name(
                        selector.name()
                )
        );
    }

    public S infos() {
        return this.infos;
    }

    private final S infos;

    @Override
    public String toString() {
        return this.infos.text();
    }
}
