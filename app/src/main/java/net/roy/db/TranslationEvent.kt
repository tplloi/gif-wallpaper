package net.roy.db

sealed interface TranslationEvent {
    object Reset : TranslationEvent
    data class PostTranslate(val translateX: Float, val translateY: Float) : TranslationEvent
}
