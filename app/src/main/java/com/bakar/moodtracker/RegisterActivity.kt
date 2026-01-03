package com.bakar.moodtracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // 🔴 EMPTY FIELDS CHECK
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔴 EMAIL FORMAT CHECK
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔴 PASSWORD LENGTH CHECK
            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 🔴 CONFIRM PASSWORD CHECK
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                // 🔴 CHECK IF USER ALREADY EXISTS
                val exists = db.userDao().checkUser(email)
                if (exists != null) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email already registered",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                // ✅ SAVE USER IN ROOM
                db.userDao().insert(
                    UserEntity(
                        email = email,
                        password = password
                    )
                )

                Toast.makeText(
                    this@RegisterActivity,
                    "Registration successful",
                    Toast.LENGTH_SHORT
                ).show()

                // ✅ GO BACK TO LOGIN
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
