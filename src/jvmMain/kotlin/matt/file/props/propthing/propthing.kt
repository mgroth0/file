package matt.file.props.propthing

import java.util.*

/*THIS ORIGINAL CAME FROM java.util.properties CODE*/
/*
     * Converts unicodes to encoded &#92;uxxxx and escapes
     * special characters with a preceding slash
     */
internal fun saveConvertForProps(
  theString: String,
  escapeSpace: Boolean,
  escapeUnicode: Boolean
): String {
  val len = theString.length
  var bufLen = len*2
  if (bufLen < 0) {
	bufLen = Int.MAX_VALUE
  }
  val outBuffer = StringBuilder(bufLen)
  val hex = HexFormat.of().withUpperCase()
  for (x in 0 until len) {
	val aChar = theString[x]
	// Handle common case first, selecting largest block that
	// avoids the specials below
	if (aChar.code > 61 && aChar.code < 127) {
	  if (aChar == '\\') {
		outBuffer.append('\\')
		outBuffer.append('\\')
		continue
	  }
	  outBuffer.append(aChar)
	  continue
	}
	when (aChar) {
	  ' '                -> {
		if (x == 0 || escapeSpace) outBuffer.append('\\')
		outBuffer.append(' ')
	  }

	  '\t'               -> {
		outBuffer.append('\\')
		outBuffer.append('t')
	  }

	  '\n'               -> {
		outBuffer.append('\\')
		outBuffer.append('n')
	  }

	  '\r'               -> {
		outBuffer.append('\\')
		outBuffer.append('r')
	  }

	  '\u000C'           -> {
		outBuffer.append('\\')
		outBuffer.append('f')
	  }

	  '=', ':', '#', '!' -> {
		outBuffer.append('\\')
		outBuffer.append(aChar)
	  }

	  else               -> if ((aChar.code < 0x0020 || aChar.code > 0x007e) and escapeUnicode) {
		outBuffer.append("\\u")
		outBuffer.append(hex.toHexDigits(aChar))
	  } else {
		outBuffer.append(aChar)
	  }
	}
  }
  return outBuffer.toString()
}
