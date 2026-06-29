package com.github.enteraname74.cloudy.controller.routing.user.resource

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

@Resource("/users")
class UserResource {

    @Resource("{id}")
    class Delete(
        val parent: UserResource = UserResource(),
        val id: Uuid,
    )

    @Resource("generateCode")
    class GenerateCode(
        val parent: UserResource = UserResource(),
    )
}
