package com.bakar.moodtracker

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakar.moodtracker.adapter.MoodAdapter
import com.bakar.moodtracker.data.AppDatabase
import com.bakar.moodtracker.databinding.ActivityMoodHistoryBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import java.util.Calendar

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodHistoryBinding
    private val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // default = today
        loadForSelectedDay()

        binding.btnPickDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(Calendar.YEAR, y)
                    cal.set(Calendar.MONTH, m)
                    cal.set(Calendar.DAY_OF_MONTH, d)
                    loadForSelectedDay()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun loadForSelectedDay() {
        val db = AppDatabase.getDatabase(this)

        val start = startOfDay(cal)
        val end = endOfDay(cal)

        lifecycleScope.launch {
            val moods = db.moodDao().getByDateRange(start, end)

            // list
            binding.recyclerView.adapter = MoodAdapter(moods)

            // chart
            showDonutChart(moods)
        }
    }

    private fun showDonutChart(moods: List<com.bakar.moodtracker.data.MoodEntity>) {

        // count by emotionType
        val counts = moods.groupingBy { it.emotionType }.eachCount()

        if (counts.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.centerText = "No moods"
            binding.pieChart.invalidate()
            return
        }

        val entries = counts.map { (emotion, count) ->
            PieEntry(count.toFloat(), emotion)
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawValues(true)

        val data = PieData(dataSet)
        data.setValueTextSize(12f)

        binding.pieChart.apply {
            setUsePercentValues(false)          // you can set true later if you want %
            description.isEnabled = false
            isRotationEnabled = true

            setDrawHoleEnabled(true)
            holeRadius = 65f
            transparentCircleRadius = 70f

            centerText = "Moods"
            setCenterTextSize(16f)

            legend.isEnabled = true

            this.data = data
            invalidate()
        }
    }

    private fun startOfDay(c: Calendar): Long {
        val tmp = c.clone() as Calendar
        tmp.set(Calendar.HOUR_OF_DAY, 0)
        tmp.set(Calendar.MINUTE, 0)
        tmp.set(Calendar.SECOND, 0)
        tmp.set(Calendar.MILLISECOND, 0)
        return tmp.timeInMillis
    }

    private fun endOfDay(c: Calendar): Long {
        val tmp = c.clone() as Calendar
        tmp.set(Calendar.HOUR_OF_DAY, 23)
        tmp.set(Calendar.MINUTE, 59)
        tmp.set(Calendar.SECOND, 59)
        tmp.set(Calendar.MILLISECOND, 999)
        return tmp.timeInMillis
    }
}
