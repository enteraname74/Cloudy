package com.github.enteraname74.cloudy.controller.routing.auth.resource

import io.ktor.resources.Resource

@Resource("/auth")
class AuthResource {
    @Resource("signIn")
    class SignIn(val parent: AuthResource = AuthResource())

    @Resource("logIn")
    class LogIn(val parent: AuthResource = AuthResource())

    @Resource("refreshTokens")
    class RefreshTokens(val parent: AuthResource = AuthResource())
}