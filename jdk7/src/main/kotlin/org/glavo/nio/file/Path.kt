@file:Suppress("NOTHING_TO_INLINE")

package org.glavo.nio.file

import java.io.*
import java.net.URI
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileTime
import java.nio.file.attribute.UserPrincipal
import java.util.*

internal const val DEFAULT_BLOCK_SIZE = 4096

internal const val MINIMUM_BLOCK_SIZE: Int = 512

val rootDirectories: Iterable<Path>
    inline get() = FileSystems.getDefault().rootDirectories

val root: Path
    inline get() = rootDirectories.first()

@Suppress("ClassName")
object parentDir

fun pathOf(): Path = root

fun pathOf(uri: URI): Path = Paths.get(uri)

fun pathOf(first: String, vararg more: String): Path = Paths.get(first, *more)

inline operator fun Path.div(other: String): Path = this.resolve(other)

inline operator fun Path.div(other: Path): Path = this.resolve(other)

inline operator fun Path.div(p: parentDir): Path = this.parent

inline fun Path.createFile(vararg attrs: FileAttribute<*>) = Files.createFile(this, *attrs)

inline fun Path.createDirectories(vararg attrs: FileAttribute<*>): Path = Files.createDirectories(this, *attrs)

inline fun Path.createDirectory(vararg attrs: FileAttribute<*>): Path = Files.createDirectory(this, *attrs)

inline fun Path.createLink(existing: Path): Path = Files.createLink(this, existing)

inline fun Path.createSymbolicLink(target: Path, vararg attrs: FileAttribute<*>): Path =
        Files.createSymbolicLink(this, target, *attrs)

inline fun Path.delete(): Unit = Files.delete(this)

inline fun Path.deleteIfExists(): Boolean = Files.deleteIfExists(this)

fun Path.deleteRecursively() {
    this.walkFileTree(object : AbstractFileVisitor<Path> {
        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
            if (exc != null)
                throw exc
            dir.delete()
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            file.delete()
            return FileVisitResult.CONTINUE

        }
    })
}

val Path.isDirectory: Boolean
    inline get() = Files.isDirectory(this)

val Path.isExecutable: Boolean
    inline get() = Files.isExecutable(this)

val Path.isHidden: Boolean
    inline get() = Files.isHidden(this)

val Path.isReadable: Boolean
    inline get() = Files.isReadable(this)

val Path.isRegularFile: Boolean
    inline get() = Files.isRegularFile(this)

val Path.isSymbolicLink: Boolean
    inline get() = Files.isSymbolicLink(this)

val Path.isWritable: Boolean
    inline get() = Files.isWritable(this)

val Path.fileStore: FileStore
    inline get() = Files.getFileStore(this)

val Path.lastModifiedTIme: FileTime
    inline get() = Files.getLastModifiedTime(this)

val Path.extension: String
    inline get() = this.fileName.toString().substringAfterLast('.', "")

val Path.nameWithoutExtension: String
    get() = fileName.toString().substringBeforeLast(".")


val Path.probeContentType: String
    inline get() = Files.probeContentType(this)

var Path.owner: UserPrincipal
    inline get() = Files.getOwner(this)
    inline set(value) {
        Files.setOwner(this, value)
    }

inline fun Path.size(): Long = Files.size(this)

inline fun Path.exists(vararg options: LinkOption): Boolean = Files.exists(this, *options)

inline fun Path.notExists(vararg options: LinkOption): Boolean = Files.notExists(this, *options)

inline fun Path.copyTo(target: Path, vararg options: CopyOption): Path = Files.copy(this, target, *options)

inline fun Path.moveTo(target: Path, vararg options: CopyOption): Path = Files.move(this, target, *options)

inline fun Path.copyTo(out: OutputStream): Long = Files.copy(this, out)

inline fun Path.copyFrom(input: InputStream, vararg options: CopyOption): Long = Files.copy(input, this, *options)

inline fun Path.walkFileTree(visitor: FileVisitor<in Path>): Path =
        Files.walkFileTree(this, visitor)


inline fun Path.walkFileTree(maxDepth: Int, vararg options: FileVisitOption, visitor: FileVisitor<in Path>): Path =
        Files.walkFileTree(this, EnumSet.copyOf(options.asList()), maxDepth, visitor)

inline fun Path.directoryStream(glob: String): DirectoryStream<Path> =
        Files.newDirectoryStream(this, glob)

inline fun Path.directoryStream(): DirectoryStream<Path> =
        Files.newDirectoryStream(this)

inline fun Path.directoryStream(filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path> =
        Files.newDirectoryStream(this, filter)

inline fun Path.directoryStream(crossinline filter: (Path) -> Boolean): DirectoryStream<Path> =
        Files.newDirectoryStream(this, DirectoryStream.Filter<Path> { filter(it) })


inline fun Path.openChannel(vararg options: OpenOption): FileChannel = FileChannel.open(this, *options)

inline fun Path.reader(charset: Charset = Charsets.UTF_8, vararg options: OpenOption): Reader =
        InputStreamReader(inputStream(*options), charset)

inline fun Path.bufferedReader(charset: Charset = Charsets.UTF_8, bufferSize: Int = DEFAULT_BUFFER_SIZE, vararg options: OpenOption): BufferedReader =
        BufferedReader(reader(charset, *options), bufferSize)


inline fun Path.writer(charset: Charset = Charsets.UTF_8, vararg options: OpenOption): Writer =
        OutputStreamWriter(Files.newOutputStream(this, *options), charset)

inline fun Path.bufferedWriter(charset: Charset = Charsets.UTF_8, bufferSize: Int = DEFAULT_BUFFER_SIZE): BufferedWriter =
        BufferedWriter(writer(charset), bufferSize)


inline fun Path.printWriter(charset: Charset = Charsets.UTF_8, vararg options: OpenOption): PrintWriter =
        PrintWriter(writer(charset, *options))

inline fun Path.readBytes(): ByteArray = Files.readAllBytes(this)

inline fun Path.writeBytes(array: ByteArray) {
    Files.write(this, array)
}

inline fun Path.readText(charset: Charset = Charsets.UTF_8): String = String(Files.readAllBytes(this), charset)

inline fun Path.writeText(text: String, charset: Charset = Charsets.UTF_8) = writeBytes(text.toByteArray(charset))

fun Path.appendBytes(array: ByteArray) {
    Files.write(this, array, OpenOptions.WRITE, OpenOptions.APPEND, OpenOptions.CREATE)
}

fun Path.appendText(text: String, charset: Charset = Charsets.UTF_8) {
    Files.write(this, text.toByteArray(charset), OpenOptions.WRITE, OpenOptions.APPEND, OpenOptions.CREATE)
}

fun Path.forEachBlock(action: (buffer: ByteArray, bytesRead: Int) -> Unit): Unit = forEachBlock(DEFAULT_BLOCK_SIZE, action)

fun Path.forEachBlock(blockSize: Int, action: (buffer: ByteArray, bytesRead: Int) -> Unit) {
    val arr = ByteArray(blockSize.coerceAtLeast(MINIMUM_BLOCK_SIZE))

    Files.newInputStream(this).use { input ->
        do {
            val size = input.read(arr)
            if (size <= 0) {
                break
            } else {
                action(arr, size)
            }
        } while (true)
    }
}

inline fun Path.forEachLine(charset: Charset = Charsets.UTF_8, noinline action: (line: String) -> Unit) {
    bufferedReader(charset).forEachLine(action)
}

inline fun Path.inputStream(vararg options: OpenOption): InputStream = Files.newInputStream(this, *options)

inline fun Path.outputStream(vararg options: OpenOption): OutputStream = Files.newOutputStream(this, *options)

inline fun Path.readLines(charset: Charset = Charsets.UTF_8): List<String> = Files.readAllLines(this, charset)

inline fun <T> Path.useLines(charset: Charset = Charsets.UTF_8, block: (Sequence<String>) -> T): T =
        bufferedReader(charset).use { block(it.lineSequence()) }

fun Path.f() {
}