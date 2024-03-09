package matt.file.type.kt

import matt.file.toJioFile
import matt.lang.classname.common.SimpleClassName
import matt.lang.classname.common.simpleClassName
import matt.lang.common.substringAfterSingular
import matt.lang.common.substringBeforeSingular
import matt.lang.common.substringBeforeSingularOrNone
import matt.lang.model.file.types.Kotlin.FILE_ANNO_LINE_MARKER
import matt.lang.model.file.types.KotlinFile
import kotlin.io.path.useLines


fun KotlinFile.fileAnnotationSimpleClassNames() =
    toJioFile()
        .useLines { lines ->
            /*there must be a space after package or UnnamedPackageIsOk will not be detected*/
            lines.takeWhile {
                "package " !in it
            }.filter {
                FILE_ANNO_LINE_MARKER in it
            }.map {
                it
                    .substringAfterSingular(FILE_ANNO_LINE_MARKER)
                    .substringAfterLast(".")
                    .substringBeforeSingularOrNone("\n")
                    .substringBeforeSingularOrNone("(")
                    .trim()
            }.toList()
        }.map { SimpleClassName(it) }

inline fun <reified A> KotlinFile.hasFileAnnotation() =
    A::class.simpleClassName in fileAnnotationSimpleClassNames()

fun KotlinFile.consts() =
    toJioFile().lines().map {
        it.trim()
    }.filter {
        it.startsWith("const")
    }.associate {
        it
            .substringAfterSingular("const")
            .substringAfterSingular("val")
            .substringBeforeSingular("=")
            .trim() to it.substringAfterSingular("=").trim()
    }

