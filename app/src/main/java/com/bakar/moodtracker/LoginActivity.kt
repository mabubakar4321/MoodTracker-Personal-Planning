package com.bakar.moodtracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.bakar.moodtracker.data.AppDatabase
import com.bakar.moodtracker.databinding.ActivityLoginBinding
import com.bakar.moodtracker.util.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        session = SessionManager(this)

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // 🔴 EMPTY CHECK
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔴 EMAIL FORMAT CHECK
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔴 PASSWORD LENGTH CHECK
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().login(email, password)

                if (user != null) {
                    // ✅ SAVE LOGIN SESSION
                    session.saveLogin(user.id)

                    // ✅ GO TO MAIN SCREEN
                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java)
                    )
                    finish()
                } else {
                    // ❌ INVALID LOGIN
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(
                Intent(this@LoginActivity, RegisterActivity::class.java)
            )
        }
    }
}
