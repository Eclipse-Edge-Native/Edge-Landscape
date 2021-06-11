import org.asciidoctor.gradle.jvm.AsciidoctorJExtension
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.xml.sax.InputSource
import java.nio.file.Files.newInputStream
import java.nio.file.Files.newOutputStream
import java.nio.file.StandardOpenOption
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult

plugins {
    id("com.github.sgtsilvio.gradle.metadata")
    id("org.asciidoctor.jvm.base")
}

repositories {
    mavenCentral()
}


/* ******************** metadata ******************** */

group = "org.eclipse.edgenative.landscape"
description = "Edge Native Landscape ${project.version}"

metadata {
    moduleName = "org.eclipse.edgenative.landscape"
    readableName = "Edge Native Landscape ${project.version}"

    organization {
        name = "Eclipse Foundation"
        url = "https://sparkplug.eclipse.org/"
    }
    license {
        shortName = "EPL-2.0"
        readableName = "Eclipse Public License - v 2.0"
        url = "https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt"
    }
    github {
        org = "Eclipse-Edge-Native"
        repo = "Edge-Landscape"
        pages()
        issues()
    }
}


/* ******************** asciidoctor ******************** */

val asciidoctorPdf = tasks.register("asciidoctorPdf", AsciidoctorTask::class) {
    group = "landscape"
    dependsOn(copySpec)

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/landscape"))
    sources {
        include("*.adoc", "chapters/*.adoc")
    }
    setOutputDir(buildDir.resolve("docs/pdf"))

    outputOptions {
        setBackends(listOf("pdf"))
    }

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
            pdf.use()
            pdf.setVersion(project.property("plugin.asciidoctor.pdf.version"))
        }

        setOptions(mapOf(
                "doctype" to "book",
                "header_footer" to "true",
                "template_engine" to "slim",
                "compact" to "false"
        ))

        setAttributes(mapOf(
                "source-highlighter" to "highlight.js",
                "pagenums" to "true",
                "numbered" to "true",
                "docinfo2" to "true",
                "experimental" to "false",
                "linkcss" to "false",
                "toc" to "true",
                "project-version" to project.version,
                "imagesdir" to "assets/images"
        ))
    }

}

val asciidoctorHtml = tasks.register("asciidoctorHtml", AsciidoctorTask::class) {
    group = "landscape"
    dependsOn(copySpec)

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/landscape"))
    sources {
        include("*.adoc", "chapters/*.adoc")
    }
    setOutputDir(buildDir.resolve("docs/html"))

    outputOptions {
        setBackends(listOf("html5"))
    }

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
        }

        setOptions(mapOf(
                "header_footer" to "true"
        ))
        setAttributes(mapOf(
                "source-highlighter" to "highlight.js",
                "toc" to "true",
                "docinfo2" to "true",
                "linkcss" to "false",
                "project-version" to project.version,
                "imagesdir" to "assets/images"
        ))
    }
}

val asciidoctorDocbook = tasks.register("asciidoctorDocbook", AsciidoctorTask::class) {
    group = "landscape"
    dependsOn("copyLandscapeSourceIntoBuild")

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/landscape"))
    sources {
        include("*.adoc", "chapters/*.adoc")
    }
    setOutputDir(buildDir.resolve("docs/docbook"))
    outputs.file(buildDir.resolve("docs/docbook/edge_landscape.xml"))

    outputOptions {
        setBackends(listOf("docbook"))
    }

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
        }

        setOptions(mapOf(
                "doctype" to "article",
                "header_footer" to "true"
        ))

        setAttributes(mapOf(
                "project-version" to version,
                "imagesdir" to "assets/images"
        ))
    }
}


/* ******************** additional ******************** */

val copySpec = tasks.register("copyLandscapeSourceIntoBuild", Copy::class) {
    group = "landscape"

    from("src/main/asciidoc")
    into(buildDir.resolve("landscape"))
}


val renameHtml = tasks.register("renameHtml", Copy::class) {
    group = "landscape"
    dependsOn("asciidoctorHtml")

    from(buildDir.resolve("docs/html/edge_landscape.html")) {
        rename { "index.html" }
    }
    into(buildDir.resolve("docs/html"))
}



tasks.named("build") {
    dependsOn(asciidoctorPdf, asciidoctorHtml, renameHtml)
}