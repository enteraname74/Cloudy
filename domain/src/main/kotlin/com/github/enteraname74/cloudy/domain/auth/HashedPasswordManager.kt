package com.github.enteraname74.cloudy.domain.auth

interface HashedPasswordManager {

    /**
     * Builds an [HashedPassword] from a given password.
     * Can return null if no hashed password could be created.
     */
    fun buildHashedPassword(password: String): HashedPassword?

    /**
     * Checks if a password is matching a hashed one.
     */
    fun isMatching(password: String, hashedPassword: HashedPassword): Boolean
}