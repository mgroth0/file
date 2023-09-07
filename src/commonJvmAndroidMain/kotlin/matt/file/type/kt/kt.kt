package matt.file.type.kt

import matt.file.KotlinFile


fun KotlinFile.fileAnnotationSimpleClassNames() =
  useLines {    /*there must be a space after package or UnnamedPackageIsOk will not be detected*/
	it.takeWhile { "package " !in it }.filter { KotlinFile.FILE_ANNO_LINE_MARKER in it }.map {
	  it.substringAfter(KotlinFile.FILE_ANNO_LINE_MARKER).substringAfterLast(".").substringBefore("\n")
		.substringBefore("(")
		.trim()
	}.toList()
  }

inline fun <reified A> KotlinFile.hasFileAnnotation() = A::class.simpleName in fileAnnotationSimpleClassNames()

fun KotlinFile.consts() = text.lines().map {
	it.trim()
}.filter { it.startsWith("const") }.associate {
	it.substringAfter("const").substringAfter("val").substringBefore("=").trim() to it.substringAfter("=").trim()
}

