package com.bakar.moodtracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.bakar.moodtracker.data.AppDatabase
import com.bakar.moodtracker.data.RoutineEntity
import com.bakar.moodtracker.databinding.ActivityAddRoutineBinding
import kotlinx.coroutines.launch

class AddRoutineActivity : ComponentActivity() {

    private lateinit var binding: ActivityAddRoutineBinding
    private lateinit var db: AppDatabase

    private var routineId: Int = 0
    private var oldRoutine: RoutineEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        routineId = intent.getIntExtra("routineId", 0)

        if (routineId != 0) {
            // ✅ Load old routine for edit
            lifecycleScope.launch {
                oldRoutine = db.routineDao().getById(routineId)
                binding.etRoutine.setText(oldRoutine?.title ?: "")
                binding.btnSave.text = "Update Routine"
            }
        }

        binding.btnSave.setOnClickListener {
            val text = binding.etRoutine.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(this, "Enter routine", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (routineId == 0) {
                    // ✅ Add new
                    db.routineDao().insert(
                        RoutineEntity(
                            title = text,
                            date = System.currentTimeMillis()
                        )
                    )
                } else {
                    // ✅ Update existing
                    val current = oldRoutine
                    if (current != null) {
                        db.routineDao().update(
                            current.copy(title = text)
                        )
                    }
                }
                finish()
            }
        }
    }
}
