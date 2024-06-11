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

import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link Set} that holds {@link PluginInfoLike} and a few related helpers.
 */
public interface PluginInfoSetLike<I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends Set<I>,
        TreePrintable {

    /**
     * Parses some text (actually a csv) holding multiple {@link PluginInfoLike} instances.
     * <pre>
     * https://example.com/service-111 service-111,https://example.com/service-222 service-222
     * </pre>
     */
    static <S extends PluginInfoSetLike<I, N>, I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> S parse(final String text,
                                                                                                                       final Function<String, I> infoParser,
                                                                                                                       final Function<Set<I>, S> setFactory) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(infoParser, "infoParser");

        final int length = text.length();
        int i = 0;
        final Set<I> parsed = Sets.sorted();

        while (i < length) {
            // https://example.com/1 SPACE info COMMA
            final int space = text.indexOf(' ', i);
            if (-1 == space) {
                try {
                    parsed.add(
                            infoParser.apply(text.substring(i))
                    ); // let the parse fail...
                    break;
                } catch (final InvalidCharacterException cause) {
                    throw cause.setTextAndPosition(
                            text,
                            i + 1 + cause.position()
                    );
                }
            }

            final int comma = text.indexOf(',', space);

            final int end = -1 == comma ?
                    length :
                    comma;
            try {
                parsed.add(
                        infoParser.apply(
                                text.substring(
                                        i,
                                        end
                                )
                        )
                );
            } catch (final InvalidCharacterException cause) {
                throw cause.setTextAndPosition(
                        text,
                        i + cause.position()
                );
            }

            i = end + 1;
        }

        return setFactory.apply(parsed);
    }

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            for (final I info : this) {
                TreePrintable.printTreeOrToString(
                        info,
                        printer
                );
                printer.lineStart();
            }
        }
        printer.outdent();
    }

    // toString.........................................................................................................

    /**
     * Helper that produces a CSV toString for any given {@link PluginInfoSetLike} by concatenating all elemenets.
     * This differs from the {@link Set#toString()} as it has no surrounding brackets.
     */
    static String toString(final PluginInfoSetLike<?, ?> set) {
        Objects.requireNonNull(set, "set");

        return set.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }
}
