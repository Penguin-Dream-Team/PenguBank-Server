package utils

import org.apache.commons.codec.binary.Base64
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.signers.RSADigestSigner
import org.bouncycastle.crypto.util.PublicKeyFactory
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.StringReader
import java.security.PublicKey

object SecurityUtils {

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

}
