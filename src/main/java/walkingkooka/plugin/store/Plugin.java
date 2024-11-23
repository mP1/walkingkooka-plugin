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
}
