package matt.file.duri

import matt.lang.anno.Optimization
import matt.lang.mime.MimeData
import matt.lang.mime.TextMimeData
import matt.lang.model.file.types.MimeType
import matt.prim.base64.encodeToBase64


fun MimeData.toDataUri() = constructDataUri(mimeType = mimeType, data = asBinary)

@Optimization
fun TextMimeData.toDataUri() = constructDataUri(mimeType = mimeType, data = asText)

@Optimization
private fun constructDataUri(
    mimeType: MimeType,
    data: String
) = constructPreEncodedDataUri(mimeType = mimeType, base64EncodedData = data.encodeToBase64())


private fun constructDataUri(
    mimeType: MimeType,
    data: ByteArray
) = constructPreEncodedDataUri(mimeType = mimeType, base64EncodedData = data.encodeToBase64())


private fun constructPreEncodedDataUri(
    mimeType: MimeType,
    base64EncodedData: String
) = "data:${mimeType.identifier};base64,${base64EncodedData}"

