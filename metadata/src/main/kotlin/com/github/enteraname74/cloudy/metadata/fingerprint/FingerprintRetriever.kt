package com.github.enteraname74.cloudy.metadata.fingerprint

import java.io.BufferedReader
import java.io.InputStreamReader

internal class FingerprintRetriever {

    fun getFingerprintFromMusic(musicPath: String): FingerprintData? =
        try {
            val command = arrayOf("fpcalc", musicPath)
            val process: Process = Runtime.getRuntime().exec(command)

            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String? = bufferedReader.readLine()
            var fingerprint = ""
            var duration = ""

            while (line != null) {
                if (isDurationField(line)) duration = getValue(line)
                else if (isFingerprintField(line)) fingerprint = getValue(line)

                line = bufferedReader.readLine()
            }

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                // Successfully calculated fingerprint
                FingerprintData(
                    duration = duration,
                    fingerprint = fingerprint,
                )
            } else {
                null
            }

        } catch (_: Exception) {
            null
        }

    private fun getValue(line: String): String =
        line.split("=").getOrNull(1).orEmpty()

    private fun isFingerprintField(line: String): Boolean =
        line.split("=").getOrNull(0) == "FINGERPRINT"

    private fun isDurationField(line: String): Boolean =
        line.split("=").getOrNull(0) == "DURATION"
}