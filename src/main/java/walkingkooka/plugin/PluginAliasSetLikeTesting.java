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

import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.naming.Name;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

public interface PluginAliasSetLikeTesting<N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<IS, I, N>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A>>
    extends ImmutableSortedSetTesting<AS, A>,
        HasTextTesting,
        TreePrintableTesting,
        ParseStringTesting<AS>,
        ToStringTesting<AS> {

    // ParseString......................................................................................................

    @Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    default RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }
}
