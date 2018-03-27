package org.glavo.nio.file

import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption

object OpenOptions {
    val READ: OpenOption = StandardOpenOption.READ

    val WRITE: OpenOption = StandardOpenOption.WRITE

    val APPEND: OpenOption = StandardOpenOption.APPEND

    val TRUNCATE_EXISTING: OpenOption = StandardOpenOption.TRUNCATE_EXISTING

    val CREATE: OpenOption = StandardOpenOption.CREATE

    val CREATE_NEW: OpenOption = StandardOpenOption.CREATE_NEW

    val DELETE_ON_CLOSE: OpenOption = StandardOpenOption.DELETE_ON_CLOSE

    val SPARSE: OpenOption = StandardOpenOption.SPARSE

    val SYNC: OpenOption = StandardOpenOption.SYNC

    val DSYNC: OpenOption = StandardOpenOption.DSYNC

    val NOFOLLOW_LINKS: OpenOption = LinkOption.NOFOLLOW_LINKS
}