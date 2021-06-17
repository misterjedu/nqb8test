package com.oladokun.nqb8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.oladokun.nqb8.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var googleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN: Int = 1
    private val dbUserDetail = FirebaseDatabase.getInstance().getReference(USER_DETAILS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .requestProfile()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.signInWithGoogleButton.setOnClickListener {
            if (googleSignInClient != null) {
                val signInIntent: Intent = googleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            } else {
                Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    //Google Sign Up result
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {

                //Get all the infos here and do save to firebase
                addUserDetails(UserDetail(account.displayName, account.givenName, account.email))
            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            Snackbar.make(binding.signInWithGoogleButton, "An error occured", Snackbar.LENGTH_LONG)
        }
    }


    //Add Details to firebase
    private fun addUserDetails(detial: UserDetail) {
        detial.id = dbUserDetail.push().key
        dbUserDetail.child(detial.id!!).setValue(detial)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show()
                    }
                }
    }


    override fun onStart() {
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            googleSignInClient?.signOut()?.addOnCompleteListener {}
        }
    }


}