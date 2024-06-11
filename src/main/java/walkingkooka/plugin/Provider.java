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

import walkingkooka.naming.Name;

import java.util.Optional;
import java.util.Set;

/**
 * A provider is a container that supports fetching components by some INPUT which may include a name and also provides
 * a {@link Set} of INFO which provide names and {@link walkingkooka.net.AbsoluteUrl} for each component.
 */
public interface Provider<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, P, IN, OUT> {

    /**
     * Getter that fetches a component by the INPUT.
     */
    Optional<OUT> get(final IN in);

    /**
     * Returns the infos for the components belonging to this {@link Provider}.
     */
    Set<I> infos();
}
