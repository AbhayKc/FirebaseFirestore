package com.example.firebase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ActivityHomePageBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Initialize the RecyclerView and set its adapter
        val userDataItems = arrayListOf<UserItemData>()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = Adapter(userDataItems)
        recyclerView.adapter = adapter

        //Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signOutButton = findViewById<AppCompatButton>(R.id.logoutBtn)
        signOutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }
        // FireBase
        binding.send.setOnClickListener {
            val text1 = binding.appCompatEditText.text.toString()
            val text2 = binding.appCompatEditText2.text.toString()

            val userMap = hashMapOf(
                "txt1" to text1,
                "txt2" to text2
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val uniqueDocumentId = db.collection("user").document().id

            //Save Data  in Firebase
            db.collection("user").document(userId).collection("userData").document(uniqueDocumentId)
                .set(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully Added!", Toast.LENGTH_SHORT).show()
                    binding.appCompatEditText.text?.clear()
                    binding.appCompatEditText2.text?.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }

            //Retrieve Data from Firebase
            db.collection("user").document(userId).collection("userData")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val txt1 = document.getString("txt1")
                        val txt2 = document.getString("txt2")

                        if (txt1 != null && txt2 != null) {
                            userDataItems.add(UserItemData(txt1, txt2))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun signOutAndStartSignInActivity() {
        auth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this@HomePageActivity, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}