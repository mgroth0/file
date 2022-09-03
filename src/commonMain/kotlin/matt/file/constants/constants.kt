package matt.file.constants

import kotlinx.serialization.Serializable

@Serializable
data class ValJson(
  val WAIT_FOR_MS: Int,
  val PORT: Map<String, Int>
)