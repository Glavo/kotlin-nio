@file:Suppress("NOTHING_TO_INLINE")

package org.glavo.nio.file

import java.nio.file.*

val FileSystem.roots: List<Path>
    inline get() = this.rootDirectories.toList()

inline operator fun FileSystem.div(other: String): Path = this.rootDirectories.first() / other

inline operator fun FileSystem.div(other: Path): Path = this.rootDirectories.first() / other
