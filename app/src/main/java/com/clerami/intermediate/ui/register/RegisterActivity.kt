package com.clerami.intermediate.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.clerami.intermediate.MainActivity
import com.clerami.intermediate.R
import com.clerami.intermediate.data.remote.retrofit.ApiConfig
import com.clerami.intermediate.databinding.ActivityRegisterBinding
import com.clerami.intermediate.ui.customview.EditText
import com.clerami.intermediate.ui.login.LoginActivity
import com.clerami.intermediate.utils.Resource
import com.clerami.intermediate.utils.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val apiService = ApiConfig.getApiService()
        registerViewModel = ViewModelProvider(
            this,
            RegisterViewModelFactory(apiService)
        ).get(RegisterViewModel::class.java)


        if (SessionManager.isLoggedIn(this)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setUpClickableSpan()

        setupPasswordFieldValidation()
        binding.register.setOnClickListener {

            handleRegister()
        }
        addPasswordTextWatcher()


    }


    private fun handleRegister() {
        val email = binding.email.text.toString().trim()
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString().trim()


        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }



        registerViewModel.register(email, password, username).observe(this) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {

                    binding.loading.visibility = View.VISIBLE
                    binding.loading.setProgressCompat(100, true)
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        putExtra(
                            "REGISTRATION_SUCCESS",
                            "Account created successfully. Please log in."
                        )
                    }
                    startActivity(intent)
                    finish()
                }

                Resource.Status.ERROR -> {
                    binding.loading.visibility = View.INVISIBLE
                    binding.loading.setProgressCompat(0, true)
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.loading.visibility = View.VISIBLE
                    binding.loading.setProgressCompat(30, true)
                    moveProgress(30, 50)
                    moveProgress(50, 80)

                }
            }
        }
    }


    private fun moveProgress(start: Int, end: Int) {
        val delay = 1000L
        val increment = (end - start)
        for (i in start until end step increment) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.loading.setProgressCompat(i, true)
            }, (i - start) * delay)
        }
    }


    private fun addPasswordTextWatcher() {
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                setMyButtonEnable()

            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setMyButtonEnable() {
        val password = binding.password.text?.toString()
        val isButtonEnabled = !password.isNullOrEmpty() && password.length >= 8
        binding.register.isEnabled = isButtonEnabled
        binding.register.text = "Register"
    }

    private fun setUpClickableSpan() {
        val text = binding.alreadyHaveAccount.text.toString()
        val spannableString = SpannableString(text)
        val loginText = "Login"
        val startIndex = text.indexOf(loginText)
        val endIndex = startIndex + loginText.length


        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.color_two, theme)
                ds.isUnderlineText = true
            }
        }

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.alreadyHaveAccount.text = spannableString
        binding.alreadyHaveAccount.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupPasswordFieldValidation() {
        binding.password.setValidationCallback(object : EditText.ValidationCallback {
            override fun onValidationError(errorMessage: String?) {
                binding.password.error = errorMessage
            }
        })
    }


}
