package com.github.enteraname74.cloudy.domain.ext

import java.io.File

fun File.ensureExist(): File =
    apply { mkdirs() }