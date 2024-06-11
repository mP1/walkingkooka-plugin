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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface ProviderTesting<P extends Provider<N, I, IN, OUT>, N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IN, OUT> extends TreePrintableTesting {

    @Test
    default void testGetNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createProvider().get(null)
        );
    }

    default void getAndCheck(final IN input) {
        this.getAndCheck(
                this.createProvider(),
                input
        );
    }

    default void getAndCheck(final P provider,
                             final IN input) {
        this.getAndCheck(
                provider,
                input,
                Optional.empty()
        );
    }

    default void getAndCheck(final IN input,
                             final OUT expected) {
        this.getAndCheck(
                this.createProvider(),
                input,
                expected
        );
    }

    default void getAndCheck(final P provider,
                             final IN input,
                             final OUT expected) {
        this.getAndCheck(
                provider,
                input,
                Optional.of(expected)
        );
    }

    default void getAndCheck(final IN input,
                             final Optional<OUT> expected) {
        this.getAndCheck(
                this.createProvider(),
                input,
                expected
        );
    }

    default void getAndCheck(final P provider,
                             final IN input,
                             final Optional<OUT> expected) {
        this.checkEquals(
                expected,
                provider.get(input),
                () -> provider + " input " + input
        );
    }

    @Test
    default void testInfoReadOnly() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createProvider().infos()
                        .clear()
        );
    }

    default void infosAndCheck(final I... infos) {
        this.infosAndCheck(
                this.createProvider(),
                infos
        );
    }

    default void infosAndCheck(final P provider,
                               final I... infos) {
        this.infosAndCheck(
                provider,
                Sets.of(infos)
        );
    }

    default void infosAndCheck(final Set<I> infos) {
        this.infosAndCheck(
                this.createProvider(),
                infos
        );
    }

    default void infosAndCheck(final P provider,
                               final Set<I> infos) {
        this.checkEquals(
                infos,
                provider.infos(),
                provider::toString
        );
    }

    P createProvider();
}
