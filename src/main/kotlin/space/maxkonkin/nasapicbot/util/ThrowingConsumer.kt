package space.maxkonkin.nasapicbot.util

import java.util.function.Consumer

fun interface ThrowingConsumer<T, E : Throwable> {
    fun accept(input: T)

    companion object {
        fun <T, E : Throwable> unchecked(consumer: ThrowingConsumer<T, E>): Consumer<T> {
            return Consumer { t: T ->
                try {
                    consumer.accept(t)
                } catch (e: Throwable) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}
