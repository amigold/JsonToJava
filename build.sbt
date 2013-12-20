import com.github.retronym.SbtOneJar._

oneJarSettings

mainClass in oneJar := Some("JsonToJava")

name := "JsonToJava"

version := "0.1"

mainClass in (Compile,run) := Some("JsonToJava")

exportJars := true

packageOptions in (Compile, packageBin) +=
  Package.ManifestAttributes( java.util.jar.Attributes.Name.SEALED -> "true" )