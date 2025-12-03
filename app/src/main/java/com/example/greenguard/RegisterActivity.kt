package com.example.greenguard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.greenguard.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTermsText()
        setupListeners()
    }

    private fun setupTermsText() {
        val text = getString(R.string.register_term_conditions)
        val spannableString = SpannableString(text)

        val target = "términos y condiciones"
        val start = text.indexOf(target)
        val end = start + target.length

        if (start >= 0) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Toast.makeText(this@RegisterActivity, "Ver términos y condiciones", Toast.LENGTH_SHORT).show()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#2C9B7D")
                    ds.isUnderlineText = true
                }
            }

            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.tvTerms.text = spannableString
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                performRegister(name, email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_email_register, Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_password_register, Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, R.string.error_six_characters_password, Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_confirm_password, Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, R.string.error_not_match_password, Toast.LENGTH_SHORT).show()
            return false
        }

        if (!binding.cbTerms.isChecked) {
            Toast.makeText(this, R.string.error_not_check_terms_conditions, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun performRegister(name: String, email: String, password: String) {
        // Aquí integrarías tu lógica de registro (Firebase, API, etc.)
        Toast.makeText(this, "Registrando usuario...", Toast.LENGTH_SHORT).show()

        // Ejemplo: navegar a MainActivity después del registro exitoso
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}