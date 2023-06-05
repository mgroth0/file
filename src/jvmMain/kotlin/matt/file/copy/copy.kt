package matt.file.copy

import matt.lang.If
import matt.lang.anno.SeeURL
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.*
import java.nio.file.attribute.*
import java.util.*


@SeeURL("https://stackoverflow.com/a/18691793/6596010")
/**
 * Copies a directory.
 *
 *
 * NOTE: This method is not thread-safe.
 *
 *
 *
 * @param source
 * the directory to copy from
 * @param target
 * the directory to copy into
 * @throws IOException
 * if an I/O error occurs
 */
@Throws(IOException::class)
fun copyDirectoryWithAttributes(source: Path, target: Path, overwrite: Boolean = false) {
    Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Int.MAX_VALUE, object : FileVisitor<Path?> {
        @Throws(IOException::class)
        override fun preVisitDirectory(
            dir: Path?,
            sourceBasic: BasicFileAttributes
        ): FileVisitResult {
            val targetDir: Path = Files.createDirectories(
                target
                    .resolve(source.relativize(dir!!))
            )
            val acl: AclFileAttributeView? = Files.getFileAttributeView(
                dir,
                AclFileAttributeView::class.java
            )
            if (acl != null) Files.getFileAttributeView(
                targetDir,
                AclFileAttributeView::class.java
            ).acl = acl.acl
            val dosAttrs: DosFileAttributeView? = Files.getFileAttributeView(
                dir, DosFileAttributeView::class.java
            )
            if (dosAttrs != null) {
                val sourceDosAttrs = dosAttrs
                    .readAttributes()
                val targetDosAttrs: DosFileAttributeView = Files
                    .getFileAttributeView(
                        targetDir,
                        DosFileAttributeView::class.java
                    )
                targetDosAttrs.setArchive(sourceDosAttrs.isArchive)
                targetDosAttrs.setHidden(sourceDosAttrs.isHidden)
                targetDosAttrs.setReadOnly(sourceDosAttrs.isReadOnly)
                targetDosAttrs.setSystem(sourceDosAttrs.isSystem)
            }
            val ownerAttrs: FileOwnerAttributeView? = Files
                .getFileAttributeView(dir, FileOwnerAttributeView::class.java)
            if (ownerAttrs != null) {
                val targetOwner: FileOwnerAttributeView = Files
                    .getFileAttributeView(
                        targetDir,
                        FileOwnerAttributeView::class.java
                    )
                targetOwner.owner = ownerAttrs.owner
            }
            val posixAttrs: PosixFileAttributeView? = Files
                .getFileAttributeView(dir, PosixFileAttributeView::class.java)
            if (posixAttrs != null) {
                val sourcePosix = posixAttrs
                    .readAttributes()
                val targetPosix: PosixFileAttributeView = Files
                    .getFileAttributeView(
                        targetDir,
                        PosixFileAttributeView::class.java
                    )
                targetPosix.setPermissions(sourcePosix.permissions())
                targetPosix.setGroup(sourcePosix.group())
            }
            val userAttrs: UserDefinedFileAttributeView? = Files
                .getFileAttributeView(
                    dir,
                    UserDefinedFileAttributeView::class.java
                )
            if (userAttrs != null) {
                val targetUser: UserDefinedFileAttributeView = Files
                    .getFileAttributeView(
                        targetDir,
                        UserDefinedFileAttributeView::class.java
                    )
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
            // Must be done last, otherwise last-modified time may be
            // wrong
            val targetBasic: BasicFileAttributeView = Files
                .getFileAttributeView(
                    targetDir,
                    BasicFileAttributeView::class.java
                )
            targetBasic.setTimes(
                sourceBasic.lastModifiedTime(),
                sourceBasic.lastAccessTime(),
                sourceBasic.creationTime()
            )
            return FileVisitResult.CONTINUE
        }

        @Throws(IOException::class)
        override fun visitFile(
            file: Path?,
            attrs: BasicFileAttributes?
        ): FileVisitResult {
            Files.copy(
                file!!, target.resolve(source.relativize(file)),
                StandardCopyOption.COPY_ATTRIBUTES,
                *If(overwrite).then(StandardCopyOption.REPLACE_EXISTING)
            )
            return FileVisitResult.CONTINUE
        }

        @Throws(IOException::class)
        override fun visitFileFailed(file: Path?, e: IOException?): FileVisitResult? {
            throw e!!
        }

        @Throws(IOException::class)
        override fun postVisitDirectory(
            dir: Path?,
            e: IOException?
        ): FileVisitResult {
            if (e != null) throw e
            return FileVisitResult.CONTINUE
        }
    })
}