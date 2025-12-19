package com.bakar.moodtracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bakar.moodtracker.data.AppDatabase
import com.bakar.moodtracker.data.MoodEntity
import com.bakar.moodtracker.databinding.ActivityAddMoodBinding
import com.bakar.moodtracker.util.MoodUtils
import kotlinx.coroutines.launch

class AddMoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMoodBinding

    private val emotions = listOf("Happy", "Neutral", "Sad", "Stressed")
    private val times = listOf("Morning", "Afternoon", "Night")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        // Setup spinners
        binding.spEmotion.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, emotions)

        binding.spTimeOfDay.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, times)

        // Update result when selection changes
        fun updateResult() {
            val emotion = binding.spEmotion.selectedItem.toString()
            val moodValue = MoodUtils.moodValueFromEmotion(emotion)
            binding.tvResult.text = "Your mood is: ${MoodUtils.getMoodText(moodValue)}"
        }

        binding.spEmotion.setOnItemSelectedListener(SimpleItemSelectedListener { updateResult() })
        binding.spTimeOfDay.setOnItemSelectedListener(SimpleItemSelectedListener { updateResult() })

        updateResult()

        binding.btnSaveMood.setOnClickListener {
            val emotion = binding.spEmotion.selectedItem.toString()
            val time = binding.spTimeOfDay.selectedItem.toString()
            val note = binding.etNote.text.toString()

            val moodValue = MoodUtils.moodValueFromEmotion(emotion)

            lifecycleScope.launch {
                db.moodDao().insert(
                    MoodEntity(
                        date = System.currentTimeMillis(),
                        moodValue = moodValue,
                        emotionType = emotion,
                        timeOfDay = time,
                        note = note
                    )
                )

                Toast.makeText(
                    this@AddMoodActivity,
                    "Saved: $emotion ($time) → ${MoodUtils.getMoodText(moodValue)}",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
        }
    }
}
