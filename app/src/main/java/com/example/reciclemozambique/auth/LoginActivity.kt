// app/src/main/java/com/example/reciclemozambique/auth/LoginActivity.kt
package com.example.reciclemozambique.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityLoginBinding
import com.example.reciclemozambique.ui.MapActivity
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { Firebase.firestore }

    // Google (Identity API)
    private val googleIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { res ->
            try {
                val credential = Identity.getSignInClient(this)
                    .getSignInCredentialFromIntent(res.data)
                val idToken = credential.googleIdToken
                if (idToken.isNullOrEmpty()) {
                    toast("Falha ao obter ID token do Google")
                    return@registerForActivityResult
                }
                val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCred).addOnCompleteListener {
                    if (it.isSuccessful) { ensureUserDoc(); goHome() }
                    else toast(it.exception?.localizedMessage)
                }
            } catch (e: ApiException) { toast(e.localizedMessage) }
            catch (e: Exception)    { toast(e.localizedMessage) }
        }

    // Facebook
    private lateinit var fbCallback: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // E-mail/senha
        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            val pass  = binding.editPassword.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                toast(getString(R.string.field_required)); return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) { ensureUserDoc(); goHome() }
                else toast(it.exception?.localizedMessage)
            }
        }

        // Esqueci a senha
        binding.textForgot.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            if (email.isEmpty()) { toast(getString(R.string.invalid_email)); return@setOnClickListener }
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                toast(if (it.isSuccessful) "Email de recuperação enviado" else it.exception?.localizedMessage)
            }
        }

        // Ir para cadastro
        binding.textNoAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Google
        binding.btnGoogle.setOnClickListener { startGoogleIdentity() }

        // Facebook
        fbCallback = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            fbCallback,
            object : com.facebook.FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) { ensureUserDoc(); goHome() }
                        else toast(task.exception?.localizedMessage)
                    }
                }
                override fun onCancel() { toast("Login do Facebook cancelado") }
                override fun onError(error: FacebookException) { toast(error.localizedMessage) }
            }
        )
        binding.btnFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        }
    }

    private fun startGoogleIdentity() {
        val request = GetSignInIntentRequest.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        Identity.getSignInClient(this).getSignInIntent(request)
            .addOnSuccessListener { pendingIntent ->
                googleIntentLauncher.launch(
                    IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                )
            }
            .addOnFailureListener { e -> toast(e.localizedMessage) }
    }

    @Deprecated("Facebook SDK ainda usa onActivityResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fbCallback.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /** Cria /users/{uid} se ainda não existir */
    private fun ensureUserDoc() {
        val u = auth.currentUser ?: return
        val ref = db.collection("users").document(u.uid)
        ref.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                ref.set(
                    hashMapOf(
                        "uid"       to u.uid,
                        "name"      to (u.displayName ?: ""),
                        "email"     to (u.email ?: ""),
                        "photoUrl"  to (u.photoUrl?.toString()),
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }
        }
    }

    // AGORA abre o MAPA após login
    private fun goHome() {
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }

    private fun toast(msg: String?) =
        Toast.makeText(this, msg ?: getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
}
