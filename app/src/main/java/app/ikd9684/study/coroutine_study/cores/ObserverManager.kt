package app.ikd9684.study.coroutine_study.cores

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference
import java.util.Collections

class ObserverManager private constructor() {

    companion object {

        private val observersMap =
            Collections.synchronizedMap(mutableMapOf<String, MutableMap<WeakReference<Any>, ((Any?) -> Unit)>>())

        fun add(
            obj: Any,
            name: Name,
            process: (params: Any?) -> Unit,
        ) {
            synchronized(observersMap) {
                val observers = observersMap[name.string] ?: mutableMapOf()
                observers[WeakReference(obj)] = process
                observersMap[name.string] = observers
            }
        }

        fun remove(
            obj: Any,
            name: Name? = null,
        ) {
            synchronized(observersMap) {
                observersMap.values.forEach { observers ->
                    observers.keys.forEach { key ->
                        key.get() ?: run { observers.remove(key) }
                    }
                }

                val refObj = WeakReference(obj)
                observersMap.forEach { element ->
                    if (name == null || element.key == name.string) {
                        element.value.remove(refObj)
                    }
                }
            }
        }

        fun notify(
            name: Name,
            param: Any? = null
        ) {
            val observers = observersMap[name.string] ?: return
            observers.forEach { entry ->
                // 登録元オブジェクトが無くなっていたら通知しない
                val refObj = entry.key.get() ?: run {
                    observers.remove(entry.key)
                    return@forEach // continue
                }
                // 登録元がActivityでかつ終了していたら通知しない
                (refObj as? Activity)?.let { activity ->
                    if (activity.isFinishing || activity.isDestroyed) {
                        observers.remove(entry.key)
                        return@forEach // continue
                    }
                }
                // 登録元がFragmentでかつ終了していたら通知しない
                (refObj as? Fragment)?.let { fragment ->
                    val activity = fragment.activity
                    if (fragment.isDetached || fragment.isRemoving || fragment.context == null
                        || activity == null || activity.isFinishing || activity.isDestroyed
                    ) {
                        observers.remove(entry.key)
                        return@forEach // continue
                    }
                }

                Handler(Looper.getMainLooper()).post {
                    entry.value.invoke(param)
                }
            }
        }
    }

    data class Name(
        val string: String
    ) {
        fun name(name: String): Name {
            return Name("${this.string}_$name")
        }
    }
}
