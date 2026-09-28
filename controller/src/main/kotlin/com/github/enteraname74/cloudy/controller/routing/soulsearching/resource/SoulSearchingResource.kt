package com.github.enteraname74.cloudy.controller.routing.soulsearching.resource

import io.ktor.resources.Resource

@Resource("/soulSearching")
class SoulSearchingResource {
    @Resource("settings")
    data class Settings(
        val parent: SoulSearchingResource = SoulSearchingResource(),
    )
}
