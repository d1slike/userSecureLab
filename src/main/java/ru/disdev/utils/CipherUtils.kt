package ru.disdev.utils

import ru.disdev.service.PHRASE
import java.io.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec


private const val AL = "RC4"

private val key: SecretKeySpec = SecretKeySpec(PHRASE, AL)


fun getSafeFileInputStream(pathToFile: String): InputStream {
    return getSafeFileInputStream(File(pathToFile))
}

fun getSaveFileOutputStream(pathToFile: String): OutputStream {
    return getSaveFileOutputStream(File(pathToFile))
}

fun getSafeFileInputStream(file: File): InputStream {
    val cipher = Cipher.getInstance(AL)
    cipher.init(Cipher.DECRYPT_MODE, key)
    return CipherInputStream(FileInputStream(file), cipher)
}

fun getSaveFileOutputStream(file: File): OutputStream {
    val cipher = Cipher.getInstance(AL)
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return CipherOutputStream(FileOutputStream(file), cipher)
}