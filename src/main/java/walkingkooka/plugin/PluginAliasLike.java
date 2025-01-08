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

import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.TreePrintable;

import java.util.Optional;

/**
 * Defines the public methods for a plugin alias.
 */
public interface PluginAliasLike<N extends Name & Comparable<N>,
    S extends PluginSelectorLike<N>,
    A extends PluginAliasLike<N, S, A>>
    extends Comparable<A>,
    HasName<N>,
    HasText,
    TreePrintable {

    /**
     * Getter that returns a {@link PluginSelectorLike} if one was present.
     */
    Optional<S> selector();

    /**
     * Getter that returns a {@link AbsoluteUrl} if one was present.
     */
    Optional<AbsoluteUrl> url();
}
