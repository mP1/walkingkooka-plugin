/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.text.CaseKind;
import walkingkooka.text.CaseSensitivity;

/**
 * Used to tag {@link Name} of components, and also adds a few useful helpers.
 */
public interface PluginNameLike<N extends Name & Comparable<N>> extends Name, Comparable<N> {

    /**
     * Helper that may be used to verify if the given character at the position is valid.
     */
    static boolean isChar(final int pos,
                          final char c) {
        return (0 == pos ?
                PluginName.INITIAL :
                PluginName.PART).test(c);
    }

    /**
     * This helper is useful for names which should follow kebab-case naming standards and need to be formatted as a
     * title case to display in a UI.
     * <pre>
     * day-of-month -> Day of Month
     * </pre>
     */
    default String kebabToTitleCase() {
        return CaseKind.KEBAB.change(
                this.value(),
                CaseKind.TITLE
        );
    }

    // Comparable ......................................................................................................

    @Override
    default int compareTo(final N other) {
        return CASE_SENSITIVITY.comparator()
                .compare(
                        this.value(),
                        other.value()
                );
    }

    // HasCaseSensitivity................................................................................................

    @Override
    default CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;
}
