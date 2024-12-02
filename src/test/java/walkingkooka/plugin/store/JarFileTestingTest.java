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
import walkingkooka.collect.map.Maps;
import walkingkooka.plugin.JarFileTesting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public final class JarFileTestingTest implements JarFileTesting {

    @Test
    public void testJarFile() throws IOException {
        final String manifestContent = "Manifest-Version: 1.0\r\n"+
                "Test-Key: Test-Value\r\n";

        final String file1 = "dir1/Hello.txt";
        final String fileContent1 = "Hello";

        final String file2 = "dir2/Hello2.txt";
        final String fileContent2 = "Hello2";

        final byte[] jar = JarFileTesting.jarFile(
                manifestContent,
                Maps.of(
                        file1,
                        fileContent1.getBytes(StandardCharsets.UTF_8),
                        file2,
                        fileContent2.getBytes(StandardCharsets.UTF_8)
                )
        );

        final JarInputStream jarInputStream = new JarInputStream(
                new ByteArrayInputStream(jar)
        );

        final Manifest manifest = jarInputStream.getManifest();
        final Attributes mainAttributes = manifest.getMainAttributes();

        this.checkEquals(
                "1.0",
                mainAttributes.getValue("Manifest-Version"),
                "Manifest-Version\n" + mainAttributes
        );

        this.checkEquals(
                "Test-Value",
                mainAttributes.getValue("Test-Key"),
                "Test-Key\n" + mainAttributes
        );

        final Map<String, String> fileToContent = Maps.hash();

        for(;;) {
            final JarEntry entry = jarInputStream.getNextJarEntry();
            if(null == entry) {
                break;
            }

            fileToContent.put(
                    entry.getName(),
                new String(
                        jarInputStream.readAllBytes(),
                        Charset.defaultCharset()
                )
            );
        }

        this.checkEquals(
                Maps.of(
                        file1,
                        fileContent1,
                        file2,
                        fileContent2
                ),
                fileToContent
        );
    }
}
