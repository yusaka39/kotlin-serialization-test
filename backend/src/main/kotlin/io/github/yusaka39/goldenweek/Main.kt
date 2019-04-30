package io.github.yusaka39.goldenweek

import org.glassfish.jersey.server.ResourceConfig
import javax.ws.rs.Path

class Main : ResourceConfig() {
    init {
        packages(true, "io.github.yusaka39.goldenweek")
    }
}