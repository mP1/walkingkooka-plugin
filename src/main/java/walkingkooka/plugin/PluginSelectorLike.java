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
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.TreePrintable;

import java.util.Comparator;
import java.util.List;

/**
 * Interface that should be implemented by all selectors.
 */
public interface PluginSelectorLike<N extends Name & Comparable<N>> extends HasName<N>, HasText,
    HasUrlFragment,
    TreePrintable {

    /**
     * {@see PluginSelectorLikeNameOnlyComparator}
     */
    static <P extends PluginSelectorLike<N>, N extends Name & Comparable<N>> Comparator<P> nameOnlyComparator() {
        return PluginSelectorLikeNameOnlyComparator.instance();
    }

    /**
     * Would be setter that returns a selector with the given {@link Name}.
     */
    PluginSelectorLike<N> setName(final N name);

    /**
     * Returns the values or parameters for this selector as text.
     */
    String valueText();

    /**
     * Would be setter that returns a selector with the given text.
     */
    PluginSelectorLike<N> setValueText(final String text);


    /**
     * Would be setter that accepts values and eventually performs a setText
     */
    PluginSelectorLike<N> setValues(final List<?> values);

    // HasText..........................................................................................................

    @Override
    default String text() {
        return this.toString();
    }

    // HasUrlFragment...................................................................................................

    /**
     * Returns this selector as a {@link UrlFragment}.
     */
    @Override
    default UrlFragment urlFragment() {
        return UrlFragment.with(this.toString());
    }
}
