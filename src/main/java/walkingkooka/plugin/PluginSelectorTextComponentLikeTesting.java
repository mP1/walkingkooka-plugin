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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginSelectorTextComponentLikeTesting<T extends PluginSelectorTextComponentLike<A>, A extends PluginSelectorTextComponentAlternativeLike> extends HasTextTesting,
        TreePrintableTesting,
        HashCodeEqualsDefinedTesting2<T>,
        JsonNodeMarshallingTesting<T> {

    String LABEL = "Label123";

    String TEXT = "Text123";

    @Test
    default void testWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorTextComponentLike(
                        null,
                        TEXT
                )
        );
    }

    @Test
    default void testWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorTextComponentLike(
                        LABEL,
                        null
                )
        );
    }

    @Test
    default void testWithNullAlternativesFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorTextComponentLike(
                        LABEL,
                        TEXT,
                        null
                )
        );
    }

    @Test
    default void testWith() {
        final List<A> alternatives = this.createPluginSelectorTextComponentAlternativesLike(1);
        final T component = this.createPluginSelectorTextComponentLike(
                LABEL,
                TEXT,
                alternatives
        );
        this.checkEquals(
                LABEL,
                component.label(),
                "label"
        );
        this.textAndCheck(
                component,
                TEXT
        );
        this.checkEquals(
                alternatives,
                component.alternatives(),
                "alternatives"
        );
    }

    @Test
    default void testWithEmptyLabelEmptyTextEmptyAlternatives() {
        final List<A> alternatives = this.createPluginSelectorTextComponentAlternativesLike(0);
        final String label = "";
        final String text = "";

        final T component = this.createPluginSelectorTextComponentLike(
                label,
                text,
                alternatives
        );
        this.checkEquals(
                label,
                component.label(),
                "label"
        );
        this.textAndCheck(
                component,
                text
        );
        this.checkEquals(
                alternatives,
                component.alternatives(),
                "alternatives"
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    default void testEqualsDifferentLabel() {
        this.checkNotEquals(
                this.createPluginSelectorTextComponentLike(
                        "different " + LABEL,
                        TEXT
                )
        );
    }

    @Test
    default void testEqualsDifferentText() {
        this.checkNotEquals(
                this.createPluginSelectorTextComponentLike(
                        LABEL,
                        "different " + TEXT
                )
        );
    }

    @Test
    default void testEqualsDifferentAlternatives() {
        this.checkNotEquals(
                this.createPluginSelectorTextComponentLike(
                        LABEL,
                        TEXT,
                        this.createPluginSelectorTextComponentAlternativesLike(2)
                )
        );
    }

    @Override
    default T createObject() {
        return this.createPluginSelectorTextComponentLike(
                LABEL,
                TEXT,
                this.createPluginSelectorTextComponentAlternativesLike(1)
        );
    }

    default T createPluginSelectorTextComponentLike(final String label,
                                                    final String text) {
        return this.createPluginSelectorTextComponentLike(
                label,
                text,
                this.createPluginSelectorTextComponentAlternativesLike(1)
        );
    }

    T createPluginSelectorTextComponentLike(final String label,
                                            final String text,
                                            final List<A> alternatives);

    default List<A> createPluginSelectorTextComponentAlternativesLike(final int count) {
        return IntStream.range(1, 1 + count)
                .mapToObj(i -> this.createPluginSelectorTextComponentAlternativesLike("alternative-label-" + i, "alternative-text-" + i))
                .collect(Collectors.toList());
    }

    A createPluginSelectorTextComponentAlternativesLike(final String label,
                                                        final String text);

    // json.............................................................................................................

    @Test
    default void testJsonMarshall() {
        this.marshallAndCheck(
                this.createPluginSelectorTextComponentLike(
                        LABEL,
                        TEXT,
                        this.createPluginSelectorTextComponentAlternativesLike(2)
                ),
                "{\n" +
                        "  \"label\": \"Label123\",\n" +
                        "  \"text\": \"Text123\",\n" +
                        "  \"alternatives\": [\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-1\",\n" +
                        "      \"text\": \"alternative-text-1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-2\",\n" +
                        "      \"text\": \"alternative-text-2\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"
        );
    }

    @Test
    default void testJsonUnmarshall() {
        this.unmarshallAndCheck(
                "{\n" +
                        "  \"label\": \"Label123\",\n" +
                        "  \"text\": \"Text123\",\n" +
                        "  \"alternatives\": [\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-1\",\n" +
                        "      \"text\": \"alternative-text-1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-2\",\n" +
                        "      \"text\": \"alternative-text-2\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}",
                this.createPluginSelectorTextComponentLike(
                        LABEL,
                        TEXT,
                        this.createPluginSelectorTextComponentAlternativesLike(2)
                )
        );
    }

    @Override
    default T createJsonNodeMarshallingValue() {
        return this.createPluginSelectorTextComponentLike(
                LABEL,
                TEXT
        );
    }
}
