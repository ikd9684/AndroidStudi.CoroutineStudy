package app.ikd9684.study.coroutine_study.cores

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

open class DispatchGroup<T> {

    private val deferredList = mutableListOf<Deferred<T>>()

    fun async(process: () -> Deferred<T>) {
        deferredList.add(process())
    }

    fun await(completion: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            deferredList.awaitAll()
            completion()
        }
    }
}

class SimpleDispatchGroup : DispatchGroup<Unit>()
