package com.github.enteraname74.cloudy.domain.model

sealed interface FileSavingData {
    val username: String
    val extension: String

    data class UserFile(
        override val username: String,
        val fileData: FileData,
    ) : FileSavingData {
        override val extension: String = fileData.extension
    }
}