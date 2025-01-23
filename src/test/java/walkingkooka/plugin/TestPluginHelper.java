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

import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;

final class TestPluginHelper implements PluginHelper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet> {

    final static TestPluginHelper INSTANCE = new TestPluginHelper(CaseSensitivity.INSENSITIVE);


    TestPluginHelper(final CaseSensitivity caseSensitivity) {
        this.caseSensitivity = caseSensitivity;
    }

    @Override
    public StringName name(final String text) {
        return Names.string(text);
    }

    @Override
    public Optional<StringName> parseName(final TextCursor cursor,
                                          final ParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        return Parsers.initialAndPartCharPredicateString(
            (i) -> i >= 'a' && i <= 'z',
            (i) -> i >= 'a' && i <= 'z' || i >= '0' && i <= '9' || i == '-',
            1, // minLength
            32 // maxLength
        ).parse(
            cursor,
            context
        ).map(
            (final ParserToken token) -> this.name(
                token.cast(StringParserToken.class).value()
            )
        );
    }

    @Override
    public Set<StringName> names(final Set<StringName> names) {
        return Sets.immutable(
            Objects.requireNonNull(names, "names")
        );
    }

    @Override
    public Function<StringName, RuntimeException> unknownName() {
        return n -> new UnknownStringNameException("Unknown " + n.getClass().getSimpleName() + " " + n);
    }

    @Override
    public Comparator<StringName> nameComparator() {
        return Name.comparator(caseSensitivity);
    }

    private final CaseSensitivity caseSensitivity;

    @Override
    public TestPluginInfo info(final AbsoluteUrl url,
                               final StringName name) {
        return new TestPluginInfo(url, name);
    }

    @Override
    public TestPluginInfo parseInfo(final String text) {
        return TestPluginInfo.parse(text);
    }

    @Override
    public TestPluginInfoSet infoSet(final Set<TestPluginInfo> infos) {
        return new TestPluginInfoSet(infos);
    }

    @Override
    public TestPluginSelector parseSelector(final String text) {
        return TestPluginSelector.parse(text);
    }

    @Override
    public TestPluginAlias alias(final StringName name,
                                 final Optional<TestPluginSelector> selector,
                                 final Optional<AbsoluteUrl> url) {
        return TestPluginAlias.with(
            name,
            selector,
            url
        );
    }

    @Override
    public TestPluginAlias alias(final PluginAlias<StringName, TestPluginSelector> pluginAlias) {
        return TestPluginAlias.with(pluginAlias);
    }

    @Override
    public TestPluginAliasSet aliasSet(final SortedSet<TestPluginAlias> aliases) {
        return TestPluginAliasSet.with(aliases);
    }

    @Override
    public String label() {
        return StringName.class.getSimpleName();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + this.caseSensitivity;
    }
}
