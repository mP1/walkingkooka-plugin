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

import walkingkooka.InvalidCharacterException;
import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Captures the common members for a plugin INFO.
 * <br>
 * The type parameter N does not extend {@link PluginNameLike} because of {@link walkingkooka.tree.expression.FunctionExpressionName}.
 * <br>
 * Note each {@link PluginInfoLike} must provide a public static parse method which must also be able to parse {@link PluginInfoLike#toString()}.
 */
public interface PluginInfoLike<I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends
        HasName<N>,
        HasAbsoluteUrl,
        Comparable<I>,
        HateosResource<N> {

    /**
     * Useful helper that should be used by {@link PluginInfoLike} implementation parse methods.
     */
    static <I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> I parsePluginInfoLike(final String text,
                                                                                                  final Function<String, N> nameFactory,
                                                                                                  final BiFunction<AbsoluteUrl, N, I> infoFactory) {
        CharSequences.failIfNullOrEmpty(text, "text");
        Objects.requireNonNull(nameFactory, "nameFactory");
        Objects.requireNonNull(infoFactory, "infoFactory");

        final int space = text.indexOf(' ');

        final String urlText = -1 != space ?
                text.substring(0, space) :
                text;
        final AbsoluteUrl url;

        try {
            url = Url.parseAbsolute(urlText);
        } catch (final InvalidCharacterException cause) {
            throw cause.setTextAndPosition(
                    text,
                    cause.position()
            );
        }

        if (-1 == space) {
            throw new InvalidCharacterException(text, space)
                    .appendToMessage(" missing name");
        }

        final String nameText = text.substring(space + 1);

        final N name;
        try {
            name = nameFactory.apply(nameText);
        } catch (final InvalidCharacterException cause) {
            throw cause.setTextAndPosition(
                    text,
                    space + cause.position() +1
            );
        }

        return infoFactory.apply(
                url,
                name
        );
    }

    // Comparable.......................................................................................................

    @Override
    default int compareTo(final I other) {
        return this.name().compareTo(other.name());
    }

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

    // json.............................................................................................................

    /**
     * Marshalls this {@link PluginInfoLike} into a {@link JsonNode}.
     */
    default JsonNode marshall(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return JsonNode.object()
                .set(
                        PluginInfoLikeJsonConstants.URL_PROPERTY,
                        context.marshall(this.url())
                ).set(
                        PluginInfoLikeJsonConstants.NAME_PROPERTY,
                        context.marshall(this.name())
                );
    }

    static <I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> I unmarshall(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context,
                                                                                         final Class<N> nameType,
                                                                                         final BiFunction<AbsoluteUrl, N, I> factory) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(nameType, "nameType");
        Objects.requireNonNull(factory, "factory");

        AbsoluteUrl url = null;
        N name = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName jsonPropertyName = child.name();

            switch (jsonPropertyName.value()) {
                case PluginInfoLikeJsonConstants.URL_PROPERTY_STRING:
                    url = context.unmarshall(
                            child,
                            AbsoluteUrl.class
                    );
                    break;
                case PluginInfoLikeJsonConstants.NAME_PROPERTY_STRING:
                    name = context.unmarshall(
                            child,
                            nameType
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(
                            jsonPropertyName,
                            node
                    );
                    break;
            }
        }

        return factory.apply(
                url,
                name
        );
    }
}
