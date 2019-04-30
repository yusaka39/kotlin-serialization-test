package io.github.yusaka39.goldenweek.api

import io.github.yusaka39.goldenweek.types.Hello
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/hello")
class HelloResource {
    @Path("/")
    @Produces("application/json")
    @GET
    fun hello(): Hello = Hello()
}
