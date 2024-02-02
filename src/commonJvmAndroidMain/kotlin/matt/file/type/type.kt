package matt.file.type

import matt.file.construct.mFile
import matt.file.ext.FileExtension
import matt.file.types.checkType
import matt.file.types.typedFile
import matt.lang.model.file.FileSystem
import matt.lang.model.file.types.Applescript
import matt.lang.model.file.types.BinaryApplescript
import matt.lang.model.file.types.Cbor
import matt.lang.model.file.types.Icns
import matt.lang.model.file.types.Ico
import matt.lang.model.file.types.Jar
import matt.lang.model.file.types.Json
import matt.lang.model.file.types.Kexe
import matt.lang.model.file.types.Kotlin
import matt.lang.model.file.types.Log
import matt.lang.model.file.types.Markdown
import matt.lang.model.file.types.Mp4
import matt.lang.model.file.types.Png
import matt.lang.model.file.types.Python
import matt.lang.model.file.types.Svg
import matt.lang.model.file.types.Txt
import matt.lang.model.file.types.UnknownShellType


context(FileSystem)
val String.py
    get() = typedFile(mFile("$this${FileExtension.PY.withPrefixDot}")).checkType(Python)
context(FileSystem)
val String.kt
    get() = typedFile(mFile("$this${FileExtension.KT.withPrefixDot}")).checkType(Kotlin)
context(FileSystem)
val String.sh
    get() = typedFile(mFile("$this${FileExtension.SH.withPrefixDot}")).checkType(UnknownShellType)
context(FileSystem)
val String.scpt
    get() = typedFile(mFile("$this${FileExtension.SCPT.withPrefixDot}")).checkType(BinaryApplescript)
context(FileSystem)
val String.applescript
    get() = typedFile(mFile("$this${FileExtension.APPLESCRIPT.withPrefixDot}")).checkType(Applescript)
context(FileSystem)
val String.jar
    get() = typedFile(mFile("$this${FileExtension.JAR.withPrefixDot}")).checkType(Jar)
context(FileSystem)
val String.kexe
    get() = typedFile(mFile("$this${FileExtension.KEXE.withPrefixDot}")).checkType(Kexe)
context(FileSystem)
val String.json
    get() = typedFile(mFile("$this${FileExtension.JSON.withPrefixDot}")).checkType(Json)
context(FileSystem)
val String.cbor
    get() = typedFile(mFile("$this${FileExtension.CBOR.withPrefixDot}")).checkType(Cbor)
context(FileSystem)
val String.md
    get() = typedFile(mFile("$this${FileExtension.MD.withPrefixDot}")).checkType(Markdown)
context(FileSystem)
val String.png
    get() = typedFile(mFile("$this${FileExtension.PNG.withPrefixDot}")).checkType(Png)
context(FileSystem)
val String.svg
    get() = typedFile(mFile("$this${FileExtension.SVG.withPrefixDot}")).checkType(Svg)
context(FileSystem)
val String.icns
    get() = typedFile(mFile("$this${FileExtension.ICNS.withPrefixDot}")).checkType(Icns)
context(FileSystem)
val String.ico
    get() = typedFile(mFile("$this${FileExtension.ICO.withPrefixDot}")).checkType(Ico)
context(FileSystem)
val String.mp4
    get() = typedFile(mFile("$this${FileExtension.MP4.withPrefixDot}")).checkType(Mp4)
context(FileSystem)
val String.log
    get() = typedFile(mFile("$this${FileExtension.LOG.withPrefixDot}")).checkType(Log)
context(FileSystem)
val String.txt
    get() = typedFile(mFile("$this${FileExtension.TXT.withPrefixDot}")).checkType(Txt)
