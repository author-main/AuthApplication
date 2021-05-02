package example.com.authapplication

enum class AuthValue {
    SUCCESSFUL,
    ERROR_CONNECTION,
    ERROR_ALREADY_EMAIL,
    ERROR_USER_DATA,
    ERROR_AUTH_SERVICE,
    ERROR_RESTORE
}

enum class AuthBiometricValue() {
    SUCCESSFUL,
    ERROR
}

enum class AuthAction {
    SIGNIN,
    REGISTER,
    RESTORE
}

