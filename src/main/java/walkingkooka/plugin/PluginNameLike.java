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

import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.list.Lists;
import walkingkooka.naming.Name;
import walkingkooka.text.CaseSensitivity;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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

    // parse............................................................................................................

    /**
     * Parses some text (actually a csv) holding multiple {@link PluginNameLike} instances.
     * <pre>
     * SPACE*
     * NAME
     * SPACE*
     * SEPARATOR-COMMA
     * </pre>
     *
     * <pre>
     * name1,name2
     * name1, name2
     * name1 , name2
     * </pre>
     */
    static <L extends ImmutableList<N>, N extends Name & Comparable<N>> L parse(final String text,
                                                                                final Function<String, N> nameParser,
                                                                                final Function<List<N>, L> listFactory) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(nameParser, "parseName");

        final List<N> names = Lists.array();

        final PluginNameLikeParser<N> parser = PluginNameLikeParser.with(
                text,
                nameParser
        );

        parser.spaces();

        if (parser.isNotEmpty()) {
            for (; ; ) {
                parser.spaces();

                names.add(parser.name());

                parser.spaces();

                if (PluginInfoSetLike.SEPARATOR.string().equals(parser.comma())) {
                    continue;
                }

                if (parser.isEmpty()) {
                    break;
                }

                parser.invalidCharacterException();
            }
        }

        return listFactory.apply(names);
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
