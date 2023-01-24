package pw.binom.web

fun <T : Enableable> T.enableOn(validated: Validated): T {
    isEnabled = validated.isValid
    validated.onValidChange.on { valid -> isEnabled = valid }
    return this
}
