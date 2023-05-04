package app.ikd9684.study.coroutine_study.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import app.ikd9684.study.coroutine_study.R
import app.ikd9684.study.coroutine_study.cores.ObserverManager
import app.ikd9684.study.coroutine_study.databinding.ActivityMainBinding
import app.ikd9684.study.coroutine_study.view_models.ExampleViewModel

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ObserverManager.add(this, ExampleViewModel.progressNotify) {
            val result = (it as? String) ?: ""
            appendResultText(result)
        }
        ObserverManager.add(this, ExampleViewModel.findCompleteEvent) {
            handleComplete()
        }

        binding.textStatus.text = getString(R.string.processing)
        binding.textResult.text = ""

        ExampleViewModel.shared.findPrimeNumbers()
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverManager.remove(this)
    }

    private fun appendResultText(result: String) {
        val resultText = binding.textResult.text
        binding.textResult.text =
            if (resultText.isNullOrEmpty()) result else "$resultText\n$result"
    }

    private fun handleComplete() {
        val primeNumbers = ExampleViewModel.shared.primeNumbers
        val primeFactors = ExampleViewModel.shared.primeFactors
        appendResultText("primeNumbers: size=${primeNumbers.size}, $primeNumbers")
        appendResultText("primeFactors: $primeFactors")
        binding.textStatus.text = getString(R.string.complete)
    }
}
