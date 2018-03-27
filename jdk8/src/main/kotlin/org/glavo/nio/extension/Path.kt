package org.glavo.nio.extension

import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.stream.Stream

fun Path.walk(maxDepth: Int = Int.MAX_VALUE, vararg options: FileVisitOption): Stream<Path> =
        Files.walk(this, maxDepth, *options)

fun Path.lines(charset: Charset = Charsets.UTF_8): Stream<String> = Files.lines(this, charset)

inline fun Path.find(maxDepth: Int = Int.MAX_VALUE,
                     vararg options: FileVisitOption,
                     crossinline matcher: (Path, BasicFileAttributes) -> Boolean) =
        Files.find(this, maxDepth, BiPredicate<Path, BasicFileAttributes> { t, u -> matcher(t, u) }, *options)