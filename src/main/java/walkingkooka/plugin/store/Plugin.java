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

package walkingkooka.plugin.store;

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.naming.HasName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.plugin.PluginName;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * A plugin including its JAR file, and audit details of the original uploader.
 * <br>
 * Note {@link Comparable#compareTo(Object)} only uses the {@link #name(), ignoring all other properties.
 */
public final class Plugin implements HateosResource<PluginName>,
        Comparable<Plugin>,
        HasName<PluginName> {

    /**
     * A {@link HateosResourceName} with <code>plugin</code>.
     */
    public static final HateosResourceName HATEOS_RESOURCE_NAME = HateosResourceName.with("plugin");

    public static Plugin with(final PluginName name,
                              final String filename,
                              final Binary archive,
                              final EmailAddress user,
                              final LocalDateTime timestamp) {
        return new Plugin(
                Objects.requireNonNull(name, "name"),
                CharSequences.failIfNullOrEmpty(filename, "filename"),
                checkArchive(archive),
                Objects.requireNonNull(user, "user"),
                Objects.requireNonNull(timestamp, "timestamp")
        );
    }

    private static Binary checkArchive(final Binary archive) {
        Objects.requireNonNull(archive, "archive");
        if (archive.isEmpty()) {
            throw new IllegalArgumentException("Empty archive");
        }
        return archive;
    }

    private Plugin(final PluginName name,
                   final String filename,
                   final Binary archive,
                   final EmailAddress user,
                   final LocalDateTime timestamp) {
        this.name = name;
        this.filename = filename;
        this.archive = archive;
        this.user = user;
        this.timestamp = timestamp;
    }

    @Override
    public PluginName name() {
        return this.name;
    }

    private final PluginName name;

    public String filename() {
        return this.filename;
    }

    private final String filename;

    public Binary archive() {
        return this.archive;
    }

    private final Binary archive;

    public EmailAddress user() {
        return this.user;
    }

    private final EmailAddress user;

    public LocalDateTime timestamp() {
        return this.timestamp;
    }

    private final LocalDateTime timestamp;

    // HateosResourceId.................................................................................................

    @Override
    public Optional<PluginName> id() {
        return Optional.of(
                this.name()
        );
    }

    @Override
    public String hateosLinkId() {
        return this.name().toString();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final Plugin plugin) {
        return this.name.compareTo(plugin.name);
    }

    // Object..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.filename,
                this.archive,
                this.user,
                this.timestamp
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof Plugin &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final Plugin other) {
        return this.name.equals(other.name()) &&
                this.filename.equals(other.filename()) &&
                this.archive.equals(other.archive) &&
                this.user.equals(other.user) &&
                this.timestamp.equals(other.timestamp);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.name)
                .value(this.filename)
                .value(this.user)
                .value(this.timestamp)
                .build();
    }

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(
                NAME_PROPERTY,
                context.marshall(this.name)
        ).set(
                FILENAME_PROPERTY,
                JsonNode.string(this.filename)
        ).set(
                ARCHIVE_PROPERTY,
                context.marshall(this.archive)
        ).set(
                USER_PROPERTY,
                context.marshall(this.user)
        ).set(
                TIMESTAMP_PROPERTY,
                context.marshall(this.timestamp)
        );
    }

    static Plugin unmarshall(final JsonNode node,
                             final JsonNodeUnmarshallContext context) {
        PluginName pluginName = null;
        String filename = null;
        Binary archive = null;
        EmailAddress user = null;
        LocalDateTime timestamp = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case NAME_PROPERTY_STRING:
                    pluginName = context.unmarshall(
                            child,
                            PluginName.class
                    );
                    break;
                case FILENAME_PROPERTY_STRING:
                    filename = context.unmarshall(
                            child,
                            String.class
                    );
                    break;
                case ARCHIVE_PROPERTY_STRING:
                    archive = context.unmarshall(
                            child,
                            Binary.class
                    );
                    break;
                case USER_PROPERTY_STRING:
                    user = context.unmarshall(
                            child,
                            EmailAddress.class
                    );
                    break;
                case TIMESTAMP_PROPERTY_STRING:
                    timestamp = context.unmarshall(
                            child,
                            LocalDateTime.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(
                            name,
                            node
                    );
                    break;
            }
        }

        if (null == pluginName) {
            JsonNodeUnmarshallContext.missingProperty(NAME_PROPERTY, node);
        }
        if (null == filename) {
            JsonNodeUnmarshallContext.missingProperty(FILENAME_PROPERTY, node);
        }
        if (null == archive) {
            JsonNodeUnmarshallContext.missingProperty(ARCHIVE_PROPERTY, node);
        }
        if (null == user) {
            JsonNodeUnmarshallContext.missingProperty(USER_PROPERTY, node);
        }
        if (null == timestamp) {
            JsonNodeUnmarshallContext.missingProperty(TIMESTAMP_PROPERTY, node);
        }

        return Plugin.with(
                pluginName,
                filename,
                archive,
                user,
                timestamp
        );
    }

    private final static String NAME_PROPERTY_STRING = "name";

    private final static String FILENAME_PROPERTY_STRING = "filename";

    private final static String ARCHIVE_PROPERTY_STRING = "archive";

    private final static String USER_PROPERTY_STRING = "user";

    private final static String TIMESTAMP_PROPERTY_STRING = "timestamp";

    final static JsonPropertyName NAME_PROPERTY = JsonPropertyName.with(NAME_PROPERTY_STRING);

    final static JsonPropertyName FILENAME_PROPERTY = JsonPropertyName.with(FILENAME_PROPERTY_STRING);

    final static JsonPropertyName ARCHIVE_PROPERTY = JsonPropertyName.with(ARCHIVE_PROPERTY_STRING);

    final static JsonPropertyName USER_PROPERTY = JsonPropertyName.with(USER_PROPERTY_STRING);

    final static JsonPropertyName TIMESTAMP_PROPERTY = JsonPropertyName.with(TIMESTAMP_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(Plugin.class),
                Plugin::unmarshall,
                Plugin::marshall,
                Plugin.class
        );
    }
}
