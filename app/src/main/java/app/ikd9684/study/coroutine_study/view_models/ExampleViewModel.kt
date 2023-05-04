package app.ikd9684.study.coroutine_study.view_models

import app.ikd9684.study.coroutine_study.cores.ObserverManager
import app.ikd9684.study.coroutine_study.cores.SimpleDispatchGroup
import app.ikd9684.study.coroutine_study.models.PrimeNumberModel

/**
 * ある程度時間のかかる処理を非同期で実行してその完了を待ち受けるような処理の例です。
 */
class ExampleViewModel {

    companion object {
        private const val TAG = "ExampleViewModel"

        private val parentName = ObserverManager.Name("ExampleViewModel")
        val findCompleteEvent = parentName.name("findCompleteEvent")
        val progressNotify = parentName.name("progressNotify")

        val shared = ExampleViewModel()
    }

    private val primeNumbersImpl = mutableSetOf<Long>()
    val primeNumbers: List<Long> get() = primeNumbersImpl.sorted()

    private val primeFactorsImpl = mutableListOf<Long>()
    val primeFactors: List<Long> get() = primeFactorsImpl

    fun findPrimeNumbers() {
        val group = SimpleDispatchGroup()

        primeNumbersImpl.clear()
        var n = 0L
        val dx = 99999L

        repeat(3) {
            group.async {
                val from = n
                val to = n + dx
                PrimeNumberModel.findPrimeNumberListAsync(from, to) {
                    primeNumbersImpl.addAll(it)
                    ObserverManager.notify(progressNotify, "complete: findPrimeNumbers($from, $to)")
                }
            }

            n += dx + 1
        }

        val v = 123456789L
        primeFactorsImpl.clear()
        group.async {
            PrimeNumberModel.findPrimeFactorsAsync(v) {
                primeFactorsImpl.addAll(it)
                ObserverManager.notify(progressNotify, "complete: findPrimeFactors($v)")
            }
        }

        group.await {
            ObserverManager.notify(findCompleteEvent)
        }
    }
}
