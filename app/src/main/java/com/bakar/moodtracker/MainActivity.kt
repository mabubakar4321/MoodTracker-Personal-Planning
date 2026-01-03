package com.bakar.moodtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakar.moodtracker.adapter.RoutineAdapter
import com.bakar.moodtracker.data.AppDatabase
import com.bakar.moodtracker.databinding.ActivityMainBinding
import com.bakar.moodtracker.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RoutineAdapter
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ SESSION CHECK (BLOCK UNAUTHORIZED ACCESS)
        session = SessionManager(this)
        if (!session.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // ✅ LOGOUT BUTTON CLICK (THIS WAS MISSING)
        binding.btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        adapter = RoutineAdapter(
            onClick = { routine ->
                val i = Intent(this, AddRoutineActivity::class.java)
                i.putExtra("routineId", routine.id)
                startActivity(i)
            },
            onToggleDone = { routine, isDone ->
                lifecycleScope.launch {
                    db.routineDao().update(routine.copy(isCompleted = isDone))
                    loadRoutines()
                }
            },
            onLongPressDelete = { routine ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Routine")
                    .setMessage("Delete this routine?")
                    .setPositiveButton("Delete") { _, _ ->
                        lifecycleScope.launch {
                            db.routineDao().delete(routine)
                            loadRoutines()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.recyclerRoutine.layoutManager = LinearLayoutManager(this)
        binding.recyclerRoutine.adapter = adapter

        binding.btnAddRoutine.setOnClickListener {
            startActivity(Intent(this, AddRoutineActivity::class.java))
        }

        binding.btnAddMood.setOnClickListener {
            startActivity(Intent(this, AddMoodActivity::class.java))
        }

        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, MoodHistoryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadRoutines()
    }

    private fun loadRoutines() {
        lifecycleScope.launch {
            adapter.submitList(db.routineDao().getAll())
        }
    }
}
