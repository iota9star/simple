
import android.util.Log
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

inline fun <reified T> T.logi(message: () -> String) = Log.i(T::class.simpleName, message())

inline fun <reified T> T.loge(error: Throwable, message: () -> String) = Log.e(T::class.simpleName, message(), error)

class Async<T>(val v: T? = null, val e: Exception? = null)

fun <T> start(block: suspend () -> T): Deferred<Async<T>> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) {
        try {
            Async(v = block())
        } catch (e: Exception) {
            loge(e) { "there are some errors on start: " }
            Async<T>(e = e)
        }
    }
}

infix fun <T, R> Deferred<Async<T>>.then(block: suspend (T?) -> R): Deferred<Async<R>> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) {
        try {
            val e = this@then.await().e
            if (e != null) {
                return@async Async<R>(e = e)
            }
            Async(v = block(this@then.await().v))
        } catch (e: Exception) {
            loge(e) { "there are some errors on then: " }
            Async<R>(e = e)
        }
    }
}

infix fun <T> Deferred<Async<T>>.inUI(result: suspend (Async<T>) -> Unit): Job {
    return launch(context = UI) {
        try {
            result(this@inUI.await())
        } catch (e: Exception) {
            throw e
        }
    }
}

infix fun <T> Deferred<Async<T>>.inSub(result: suspend (Async<T>) -> Unit): Job {
    return launch(context = CommonPool) {
        try {
            result(this@inSub.await())
        } catch (e: Exception) {
            throw e
        }
    }
}

fun <A, B, R> Deferred<A>.combine(b: Deferred<B>, cr: DeferredCombine2<A, B, R>): Deferred<R> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) { cr.apply(this@combine.await(), b.await()) }
}

fun <A, B, C, R> Deferred<A>.combine(b: Deferred<B>, c: Deferred<C>, cr: DeferredCombine3<A, B, C, R>): Deferred<R> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) { cr.apply(this@combine.await(), b.await(), c.await()) }
}

fun <A, B, C, D, R> Deferred<A>.combine(b: Deferred<B>, c: Deferred<C>, d: Deferred<D>, cr: DeferredCombine4<A, B, C, D, R>): Deferred<R> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) { cr.apply(this@combine.await(), b.await(), c.await(), d.await()) }
}

fun <A, B, C, D, E, R> Deferred<A>.combine(b: Deferred<B>, c: Deferred<C>, d: Deferred<D>, e: Deferred<E>, cr: DeferredCombine5<A, B, C, D, E, R>): Deferred<R> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) { cr.apply(this@combine.await(), b.await(), c.await(), d.await(), e.await()) }
}

fun <A, B, C, D, E, F, R> Deferred<A>.combine(b: Deferred<B>, c: Deferred<C>, d: Deferred<D>, e: Deferred<E>, f: Deferred<F>, cr: DeferredCombine6<A, B, C, D, E, F, R>): Deferred<R> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) { cr.apply(this@combine.await(), b.await(), c.await(), d.await(), e.await(), f.await()) }
}

fun <A, B, C, D, E, F, G, R> Deferred<A>.combine(b: Deferred<B>, c: Deferred<C>, d: Deferred<D>, e: Deferred<E>, f: Deferred<F>, g: Deferred<G>, cr: DeferredCombine7<A, B, C, D, E, F, G, R>): Deferred<R> {
    return async(context = CommonPool, start = CoroutineStart.LAZY) { cr.apply(this@combine.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await()) }
}

interface DeferredCombine2<A, B, R> {
    fun apply(a: A, b: B): R
}

interface DeferredCombine3<A, B, C, R> {
    fun apply(a: A, b: B, c: C): R
}

interface DeferredCombine4<A, B, C, D, R> {
    fun apply(a: A, b: B, c: C, d: D): R
}

interface DeferredCombine5<A, B, C, D, E, R> {
    fun apply(a: A, b: B, c: C, d: D, e: E): R
}

interface DeferredCombine6<A, B, C, D, E, F, R> {
    fun apply(a: A, b: B, c: C, d: D, e: E, f: F): R
}

interface DeferredCombine7<A, B, C, D, E, F, G, R> {
    fun apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G): R
}
