package example.com.authapplication

enum class AuthValue {
    SUCCESSFUL,
    SUCCESSFUL_RESTORE,
    SUCCESSFUL_REGISTER,
    ERROR_CONNECTION,
    ERROR_RESTORE,
    ERROR_REGISTER,
    ERROR_ALREADY_EMAIL,
    ERROR_USER_DATA,
    ERROR_AUTH_SERVICE
}