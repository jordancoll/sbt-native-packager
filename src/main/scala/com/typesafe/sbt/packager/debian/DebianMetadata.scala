package com.typesafe.sbt
package packager
package debian

case class PackageInfo(
  name: String,
  version: String,
  maintainer: String,
  summary: String,
  description: String)

case class PackageRelationships(
  depends: Seq[String] = Seq.empty,
  recommends: Seq[String] = Seq.empty,
  suggests: Seq[String] = Seq.empty,
  enhances: Seq[String] = Seq.empty,
  predepends: Seq[String] = Seq.empty,
  breaks: Seq[String] = Seq.empty,
  conflicts: Seq[String] = Seq.empty,
  provides: Seq[String] = Seq.empty,
  replaces: Seq[String] = Seq.empty) {
  override def toString: String = {
    val sb = new StringBuilder
    if (!depends.isEmpty)
      sb append ("Depends: %s\n" format (depends mkString ", "))
    if (!predepends.isEmpty)
      sb append ("Pre-Depends: %s\n" format (predepends mkString ", "))
    if (!recommends.isEmpty)
      sb append ("Recommends: %s\n" format (recommends mkString ", "))
    if (!suggests.isEmpty)
      sb append ("Suggests: %s\n" format (suggests mkString ", "))
    if (!breaks.isEmpty)
      sb append ("Breaks: %s\n" format (breaks mkString ", "))
    if (!conflicts.isEmpty)
      sb append ("Conflicts: %s\n" format (conflicts mkString ", "))
    if (!provides.isEmpty)
      sb append ("Provides: %s\n" format (provides mkString ", "))
    if (!replaces.isEmpty)
      sb append ("Replaces: %s\n" format (replaces mkString ", "))
    if (!enhances.isEmpty)
      sb append ("Enhances: %s\n" format (enhances mkString ", "))

    sb toString
  }
}

/** Represents package meta used by debian when constructing packages. */
case class PackageMetaData(
  info: PackageInfo,
  priority: String = "optional",
  architecture: String = "all",
  section: String = "java",
  relationships: PackageRelationships) {
  def makeContent(installSizeEstimate: Long = 0L): String = {
    // TODO: Pretty print with line wrap.
    val sb = new StringBuilder
    sb append ("Source: %s\n" format info.name)
    sb append ("Package: %s\n" format info.name)
    sb append ("Version: %s\n" format info.version)
    sb append ("Section: %s\n" format section)
    sb append ("Priority: %s\n" format priority)
    sb append ("Architecture: %s\n" format architecture)
    sb append ("Installed-Size: %d\n" format installSizeEstimate)
    sb append relationships toString ()
    sb append ("Maintainer: %s\n" format info.maintainer)
    sb append ("Description: %s\n %s\n" format (info.summary, info.description))
    sb toString
  }

  def makeSourceControl(): String = {
    val sb = new StringBuilder
    sb append ("Source: %s\n" format info.name)
    sb append ("Maintainer: %s\n" format info.maintainer)
    sb append ("Section: %s\n" format section)
    sb append ("Priority: %s\n\n" format priority)

    sb append ("Package: %s\n" format info.name)
    sb append ("Architecture: %s\n" format architecture)
    sb append ("Section: %s\n" format section)
    sb append ("Priority: %s\n" format priority)
    sb append relationships toString ()
    sb append ("Description: %s\n %s\n" format (info.summary, info.description))
    sb toString
  }
}

/**
 * This replacements are use for the debian maintainer scripts:
 * preinst, postinst, prerm, postrm
 */
case class DebianControlScriptReplacements(
  author: String,
  descr: String,
  name: String,
  version: String) {

  /**
   * Generates the replacement sequence for the debian
   * maintainer scripts
   */
  def makeReplacements(): Seq[(String, String)] = Seq(
    "author" -> author,
    "descr" -> descr,
    "name" -> name,
    "version" -> version
  )
}