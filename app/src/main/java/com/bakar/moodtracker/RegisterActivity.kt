package com.bakar.moodtracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.bakar.moodtracker.data.AppDatabase
import com.bakar.moodtracker.data.UserEntity
import com.bakar.moodtracker.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val exists = db.userDao().checkUser(email)
                if (exists != null) {
                    Toast.makeText(this@RegisterActivity, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    db.userDao().insert(UserEntity(email = email, password = pass))
                    Toast.makeText(this@RegisterActivity, "Signup successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
