package com.example.security

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
  private const val ALGORITHM = "AES/GCM/NoPadding"
  private const val TAG_LENGTH_BIT = 128
  private const val IV_LENGTH_BYTE = 12

  // Internal application master salt & key derivation
  private val SECRET_SEED = "AuraSync_Secure_2026_Key_V1_Encrypted_Privacy".toByteArray(StandardCharsets.UTF_8)

  private val secretKey: SecretKeySpec by lazy {
    val sha = MessageDigest.getInstance("SHA-256")
    val keyBytes = sha.digest(SECRET_SEED)
    SecretKeySpec(keyBytes, "AES")
  }

  fun encrypt(plainText: String): String {
    if (plainText.isEmpty()) return ""
    return try {
      val cipher = Cipher.getInstance(ALGORITHM)
      cipher.init(Cipher.ENCRYPT_MODE, secretKey)
      val iv = cipher.iv
      val cipherBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

      val combined = ByteArray(iv.size + cipherBytes.size)
      System.arraycopy(iv, 0, combined, 0, iv.size)
      System.arraycopy(cipherBytes, 0, combined, iv.size, cipherBytes.size)

      Base64.encodeToString(combined, Base64.NO_WRAP)
    } catch (e: Exception) {
      // In case of platform encryption error, fallback safely
      plainText
    }
  }

  fun decrypt(encryptedText: String): String {
    if (encryptedText.isEmpty()) return ""
    return try {
      val combined = Base64.decode(encryptedText, Base64.NO_WRAP)
      if (combined.size <= IV_LENGTH_BYTE) return encryptedText

      val iv = ByteArray(IV_LENGTH_BYTE)
      val cipherBytes = ByteArray(combined.size - IV_LENGTH_BYTE)
      System.arraycopy(combined, 0, iv, 0, IV_LENGTH_BYTE)
      System.arraycopy(combined, IV_LENGTH_BYTE, cipherBytes, 0, cipherBytes.size)

      val cipher = Cipher.getInstance(ALGORITHM)
      val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
      cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

      val plainBytes = cipher.doFinal(cipherBytes)
      String(plainBytes, StandardCharsets.UTF_8)
    } catch (e: Exception) {
      // Return as is if already plain or decoding issue
      encryptedText
    }
  }

  fun maskEmail(email: String): String {
    val atIndex = email.indexOf('@')
    if (atIndex <= 2) return email
    val namePart = email.substring(0, atIndex)
    val domainPart = email.substring(atIndex)
    val maskedName = namePart.first() + "***" + namePart.last()
    return "$maskedName$domainPart"
  }
}
