package matt.file.duri

import matt.lang.anno.Optimization
import matt.lang.mime.MimeData
import matt.lang.mime.TextMimeData
import matt.prim.base64.encodeToBase64


fun MimeData.toDataUri() = constructDataUri(mimeType = mimeType, data = data)

@Optimization
fun TextMimeData.toDataUri() = constructDataUri(mimeType = mimeType, data = textData)

@Optimization
private fun constructDataUri(
    mimeType: String,
    data: String
) = constructPreEncodedDataUri(mimeType = mimeType, base64EncodedData = data.encodeToBase64())


private fun constructDataUri(
    mimeType: String,
    data: ByteArray
) = constructPreEncodedDataUri(mimeType = mimeType, base64EncodedData = data.encodeToBase64())


private fun constructPreEncodedDataUri(
    mimeType: String,
    base64EncodedData: String
) = "data:$mimeType;base64,${base64EncodedData}"

