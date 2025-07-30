import kotlinx.datetime.Clock

interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit, duration: Long = 300L)

    companion object
}

internal fun MultipleEventsCutter.Companion.get(): MultipleEventsCutter =
    MultipleEventsCutterImpl()

private class MultipleEventsCutterImpl : MultipleEventsCutter {
    private val now: Long
        get() = Clock.System.now().toEpochMilliseconds()


    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit, duration: Long) {
        if (now - lastEventTimeMs >= duration) {
            event.invoke()
        }
        lastEventTimeMs = now
    }
}