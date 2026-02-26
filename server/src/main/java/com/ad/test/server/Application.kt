package com.ad.test.server

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.css.CssBuilder
import kotlinx.css.Margin
import kotlinx.css.fontSize
import kotlinx.css.margin
import kotlinx.css.px
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.p

fun Application.main() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    // Đảm bảo đường dẫn này khớp với route bên dưới
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                }
                body {
                    p {
                        +"Kotlin/CSS"
                    }
                }
            }
        }

        get("/styles.css") {
            val styleContent = CssBuilder().apply {
                rule("p") {
                    fontSize = 16.px
                    margin = Margin(10.px)
                }
            }.toString()

            call.respondText(styleContent, ContentType.Text.CSS)
        }
    }
}