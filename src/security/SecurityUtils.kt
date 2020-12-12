package security

import org.apache.commons.codec.binary.Base64
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.signers.RSADigestSigner
import org.bouncycastle.crypto.util.PublicKeyFactory
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.File
import java.io.StringReader
import java.security.KeyStore
import java.security.PublicKey
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


object SecurityUtils {

    private var keyStore: KeyStore
    private val home = "${System.getProperty("user.home")}${File.separator}.pengubank-server${File.separator}"
    private val folder = File(home)
    private var file: File

    private const val SECRET_KEY_ALIAS = "pengubank-secretkey"

    init {
        Security.addProvider(BouncyCastleProvider())
        keyStore = KeyStore.getInstance("BKS", "BC")
        folder.mkdirs()
        file = File(folder, "keystore.bks")
    }

    fun verifySignature(data: String, signature: String, publicKey: String): Boolean {
        val mobilePublicKey = parsePublicKey(publicKey)
        val dataBytes = data.encodeToByteArray()
        val publicKeyParameter = PublicKeyFactory.createKey(mobilePublicKey.encoded)

        val signer = RSADigestSigner(SHA512Digest())
        signer.init(false, publicKeyParameter)
        signer.update(dataBytes, 0, dataBytes.size)

        return signer.verifySignature(Base64.decodeBase64(signature))
    }

    private fun parsePublicKey(publicKeyPEM: String): PublicKey {
        val textReader = StringReader(publicKeyPEM)
        val pemParser = PEMParser(textReader)
        val converter = JcaPEMKeyConverter()
        return converter.getPublicKey(
            SubjectPublicKeyInfo.getInstance(
                pemParser.readObject()
            )
        )
    }

    fun init() {
        val fis = if (file.exists()) file.inputStream() else null

        // Create new keystore
        keyStore.load(fis, null)
        if (!keyStore.containsAlias(SECRET_KEY_ALIAS)) {
            val key = generateSecretKey()
            keyStore.setKeyEntry(SECRET_KEY_ALIAS, key, null, null)
            save(file, keyStore)
        }
    }

    private fun getSecretKey(): SecretKey = keyStore.getKey(SECRET_KEY_ALIAS, null) as SecretKey

    private fun generateSecretKey(): SecretKey {
        val keyGenerator =
            KeyGenerator.getInstance("AES", "BC")

        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    private fun save(file: File, keyStore: KeyStore) {
        keyStore.store(file.outputStream(), null)
    }

    fun cipher(data: String): Pair<String, GCMParameterSpec> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val parameterSpec = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java)
        return Pair(
            Base64.encodeBase64String(cipher.doFinal(data.encodeToByteArray())),
            parameterSpec
        )
    }

    fun decipher(data: String, iv: ByteArray, tagLen: Int): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            getSecretKey(),
            GCMParameterSpec(tagLen, iv)
        )
        return String(cipher.doFinal(Base64.decodeBase64(data)))
    }
}