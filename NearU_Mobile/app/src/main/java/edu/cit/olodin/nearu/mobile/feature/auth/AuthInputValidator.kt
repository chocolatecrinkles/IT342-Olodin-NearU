package edu.cit.olodin.nearu.mobile.feature.auth

object AuthInputValidator {
    fun hasBlankRequiredFields(vararg values: String): Boolean {
        return values.any { it.isBlank() }
    }
}
