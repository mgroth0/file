package matt.file.copy

import matt.file.JioFile
import matt.file.construct.toMFile
import matt.file.toJioFile
import matt.lang.If
import matt.lang.anno.SeeURL
import matt.lang.file.toJFile
import matt.lang.go
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.AclFileAttributeView
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.DosFileAttributeView
import java.nio.file.attribute.FileOwnerAttributeView
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.UserDefinedFileAttributeView
import java.util.EnumSet
import kotlin.io.path.fileAttributesView
import kotlin.io.path.fileAttributesViewOrNull


private const val DEFAULT_OVERWRITE = false

@SeeURL("https://stackoverflow.com/a/18691793/6596010")
/*NOTE: This method is not thread-safe.*/
fun copyPathWithAttributes(
    source: JioFile,
    target: JioFile,
    overwrite: Boolean = DEFAULT_OVERWRITE,
    definitelyMkDirs: Boolean
) {
    if (source.isDir()) {
        Files.walkFileTree(
            source.toJFile().toPath(),
            EnumSet.of(FileVisitOption.FOLLOW_LINKS),
            Int.MAX_VALUE,
            DirCopyFileVisitor(
                source = source,
                target = target,
                overwrite = overwrite,
                definitelyMkDirs = definitelyMkDirs
            )
        )
    } else {
//        println("copying $source to $target with attributes")
        copyFileWithAttributes(source, target, overwrite = overwrite, definitelyMkDirs = definitelyMkDirs)

    }

}


private class DirCopyFileVisitor(
    private val source: JioFile,
    private val target: JioFile,
    private val overwrite: Boolean = DEFAULT_OVERWRITE,
    private val definitelyMkDirs: Boolean
) : FileVisitor<Path?> {
    override fun preVisitDirectory(
        dir: Path?,
        sourceBasic: BasicFileAttributes
    ): FileVisitResult {
        val targetDir: Path = Files.createDirectories(
            target.toJFile().toPath().resolve(source.toJFile().toPath().relativize(dir!!))
        )
        dir.fileAttributesViewOrNull<AclFileAttributeView>()?.go { acl ->
            targetDir.fileAttributesView<AclFileAttributeView>().acl = acl.acl
        }
        dir.fileAttributesViewOrNull<DosFileAttributeView>()?.go { dosAttrs ->
            val sourceDosAttrs = dosAttrs
                .readAttributes()
            val targetDosAttrs = targetDir.fileAttributesView<DosFileAttributeView>()
            targetDosAttrs.setArchive(sourceDosAttrs.isArchive)
            targetDosAttrs.setHidden(sourceDosAttrs.isHidden)
            targetDosAttrs.setReadOnly(sourceDosAttrs.isReadOnly)
            targetDosAttrs.setSystem(sourceDosAttrs.isSystem)
        }
        dir.fileAttributesViewOrNull<FileOwnerAttributeView>()?.go { ownerAttrs ->
            targetDir.fileAttributesView<FileOwnerAttributeView>().owner = ownerAttrs.owner
        }
        dir.fileAttributesViewOrNull<PosixFileAttributeView>()?.readAttributes()?.go { sourcePosix ->
            val targetPosix = targetDir.fileAttributesView<PosixFileAttributeView>()
            targetPosix.setPermissions(sourcePosix.permissions())
            targetPosix.setGroup(sourcePosix.group())
        }
        dir.fileAttributesViewOrNull<UserDefinedFileAttributeView>()?.go { userAttrs ->
            val targetUser = targetDir.fileAttributesView<UserDefinedFileAttributeView>()
            for (key in userAttrs.list()) {
                val buffer: ByteBuffer = ByteBuffer.allocate(
                    userAttrs
                        .size(key)
                )
                userAttrs.read(key, buffer)
                buffer.flip()
                targetUser.write(key, buffer)
            }
        }
        /*Must be done last, otherwise last-modified time may be wrong*/
        targetDir.fileAttributesView<BasicFileAttributeView>().setTimes(
            sourceBasic.lastModifiedTime(),
            sourceBasic.lastAccessTime(),
            sourceBasic.creationTime()
        )
        return FileVisitResult.CONTINUE
    }

    override fun visitFile(
        file: Path?,
        attrs: BasicFileAttributes?
    ): FileVisitResult {
        copyFileWithAttributes(
            from = with(source.fileSystem) { file!!.toMFile().toJioFile() },
            to = with(target.fileSystem) {
                target.toJFile().toPath().resolve(source.toJFile().toPath().relativize(file)).toMFile()
            },
            overwrite = overwrite,
            definitelyMkDirs = definitelyMkDirs
        )
        return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(
        file: Path?,
        e: IOException?
    ): FileVisitResult = throw e!!

    override fun postVisitDirectory(
        dir: Path?,
        e: IOException?
    ): FileVisitResult = if (e != null) throw e else FileVisitResult.CONTINUE
}

private fun copyFileWithAttributes(
    from: JioFile,
    to: JioFile,
    overwrite: Boolean,
    definitelyMkDirs: Boolean
) {
    if (definitelyMkDirs) {
        Files.createDirectories(to.parent!!.toJFile().toPath())
    }
    Files.copy(
        from.toJFile().toPath(),
        to.toJFile().toPath(),
        StandardCopyOption.COPY_ATTRIBUTES,
        *If(overwrite).then(StandardCopyOption.REPLACE_EXISTING)
    )
}
