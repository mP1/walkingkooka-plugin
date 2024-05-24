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

import com.sun.source.util.Plugin;
import walkingkooka.Cast;
import walkingkooka.naming.HasName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Provides a few bits of info describing a {@link Plugin}. The {@link AbsoluteUrl} must be a unique identifier,
 * with the {@link PluginInfo} being a shorter human friendly reference.
 */
public final class PluginInfo implements HasName<PluginName>,
        Comparable<PluginInfo> {

    public static PluginInfo with(final AbsoluteUrl url,
                                  final PluginName name) {
        return new PluginInfo(
                Objects.requireNonNull(url, "url"),
                Objects.requireNonNull(name, "name")
        );
    }

    private PluginInfo(final AbsoluteUrl url,
                       final PluginName name) {
        this.url = url;
        this.name = name;
    }

    public AbsoluteUrl url() {
        return this.url;
    }

    private final AbsoluteUrl url;

    // HasName..........................................................................................................

    @Override
    public PluginName name() {
        return this.name;
    }

    private final PluginName name;

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final PluginInfo other) {
        return this.name.compareTo(other.name);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.url,
                this.name
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginInfo &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginInfo other) {
        return this.url.equals(other.url) &&
                this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return this.url + " " + this.name;
    }

    // Json.............................................................................................................

    private final static String URL_PROPERTY_STRING = "url";

    private final static String NAME_PROPERTY_STRING = "name";

    // @VisibleForTesting
    final static JsonPropertyName URL_PROPERTY = JsonPropertyName.with(URL_PROPERTY_STRING);

    final static JsonPropertyName NAME_PROPERTY = JsonPropertyName.with(NAME_PROPERTY_STRING);

    static PluginInfo unmarshall(final JsonNode node,
                                 final JsonNodeUnmarshallContext context) {
        AbsoluteUrl url = null;
        PluginName pluginName = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (name.value()) {
                case URL_PROPERTY_STRING:
                    url = context.unmarshall(
                            child,
                            AbsoluteUrl.class
                    );
                    break;
                case NAME_PROPERTY_STRING:
                    pluginName = context.unmarshall(
                            child,
                            PluginName.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return with(
                url,
                pluginName
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(URL_PROPERTY, context.marshall(this.url))
                .set(NAME_PROPERTY, context.marshall(this.name));
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(PluginInfo.class),
                PluginInfo::unmarshall,
                PluginInfo::marshall,
                PluginInfo.class
        );
    }
}
