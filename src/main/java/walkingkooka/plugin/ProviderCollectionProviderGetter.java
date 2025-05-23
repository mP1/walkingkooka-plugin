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

import java.util.List;

public interface ProviderCollectionProviderGetter<P extends Provider, N extends Name & Comparable<N>, S extends PluginSelectorLike<N>, OUT> {

    OUT get(final P provider,
            final N name,
            final List<?> values,
            final ProviderContext context);

    OUT get(final P provider,
            final S selector,
            final ProviderContext context);
}
