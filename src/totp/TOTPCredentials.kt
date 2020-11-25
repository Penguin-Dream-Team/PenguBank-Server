package totp

/**
 * TOTP authenticator credentials.
 *
 * @param secretKey the secret key.
 * @param verificationCode the verification code at time = 0 (the UNIX epoch).
 * @param scratchCodes the list of scratch codes.
 */
data class TOTPCredentials(
    val secretKey: TOTPSecretKey,
    val verificationCode: Int,
    val scratchCodes: List<Int> = emptyList()
)
