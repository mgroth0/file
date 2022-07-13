configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
  sourceSets {
	val commonMain by getting {
	  dependencies {
		implementation(kotlin("reflect"))
		implementations(
		  ":k:klib".auto(), // this is just an example, feel free to remove
		  ":k:stream".auto(),
		  handler = this
		)
	  }
	}
  }
}