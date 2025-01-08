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

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.plugin.PluginName;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginTest implements HashCodeEqualsDefinedTesting2<Plugin>,
    ToStringTesting<Plugin>,
    ComparableTesting2<Plugin>,
    ClassTesting<Plugin>,
    JsonNodeMarshallingTesting<Plugin>,
    HateosResourceTesting<Plugin, PluginName> {

    private final static PluginName NAME = PluginName.with("TestPlugin123");

    private final static String FILENAME = "file.jar";

    private final static Binary ARCHIVE = Binary.with("hello".getBytes(Charset.defaultCharset()));

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static LocalDateTime TIMESTAMP = LocalDateTime.of(1999, 12, 31, 12, 58);

    // with.............................................................................................................

    @Test
    public void testWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> Plugin.with(
                null,
                FILENAME,
                ARCHIVE,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullFilenameFails() {
        assertThrows(
            NullPointerException.class,
            () -> Plugin.with(
                NAME,
                null,
                ARCHIVE,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testWithEmptyFilenameFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Plugin.with(
                NAME,
                "",
                ARCHIVE,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullArchiveFails() {
        assertThrows(
            NullPointerException.class,
            () -> Plugin.with(
                NAME,
                FILENAME,
                null,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testWithEmptyArchiveFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Plugin.with(
                NAME,
                FILENAME,
                Binary.EMPTY,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullUserFails() {
        assertThrows(
            NullPointerException.class,
            () -> Plugin.with(
                NAME,
                FILENAME,
                ARCHIVE,
                null,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullTimestampFails() {
        assertThrows(
            NullPointerException.class,
            () -> Plugin.with(
                NAME,
                FILENAME,
                ARCHIVE,
                USER,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final Plugin plugin = Plugin.with(
            NAME,
            FILENAME,
            ARCHIVE,
            USER,
            TIMESTAMP
        );
        this.checkEquals(NAME, plugin.name(), "name");
        this.checkEquals(FILENAME, plugin.filename(), "filename(");
        this.checkEquals(ARCHIVE, plugin.archive(), "archive");
        this.checkEquals(USER, plugin.user(), "user");
        this.checkEquals(TIMESTAMP, plugin.timestamp(), "timestamp");
    }


    @Override
    public Plugin createHateosResource() {
        return this.createObject();
    }

    // Comparable.......................................................................................................

    @Test
    public void testCompareSort() {
        final Plugin a1 = Plugin.with(
            PluginName.with("A1"),
            FILENAME,
            ARCHIVE,
            USER,
            TIMESTAMP
        );

        final Plugin b2 = Plugin.with(
            PluginName.with("B2"),
            FILENAME,
            ARCHIVE,
            USER,
            TIMESTAMP
        );

        final Plugin c3 = Plugin.with(
            PluginName.with("C3"),
            FILENAME,
            ARCHIVE,
            USER,
            TIMESTAMP
        );

        this.compareToArraySortAndCheck(
            c3,
            a1,
            b2,
            a1,
            b2,
            c3
        );
    }

    @Override
    public Plugin createComparable() {
        return this.createObject();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentId() {
        this.checkNotEquals(
            Plugin.with(
                PluginName.with("Different"),
                FILENAME,
                ARCHIVE,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentFilename() {
        this.checkNotEquals(
            Plugin.with(
                NAME,
                "different.jar",
                ARCHIVE,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentArchive() {
        this.checkNotEquals(
            Plugin.with(
                NAME,
                FILENAME,
                Binary.with(
                    "different".getBytes(Charset.defaultCharset())
                ),
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentUser() {
        this.checkNotEquals(
            Plugin.with(
                NAME,
                FILENAME,
                ARCHIVE,
                EmailAddress.parse("different@example.com"),
                TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentTimestamp() {
        this.checkNotEquals(
            Plugin.with(
                NAME,
                FILENAME,
                ARCHIVE,
                USER,
                LocalDateTime.now()
            )
        );
    }

    @Override
    public Plugin createObject() {
        return Plugin.with(
            NAME,
            FILENAME,
            ARCHIVE,
            USER,
            TIMESTAMP
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            Plugin.with(
                NAME,
                FILENAME,
                ARCHIVE,
                USER,
                TIMESTAMP
            ),
            "TestPlugin123 \"file.jar\" user@example.com 1999-12-31T12:58"
        );
    }

    // json.............................................................................................................

    @Override
    public Plugin createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public Plugin unmarshall(final JsonNode json,
                             final JsonNodeUnmarshallContext context) {
        return Plugin.unmarshall(
            json,
            context
        );
    }

    // class............................................................................................................

    @Override
    public Class<Plugin> type() {
        return Plugin.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
