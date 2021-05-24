package com.sion.bankcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import com.sion.bankcounter.databinding.ActivityMainBinding

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

    private fun setupUI() {
        processingTVs.add(binding.tvProcessing1)
        processingTVs.add(binding.tvProcessing2)
        processingTVs.add(binding.tvProcessing3)
        processingTVs.add(binding.tvProcessing4)
        processingTVs.add(binding.tvProcessing5)
        processedTVs.add(binding.tvProcessed1)
        processedTVs.add(binding.tvProcessed2)
        processedTVs.add(binding.tvProcessed3)
        processedTVs.add(binding.tvProcessed4)
        processedTVs.add(binding.tvProcessed5)

        binding.btNext.setOnClickListener {
            viewModel.next()
        }
    }

    private fun setObserver() {
        viewModel.waitingStatus.observe(this) {
            binding.tvWaiting.text = "waitings: $it"
        }

        viewModel.nextStatus.observe(this) {
            binding.btNext.text = "NEXT:$it"
        }

        viewModel.counterStatus.observe(this) {
            val processingTv = processingTVs[it.id]
            val processedTv = processedTVs[it.id]
            when(it.status) {
                is Status.Idle -> processingTv.text = "idle"
                is Status.Processing -> processingTv.text = (it.status as Status.Processing).number.toString()
            }
            processedTv.text = with(it.processed) {
                var result = ""
                this.forEachIndexed { index, value ->
                    result += if (index == 0) "$value" else ",$value"
                }
                result
            }
        }
    }
}