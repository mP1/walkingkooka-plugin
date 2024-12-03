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

import walkingkooka.test.Testing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Helpers to create JAR files with a manifest.
 */
public interface JarFileTesting extends Testing {

    static Manifest manifest(final String manifestContent) throws IOException {
        final Manifest manifest = new Manifest();
        manifest.read(
                new ByteArrayInputStream(
                        manifestContent.getBytes(Charset.defaultCharset())
                )
        );
        return manifest;
    }

    LocalDateTime CREATE = LocalDateTime.of(
            1999,
            12,
            31,
            12,
            58
    );

    LocalDateTime LAST_MODIFIED = LocalDateTime.of(
            2000,
            1,
            2,
            4,
            58
    );

    static byte[] jarFile(final String manifestContent,
                          final Map<String, byte[]> contents) throws IOException {
        try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {

            final JarOutputStream jarOut = new JarOutputStream(
                    bytes,
                    manifest(manifestContent)
            );

            final ZoneId zoneId = ZoneId.of("GMT");

            for (final Map.Entry<String, byte[]> mapEntry : contents.entrySet()) {
                final JarEntry jarEntry = new JarEntry(mapEntry.getKey());

                final byte[] resource = mapEntry.getValue();

                jarEntry.setCreationTime(
                        FileTime.fromMillis(
                                CREATE.atZone(zoneId)
                                        .toEpochSecond()
                        )
                );
                jarEntry.setLastModifiedTime(
                        FileTime.fromMillis(
                                LAST_MODIFIED.atZone(zoneId)
                                        .toEpochSecond()
                        )
                );

                jarOut.putNextEntry(jarEntry);
                jarOut.write(resource);
                jarOut.closeEntry();
            }

            jarOut.flush();
            jarOut.finish();
            jarOut.close();

            return bytes.toByteArray();
        }
    }
}
