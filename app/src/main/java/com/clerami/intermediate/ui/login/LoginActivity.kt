package com.clerami.intermediate.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clerami.intermediate.MainActivity
import com.clerami.intermediate.R
import com.clerami.intermediate.data.remote.retrofit.ApiConfig
import com.clerami.intermediate.databinding.ActivityLoginBinding
import com.clerami.intermediate.ui.customview.EditText
import com.clerami.intermediate.ui.register.RegisterActivity

import com.clerami.intermediate.utils.Resource
import com.clerami.intermediate.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Skip login if already logged in
        if (SessionManager.isLoggedIn(this)) {
            navigateToMainActivity()
            return
        }

        val apiService = ApiConfig.getApiService()
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(apiService)).get(LoginViewModel::class.java)

        // Setting up SignUp clickable text
        setupSignUpClickableSpan()

        // Password and Email Text Watchers
        addPasswordTextWatcher()
        addEmailTextWatcher()
        loginViewModel.login("email@example.com", "password")
        // Login Button Click Listener
        binding.login.setOnClickListener {
            val usernameOrEmail = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (usernameOrEmail.isNotEmpty() && password.isNotEmpty()) {
                performLogin(usernameOrEmail, password)
            } else {
                showToast("Please enter both email and password.")
            }
        }

        // Handle successful registration message
        val successMessage = intent.getStringExtra("REGISTRATION_SUCCESS")
        successMessage?.let {
            showToast(it)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun performLogin(usernameOrEmail: String, password: String) {
        binding.loading.visibility = View.VISIBLE

        loginViewModel.login(usernameOrEmail, password).observe(this) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    binding.loading.setProgressCompat(100, true)
                    showToast("Login Successful!")
                    resource.data?.let { SessionManager.saveSession(this, it) }
                    navigateToMainActivity()
                }

                Resource.Status.ERROR -> {
                    binding.loading.visibility = View.GONE
                    showToast(resource.message ?: "Unknown error")
                }

                Resource.Status.LOADING -> {
                    binding.loading.visibility = View.VISIBLE
                    binding.loading.setProgressCompat(20, true)
                    moveProgress(50, 80)
                    moveProgress(80, 100)
                }
            }
        }
    }

    private fun moveProgress(start: Int, end: Int) {
        // Simplified progress movement, consider animations for smoother transitions
        val delay = 1000L
        Handler(Looper.getMainLooper()).postDelayed({
            binding.loading.setProgressCompat(end, true)
        }, delay)
    }

    private fun setupSignUpClickableSpan() {
        val text = binding.dontHaveAccount.text.toString()
        val spannableString = SpannableString(text)
        val signUpText = "Sign up"
        val startIndex = text.indexOf(signUpText)
        val endIndex = startIndex + signUpText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.color_two, theme)
                ds.isUnderlineText = true
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.dontHaveAccount.text = spannableString
        binding.dontHaveAccount.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }

    private fun addPasswordTextWatcher() {
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun addEmailTextWatcher() {
        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val email = charSequence.toString()
                binding.email.error = when {
                    email.isEmpty() -> getString(R.string.email_cannot_be_empty)
                    !isValidEmail(email) -> getString(R.string.invalid_email_format)
                    else -> null
                }
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setupPasswordFieldValidation() {
        binding.password.setValidationCallback(object : EditText.ValidationCallback {
            override fun onValidationError(errorMessage: String?) {
                binding.password.error = errorMessage
            }
        })
    }

    private fun setMyButtonEnable() {
        val password = binding.password.text?.toString()
        binding.login.isEnabled = !password.isNullOrEmpty() && password.length >= 8
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
