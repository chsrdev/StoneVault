package dev.chsr.stonevault.utils

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AES {
    companion object {
        fun generateIV(): IvParameterSpec {
            val iv = ByteArray(16)
            val secureRandom = SecureRandom()
            secureRandom.nextBytes(iv)
            return IvParameterSpec(iv)
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun encode(
            textToEncrypt: String,
            secretKey: SecretKey,
            iv: IvParameterSpec
        ): String {
            val plainText = textToEncrypt.toByteArray()

            val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

            val encrypt = cipher.doFinal(plainText)
            return Base64.Default.encode(encrypt)
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun decode(
            encryptedText: String,
            secretKey: SecretKey,
            iv: IvParameterSpec
        ): String {
            val textToDecrypt = Base64.Default.decode(encryptedText)

            val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")

            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)

            val decrypt = cipher.doFinal(textToDecrypt)
            return String(decrypt)
        }
    }
}