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

import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.AbstractSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link Set} that holds {@link PluginInfoLike} and a few related helpers.
 */
public interface PluginInfoSetLike<I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends Set<I>,
        HasText,
        HasUrlFragment,
        TreePrintable {

    /**
     * The character that separates multiple {@link PluginInfoLike}.
     */
    CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    // merge...........................................................................................................

    /**
     * Returns a merge {@link Set INFOs} where the view entries will replace any from target if they have the same URL.
     * This supports environments such as the browser where not all SpreadsheetComparator instances are available,
     * but those that are available could be renamed by SpreadsheetComparatorInfo(s) in the active SpreadsheetMetadata.
     */
    static <I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> Set<I> merge(final Set<I> view,
                                                                                         final Set<I> target) {
        Objects.requireNonNull(view, "view");
        Objects.requireNonNull(target, "target");

        final Set<I> viewCopy = Sets.immutable(view);

        final Set<AbsoluteUrl> viewUrls = viewCopy.stream()
                .map(HasAbsoluteUrl::url)
                .collect(Collectors.toSet());

        final Set<I> all = Sets.sorted();

        for(final I info : target) {
            if(false == viewUrls.contains(info.url())) {
                all.add(info);
            }
        }

        all.addAll(viewCopy);

        return all.equals(viewCopy) ?
                viewCopy :
                Sets.immutable(all);
    }

    /**
     * Computes a mapper {@link Function} merges names from the view and target. Note that targets with the same URL
     * will be replaced and only the view name will work.
     */
    static <I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> Function<N, Optional<N>> nameMapper(final Set<I> view,
                                                                                                                final Set<I> target) {
        Objects.requireNonNull(view, "view");
        Objects.requireNonNull(target, "target");

        final Map<AbsoluteUrl, Name[]> urlToNames = Maps.hash();

        for (final I info : target) {
            final N name = info.name();

            urlToNames.put(
                    info.url(),
                    new Name[]{name, name}
            );
        }

        for (final I info : view) {
            final AbsoluteUrl url = info.url();
            final N name = info.name();

            Name[] names = urlToNames.get(url);
            if (null == names) {
                names = new Name[]{
                        name,
                        name
                };
                urlToNames.put(
                        url,
                        names
                );
            } else {
                names[0] = name;
            }
        }

        final Map<N, N> viewNameToTargetName = Maps.sorted();

        for (final Name[] names : urlToNames.values()) {
            viewNameToTargetName.put(
                    (N) names[0],
                    (N) names[1]
            );
        }

        return n -> Optional.ofNullable(
                viewNameToTargetName.get(n)
        );
    }

    // parse............................................................................................................

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

            final int separator = text.indexOf(
                    SEPARATOR.character(),
                    space
            );

            final int end = -1 == separator ?
                    length :
                    separator;
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

    // HasText..........................................................................................................

    @Override
    default String text() {
        return SEPARATOR.toSeparatedString(
                this,
                Object::toString
        );
    }

    // HasUrlFragment...................................................................................................

    /**
     * Custom UrlFragment support is required otherwise the {@link AbstractSet#toString} will be used when creating a
     * save token which will eventually cause failure when the text is parsed back into the {@link PluginInfoSetLike}.
     */
    @Override
    default UrlFragment urlFragment() {
        return UrlFragment.with(
                this.text()
        );
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
}
