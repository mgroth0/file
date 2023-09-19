package matt.file.type.kt

import matt.file.toJioFile
import matt.lang.classname.SimpleClassName
import matt.lang.classname.simpleClassName
import matt.lang.file.toJFile
import matt.lang.model.file.types.Kotlin.FILE_ANNO_LINE_MARKER
import matt.lang.model.file.types.KotlinFile


fun KotlinFile.fileAnnotationSimpleClassNames() =
    toJFile().useLines {    /*there must be a space after package or UnnamedPackageIsOk will not be detected*/
        it.takeWhile { "package " !in it }.filter { FILE_ANNO_LINE_MARKER in it }.map {
            it.substringAfter(FILE_ANNO_LINE_MARKER).substringAfterLast(".").substringBefore("\n").substringBefore("(")
                .trim()
        }.toList()
    }.map { SimpleClassName(it) }

inline fun <reified A> KotlinFile.hasFileAnnotation() = A::class.simpleClassName in fileAnnotationSimpleClassNames()

fun KotlinFile.consts() = toJioFile().lines().map {
    it.trim()
}.filter { it.startsWith("const") }.associate {
    it.substringAfter("const").substringAfter("val").substringBefore("=").trim() to it.substringAfter("=").trim()
}

