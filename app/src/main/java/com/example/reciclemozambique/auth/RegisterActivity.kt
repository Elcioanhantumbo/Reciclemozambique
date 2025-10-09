// app/src/main/java/com/example/reciclemozambique/auth/RegisterActivity.kt
package com.example.reciclemozambique.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }
        binding.textSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)); finish()
        }

        binding.btnCreate.setOnClickListener {
            val name = binding.editName.text?.toString()?.trim().orEmpty()
            val email = binding.editEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.editPassword.text?.toString().orEmpty()
            val confirm = binding.editConfirm.text?.toString().orEmpty()
            val termsChecked = binding.checkboxTerms.isChecked

            var hasError = false
            if (name.isEmpty()) { binding.editName.error = getString(R.string.field_required); hasError = true } else binding.editName.error = null
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.editEmail.error = getString(R.string.invalid_email); hasError = true } else binding.editEmail.error = null
            if (pass.length < 6) { binding.editPassword.error = getString(R.string.password_min_length); hasError = true } else binding.editPassword.error = null
            if (pass != confirm) { binding.editConfirm.error = getString(R.string.password_mismatch); hasError = true } else binding.editConfirm.error = null
            if (!termsChecked) { toast(getString(R.string.terms_agree)); hasError = true }
            if (hasError) return@setOnClickListener

            setUiEnabled(false)
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { res ->
                if (!res.isSuccessful) { setUiEnabled(true); toast(res.exception?.localizedMessage); return@addOnCompleteListener }

                val uid = res.result?.user?.uid ?: run { setUiEnabled(true); toast(getString(R.string.error_generic)); return@addOnCompleteListener }

                val userDoc = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "photoUrl" to null,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                db.collection("users").document(uid).set(userDoc).addOnCompleteListener { save ->
                    setUiEnabled(true)
                    if (save.isSuccessful) {
                        // opcional: auth.currentUser?.sendEmailVerification()
                        startActivity(Intent(this, LoginActivity::class.java)); finish()
                    } else {
                        toast(save.exception?.localizedMessage)
                    }
                }
            }
        }
    }

    private fun setUiEnabled(enabled: Boolean) {
        binding.btnCreate.isEnabled = enabled
        binding.editName.isEnabled = enabled
        binding.editEmail.isEnabled = enabled
        binding.editPassword.isEnabled = enabled
        binding.editConfirm.isEnabled = enabled
        binding.checkboxTerms.isEnabled = enabled
    }

    private fun toast(msg: String?) = Toast.makeText(this, msg ?: getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
}
