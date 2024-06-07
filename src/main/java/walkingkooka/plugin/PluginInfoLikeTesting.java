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

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrlTesting;
import walkingkooka.net.Url;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.beans.Visibility;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginInfoLikeTesting<I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends ClassTesting2<I>,
        HashCodeEqualsDefinedTesting2<I>,
        HateosResourceTesting<I, N>,
        JsonNodeMarshallingTesting<I>,
        ComparableTesting2<I>,
        HasAbsoluteUrlTesting<I>,
        ParseStringTesting<I>,
        ToStringTesting<I> {

    // factory..........................................................................................................

    @Test
    default void testWithNullUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginInfoLike(
                        null,
                        this.createName("abc-123")
                )
        );
    }

    @Test
    default void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginInfoLike(
                        Url.parseAbsolute("https://example.com/123"),
                        null
                )
        );
    }

    N createName(final String value);

    I createPluginInfoLike(final AbsoluteUrl url,
                           final N name);

    default I createPluginInfoLike() {
        return this.createPluginInfoLike(
                Url.parseAbsolute("https://example.com/123"),
                this.createName("Test123")
        );
    }

    // Class............................................................................................................

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Comparable.......................................................................................................

    @Test
    default void testCompareLess() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/123");
        this.compareToAndCheckLess(
                this.createPluginInfoLike(
                        url,
                        this.createName("abc-123")
                ),
                this.createPluginInfoLike(
                        url,
                        this.createName("xyz-456")
                )
        );
    }

    @Override
    default I createComparable() {
        return this.createPluginInfoLike();
    }

    // equals...........................................................................................................

    @Test
    default void testEqualsDifferentUrl() {
        final N name = this.createName("abc-123");

        this.checkNotEquals(
                this.createPluginInfoLike(
                        Url.parseAbsolute("https://example.com"),
                        name
                ),
                this.createPluginInfoLike(
                        Url.parseAbsolute("https://example.com/different"),
                        name
                )
        );
    }

    @Test
    default void testEqualsDifferentName() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com");

        this.checkNotEquals(
                this.createPluginInfoLike(
                        url,
                        this.createName("abc-123")
                ),
                this.createPluginInfoLike(
                        url,
                        this.createName("different-456")
                )
        );
    }

    // Json.............................................................................................................

    @Override
    default I createJsonNodeMarshallingValue() {
        return this.createPluginInfoLike();
    }

    // HateosResource...................................................................................................

    @Test
    default void testHateosId() {
        final N name = this.createName("abc-123");
        final I info = this.createPluginInfoLike(
                Url.parseAbsolute("https://example.com/123"),
                name
        );
        this.hateosLinkIdAndCheck(
                info,
                name.value()
        );
    }

    @Test
    default void testId() {
        final N name = this.createName("abc-123");
        final I info = this.createPluginInfoLike(
                Url.parseAbsolute("https://example.com/123"),
                name
        );

        this.idAndCheck(
                info,
                Optional.of(name)
        );
    }

    @Override
    default I createHateosResource() {
        return this.createPluginInfoLike();
    }

    // parse/toString...................................................................................................

    @Test
    default void testToString() {
        final I info = this.createPluginInfoLike();
        this.toStringAndCheck(
                info,
                info.url() + " " + info.name()
        );
    }

    @Test
    default void testParseToStringRoundtrip() {
        final I info = this.createPluginInfoLike();
        this.parseStringAndCheck(
                info.toString(),
                info
        );
    }

    @Test
    default void testParseStaticMethod() throws Exception {
        final Method parse = this.type().getMethod("parse", String.class);
        this.checkEquals(
                JavaVisibility.PUBLIC,
                JavaVisibility.of(parse),
                parse::toGenericString
        );
    }

    // parse............................................................................................................

    @Test
    default void testParseInvalidUrlFails() {
        final String text = "/host/path test-name-123";

        this.parseStringFails(
                text,
                new IllegalArgumentException("no protocol: /host/path")
        );
    }

    @Test
    default void testParseInvalidNameFails() {
        final String text = "https://example.com/path #test-name/123";

        // slash within StringName will throw a InvalidCharacterException.
        this.parseStringFails(
                text,
                new InvalidCharacterException(text, text.lastIndexOf('/'))
        );
    }

    @Test
    default void testParse() {
        final String url = "https://example.com/123";
        final String name = "TestName123";

        this.parseStringAndCheck(
                url + " " + name,
                this.createPluginInfoLike(
                        Url.parseAbsolute(url),
                        this.createName(name)
                )
        );
    }
}
