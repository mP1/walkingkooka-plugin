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
import walkingkooka.text.HasText;
import walkingkooka.text.printer.TreePrintable;

/**
 * Interface that should be implemented by all selectors.
 */
public interface PluginSelectorLike<N extends Name> extends HasName<N>, HasText, TreePrintable {

    /**
     * Would be setter that returns a selector with the given {@link Name}.
     */
    PluginSelectorLike<N> setName(final N name);
}
