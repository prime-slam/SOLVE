package solve

import org.junit.jupiter.api.Assertions.assertNull
import java.lang.ref.WeakReference

internal fun<T> testMemoryLeak(factory: () -> T, action: (T) -> Unit) {
    val weakReference = WeakReference(factory())
    action(weakReference.get()!!)
    System.gc()
    val item = weakReference.get()
    assertNull(item, "$item was not collected")
}

