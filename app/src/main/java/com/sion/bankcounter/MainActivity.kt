package com.sion.bankcounter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sion.bankcounter.databinding.ActivityMainBinding
import com.sion.bankcounter.intent.MainIntent
import com.sion.bankcounter.model.Status
import com.sion.bankcounter.state.CounterState
import com.sion.bankcounter.state.NextState
import com.sion.bankcounter.state.WaitingState
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val processingTVs = arrayListOf<TextView>()
    private val processedTVs = arrayListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        repeat(COUNTER_NUM) {
            val counterLayout = layoutInflater.inflate(R.layout.layout_counter, binding.llCounter, false)
            val counterTV = counterLayout.findViewById<TextView>(R.id.tv_counter)
            val processingTV = counterLayout.findViewById<TextView>(R.id.tv_processing)
            val processedTV = counterLayout.findViewById<TextView>(R.id.tv_processed)
            counterTV.text = "counter${it+1}"
            processingTV.text = "idle"
            processingTVs.add(processingTV)
            processedTVs.add(processedTV)
            binding.llCounter.addView(counterLayout)
        }


        binding.btNext.setOnClickListener {
            lifecycleScope.launch {
                viewModel.mainIntent.send(MainIntent.NextClicked)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.waitingState.observe(this@MainActivity) {
                when (it) {
                    is WaitingState.InitState -> {
                    }
                    is WaitingState.ModifiedState -> {
                        binding.tvWaiting.text = "waitings: ${it.count}"
                    }
                }
            }

            viewModel.nextState.observe(this@MainActivity) {
                when (it) {
                    is NextState.InitState -> {
                    }
                    is NextState.IncreaseState -> {
                        binding.btNext.text = "NEXT:${it.next}"
                    }
                }
            }

            viewModel.counterState.observe(this@MainActivity) {
                when (it) {
                    is CounterState.InitState -> {
                    }
                    is CounterState.ModifiedState -> {
                        val processingTv = processingTVs[it.counter.id]
                        val processedTv = processedTVs[it.counter.id]
                        when (it.counter.status) {
                            is Status.Idle -> processingTv.text = "idle"
                            is Status.Processing -> processingTv.text =
                                (it.counter.status as Status.Processing).number.toString()
                        }
                        processedTv.text = with(it.counter.processed) {
                            var result = ""
                            this.forEachIndexed { index, value ->
                                result += if (index == 0) "$value" else ",$value"
                            }
                            result
                        }
                    }
                }
            }
        }
    }
}