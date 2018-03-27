package org.glavo.nio.file

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.attribute.BasicFileAttributes

interface AbstractFileVisitor<T> : FileVisitor<T> {
    override fun preVisitDirectory(dir: T, attrs: BasicFileAttributes): FileVisitResult = FileVisitResult.CONTINUE

    override fun postVisitDirectory(dir: T, exc: IOException?): FileVisitResult =
            exc?.run { throw this } ?: FileVisitResult.CONTINUE

    override fun visitFile(file: T, attrs: BasicFileAttributes): FileVisitResult = FileVisitResult.CONTINUE

    override fun visitFileFailed(file: T, exc: IOException): FileVisitResult = throw exc
}