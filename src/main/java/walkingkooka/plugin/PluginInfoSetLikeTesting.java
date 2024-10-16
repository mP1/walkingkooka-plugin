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
import walkingkooka.collect.set.ImmutableSetTesting;
import walkingkooka.naming.Name;
import walkingkooka.net.http.server.hateos.HateosResourceSetTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasTextTesting;

public interface PluginInfoSetLikeTesting<N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
        extends ImmutableSetTesting<IS, I>,
            HateosResourceSetTesting<IS, I, N>,
            ParseStringTesting<IS>,
            HasTextTesting {

    // empty............................................................................................................

    @Test
    default void testEmpty() throws Exception {
        this.textAndCheck(
                (IS) this.type()
                        .getField("EMPTY")
                        .get(null),
                ""
        );
    }

    @Test
    default void testEmptyConcatText() throws Exception {
        final IS set = (IS) this.type()
                .getField("EMPTY")
                .get(null);

        final I info = this.info();

        this.textAndCheck(
                set.concat(
                        info
                ),
                info.toString()
        );
    }

    I info();

    // parseString......................................................................................................

    @Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    default RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // json.............................................................................................................

    @Override
    default IS createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // hasText..........................................................................................................

    @Test
    default void testText() {
        final IS set = this.createSet();

        this.textAndCheck(
                set,
                CharacterConstant.COMMA.toSeparatedString(
                        set,
                        Object::toString
                )
        );
    }
}
