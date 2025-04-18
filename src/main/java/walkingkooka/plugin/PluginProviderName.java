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

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * The {@link Name} of a {@link PluginProvider}. Note names are case-sensitive.
 */
final public class PluginProviderName implements Name, Comparable<PluginProviderName> {

    final static CharPredicate INITIAL = CharPredicates.range('A', 'Z')
        .or(CharPredicates.range('a', 'z'));

    final static CharPredicate PART = INITIAL.or(CharPredicates.range('0', '9'))
        .or(CharPredicates.is('-'));

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link PluginProviderName}
     */
    public static PluginProviderName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
            name,
            PluginProviderName.class.getSimpleName(),
            INITIAL,
            PART
        );

        final int length = name.length();
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException("Name length " + length + " > " + MAX_LENGTH);
        }

        return new PluginProviderName(name);
    }

    /**
     * Private constructor
     */
    private PluginProviderName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    // Object..................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof PluginProviderName &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginProviderName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final PluginProviderName other) {
        return CASE_SENSITIVITY.comparator()
            .compare(
                this.name,
                other.name
            );
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    // Json.............................................................................................................

    static PluginProviderName unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(PluginProviderName.class),
            PluginProviderName::unmarshall,
            PluginProviderName::marshall,
            PluginProviderName.class
        );
    }
}
