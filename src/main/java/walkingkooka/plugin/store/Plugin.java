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
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassName;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A plugin including its JAR file, and audit details of the original uploader.
 */
public final class Plugin {
    public static Plugin with(final Long id,
                              final String filename,
                              final Binary archive,
                              final ClassName className,
                              final EmailAddress user,
                              final LocalDateTime timestamp) {
        return new Plugin(
                id,
                CharSequences.failIfNullOrEmpty(filename, "filename"),
                checkArchive(archive),
                Objects.requireNonNull(className, "className"),
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

    private Plugin(final Long id,
                   final String filename,
                   final Binary archive,
                   final ClassName className,
                   final EmailAddress user,
                   final LocalDateTime timestamp) {
        this.id = id;
        this.filename = filename;
        this.archive = archive;
        this.className = className;
        this.user = user;
        this.timestamp = timestamp;
    }

    public Long id() {
        return this.id;
    }

    private final Long id;

    public String filename() {
        return this.filename;
    }

    private final String filename;

    public Binary archive() {
        return this.archive;
    }

    private final Binary archive;

    public ClassName className() {
        return this.className;
    }

    private final ClassName className;

    public EmailAddress user() {
        return this.user;
    }

    private final EmailAddress user;

    public LocalDateTime timestamp() {
        return this.timestamp;
    }

    private final LocalDateTime timestamp;

    // Object..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.filename,
                this.archive,
                this.className,
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
        return Objects.equals(this.id, other.id()) &&
                this.filename.equals(other.filename()) &&
                this.archive.equals(other.archive) &&
                this.className.equals(other.className()) &&
                this.user.equals(other.user) &&
                this.timestamp.equals(other.timestamp);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("id")
                .value(this.id)
                .value(this.filename)
                .value(this.className)
                .value(this.user)
                .value(this.timestamp)
                .build();
    }

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object();

        final Long id = this.id;
        if (null != id) {
            object = object.set(
                    ID_PROPERTY,
                    JsonNode.string(
                            String.valueOf(id)
                    )
            );
        }

        return object.set(
                FILENAME_PROPERTY,
                JsonNode.string(this.filename)
        ).set(
                ARCHIVE_PROPERTY,
                context.marshall(this.archive)
        ).set(
                CLASSNAME_PROPERTY,
                context.marshall(
                        this.className.value()
                )
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
        Long id = null;
        String filename = null;
        Binary archive = null;
        ClassName className = null;
        EmailAddress user = null;
        LocalDateTime timestamp = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case ID_PROPERTY_STRING:
                    id = context.unmarshall(
                            child,
                            Long.class
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
                case CLASSNAME_PROPERTY_STRING:
                    className = ClassName.with(
                            context.unmarshall(
                                    child,
                                    String.class
                            )
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

        if (null == filename) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(FILENAME_PROPERTY, node);
        }
        if (null == className) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(CLASSNAME_PROPERTY, node);
        }
        if (null == archive) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(ARCHIVE_PROPERTY, node);
        }
        if (null == user) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(USER_PROPERTY, node);
        }
        if (null == timestamp) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(TIMESTAMP_PROPERTY, node);
        }

        return Plugin.with(
                id,
                filename,
                archive,
                className,
                user,
                timestamp
        );
    }

    private final static String ID_PROPERTY_STRING = "id";

    private final static String FILENAME_PROPERTY_STRING = "filename";

    private final static String CLASSNAME_PROPERTY_STRING = "className";

    private final static String ARCHIVE_PROPERTY_STRING = "archive";

    private final static String USER_PROPERTY_STRING = "user";

    private final static String TIMESTAMP_PROPERTY_STRING = "timestamp";

    final static JsonPropertyName ID_PROPERTY = JsonPropertyName.with(ID_PROPERTY_STRING);

    final static JsonPropertyName FILENAME_PROPERTY = JsonPropertyName.with(FILENAME_PROPERTY_STRING);

    final static JsonPropertyName CLASSNAME_PROPERTY = JsonPropertyName.with(CLASSNAME_PROPERTY_STRING);

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
