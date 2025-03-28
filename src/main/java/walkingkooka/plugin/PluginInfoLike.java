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

import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Optional;

/**
 * Captures the common members for a plugin INFO.
 * <br>
 * The type parameter N does not extend {@link PluginNameLike} because of {@link walkingkooka.tree.expression.ExpressionFunctionName}.
 * <br>
 * Note each {@link PluginInfoLike} must provide a public static parse method which must also be able to parse {@link Object#toString()}.
 */
public interface PluginInfoLike<I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends
    HasName<N>,
    HasAbsoluteUrl,
    Comparable<I>,
    HateosResource<N>,
    TreePrintable {

    I setName(final N name);

    // HateosResource...................................................................................................

    @Override
    default String hateosLinkId() {
        return this.name().value();
    }

    @Override
    default Optional<N> id() {
        return Optional.of(
            this.name()
        );
    }

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        printer.println(this.toString());
    }
}
