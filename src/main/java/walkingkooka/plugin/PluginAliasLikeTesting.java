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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.naming.Name;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

public interface PluginAliasLikeTesting<N extends Name & Comparable<N>, S extends PluginSelectorLike<N>, A extends PluginAliasLike<N, S, A>> extends ComparableTesting2<A>,
        HasNameTesting<N>,
        ParseStringTesting<PluginAlias<N, S>>,
        HasTextTesting,
        TreePrintableTesting,
        ToStringTesting<A>,
        ClassTesting<A> {

    // parseString......................................................................................................

    @Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    default RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // toString.........................................................................................................

    @Test
    default void testToString() {
        final A alias = this.createObject();
        this.toStringAndCheck(
                alias,
                alias.text()
        );
    }
}
