package app.ikd9684.study.coroutine_study.models

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.math.sqrt

/**
 * このクラスはある程度時間のかかる処理が欲しかっただけで、このクラスの中身に意味は無いです。
 */
class PrimeNumberModel {

    companion object {
        private const val TAG = "PrimeNumberModel"

        fun findPrimeNumberListAsync(
            from: Long,
            to: Long,
            completion: (Set<Long>) -> Unit,
        ): Deferred<Unit> {
            Log.d(TAG, "findAsync($from, $to)")
            return CoroutineScope(Dispatchers.IO).async {
                val result = findPrimeNumberList(from, to)
                Log.d(TAG, "findAsync($from, $to): completion()")
                completion(result)
            }
        }

        fun findPrimeFactorsAsync(
            value: Long,
            completion: (List<Long>) -> Unit,
        ): Deferred<Unit> {
            Log.d(TAG, "findPrimeFactors($value)")
            return CoroutineScope(Dispatchers.IO).async {
                val result = findPrimeFactors(value)
                Log.d(TAG, "findPrimeFactors($value): completion()")
                completion(result)
            }
        }

        private fun findPrimeNumberList(from: Long, to: Long): Set<Long> {
            val result = mutableSetOf<Long>()
            for (i in (if (from <= 2) 2 else from) until to) {
                var j = 2L
                while (j < i) {
                    if (i % j == 0L) {
                        break
                    }
                    j++
                }
                if (i == j) {
                    result.add(i)
                }
            }
            return result
        }

        private fun findPrimeFactors(n: Long): List<Long> {
            var num = n
            val factors = mutableListOf<Long>()
            for (i in 2L..sqrt(num.toDouble()).toInt()) {
                while (num % i == 0L) {
                    factors.add(i)
                    num /= i
                }
            }
            if (num > 1L) {
                factors.add(num)
            }
            return factors
        }
    }
}
