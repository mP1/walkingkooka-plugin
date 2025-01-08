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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginSelectorTokenLikeTesting<T extends PluginSelectorTokenLike<A>, A extends PluginSelectorTokenAlternativeLike> extends HasTextTesting,
    TreePrintableTesting,
    HashCodeEqualsDefinedTesting2<T>,
    JsonNodeMarshallingTesting<T> {

    /**
     * Verify that the public with(String, String, List) is public and static.
     */
    @Test
    default void testPublicStaticWithMethodStringStringList() throws Exception {
        final Class<T> type = this.type();
        final Method method = type.getMethod("with", String.class, String.class, List.class);
        this.checkEquals(
            JavaVisibility.PUBLIC,
            JavaVisibility.of(method),
            method::toGenericString
        );
        this.checkEquals(
            true,
            MethodAttributes.STATIC.is(method),
            method::toGenericString
        );
    }

    String LABEL = "Label123";

    String TEXT = "Text123";

    @Test
    default void testWithNullLabelFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createPluginSelectorTokenLike(
                null,
                TEXT
            )
        );
    }

    @Test
    default void testWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createPluginSelectorTokenLike(
                LABEL,
                null
            )
        );
    }

    @Test
    default void testWithNullAlternativesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createPluginSelectorTokenLike(
                LABEL,
                TEXT,
                null
            )
        );
    }

    @Test
    default void testWith() {
        final List<A> alternatives = this.createPluginSelectorTokenAlternativesLike(1);
        final T component = this.createPluginSelectorTokenLike(
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
        final List<A> alternatives = this.createPluginSelectorTokenAlternativesLike(0);
        final String label = "";
        final String text = "";

        final T component = this.createPluginSelectorTokenLike(
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
            this.createPluginSelectorTokenLike(
                "different " + LABEL,
                TEXT
            )
        );
    }

    @Test
    default void testEqualsDifferentText() {
        this.checkNotEquals(
            this.createPluginSelectorTokenLike(
                LABEL,
                "different " + TEXT
            )
        );
    }

    @Test
    default void testEqualsDifferentAlternatives() {
        this.checkNotEquals(
            this.createPluginSelectorTokenLike(
                LABEL,
                TEXT,
                this.createPluginSelectorTokenAlternativesLike(2)
            )
        );
    }

    @Override
    default T createObject() {
        return this.createPluginSelectorTokenLike(
            LABEL,
            TEXT,
            this.createPluginSelectorTokenAlternativesLike(1)
        );
    }

    default T createPluginSelectorTokenLike(final String label,
                                            final String text) {
        return this.createPluginSelectorTokenLike(
            label,
            text,
            this.createPluginSelectorTokenAlternativesLike(1)
        );
    }

    T createPluginSelectorTokenLike(final String label,
                                    final String text,
                                    final List<A> alternatives);

    default List<A> createPluginSelectorTokenAlternativesLike(final int count) {
        return IntStream.range(1, 1 + count)
            .mapToObj(i -> this.createPluginSelectorTokenAlternativesLike("alternative-label-" + i, "alternative-text-" + i))
            .collect(Collectors.toList());
    }

    A createPluginSelectorTokenAlternativesLike(final String label,
                                                final String text);

    // json.............................................................................................................

    @Test
    default void testJsonMarshall() {
        this.marshallAndCheck(
            this.createPluginSelectorTokenLike(
                LABEL,
                TEXT,
                this.createPluginSelectorTokenAlternativesLike(2)
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
            this.createPluginSelectorTokenLike(
                LABEL,
                TEXT,
                this.createPluginSelectorTokenAlternativesLike(2)
            )
        );
    }

    @Override
    default T createJsonNodeMarshallingValue() {
        return this.createPluginSelectorTokenLike(
            LABEL,
            TEXT
        );
    }
}
