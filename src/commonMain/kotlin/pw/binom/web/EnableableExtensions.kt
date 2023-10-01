package pw.binom.web

suspend fun <T : Enableable> T.enableOn(validated: Validated): T {
    isEnabled.setValue(validated.isValid)
    validated.onValidChange.on { valid -> isEnabled.setValue(valid) }
    return this
}
