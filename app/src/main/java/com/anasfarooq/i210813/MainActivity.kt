package com.anasfarooq.i210813

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.Models.Booking
import com.anasfarooq.i210813.Models.Mentor
import com.anasfarooq.i210813.Models.MentorType
import com.anasfarooq.i210813.Models.Review
import com.anasfarooq.i210813.Models.UserProfile
import com.anasfarooq.i210813.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var firebasedatabase: FirebaseDatabase

        const val REQUEST_STORAGE_PERMISSION = 101
        const val PICK_IMAGE_REQUEST = 102
        const val PICK_VIDEO_REQUEST = 103
        const val PERMISSION_REQ_ID_RECORD_AUDIO = 22

        lateinit var currentUserInfo: UserProfile
        lateinit var topMentorList: ArrayList<Mentor>
        lateinit var educationMentorList: ArrayList<Mentor>
        lateinit var personalGrowthMentorList: ArrayList<Mentor>
        var userList = ArrayList<UserProfile>()

        lateinit var reviewList: ArrayList<Review>
        lateinit var bookings: ArrayList<Booking>

        const val PREFS_NAME = "MyPrefsFile"
        const val LOGIN_STATUS_KEY = "loginStatus"

        public fun isOnline(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)

        // Initializations
        auth = FirebaseAuth.getInstance()
        firebasedatabase = FirebaseDatabase.getInstance()
        currentUserInfo = UserProfile("", "", "", "", "", "")
        topMentorList = ArrayList()
        educationMentorList = ArrayList()
        personalGrowthMentorList = ArrayList()
        reviewList = ArrayList()
        bookings = ArrayList()
        userList = ArrayList()

        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        if (isLoggedIn() && isOnline(this)) {
            loadAllData { navigateToHome() }
        } else if (isLoggedIn() && !isOnline(this)) {
            Toast.makeText(this, "Offline mode. Using cached data.", Toast.LENGTH_LONG).show()
            // load data from cache
            loadAllData {
                navigateToHome() // Navigate to home directly since data will be loaded from cache
            }
        } else {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000) // Delay to simulate loading/splash screen
    }

    private fun loadAllData(onDataLoaded: () -> Unit) {
        loadUserDetails {
            loadMentors {
                loadReviews {
                    loadBookedMentors {
                        loadUserChats {
                            onDataLoaded()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun loadUserDetails(onDetailsLoaded: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val databaseReference = firebasedatabase.getReference("users")
            databaseReference.child(currentUser.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        currentUserInfo.id = snapshot.child("id").value as? String ?: ""
                        currentUserInfo.name = snapshot.child("name").value as? String ?: ""
                        currentUserInfo.email = snapshot.child("email").value as? String ?: ""
                        currentUserInfo.phone = snapshot.child("phone").value as? String ?: ""
                        currentUserInfo.country = snapshot.child("country").value as? String ?: ""
                        currentUserInfo.city = snapshot.child("city").value as? String ?: ""
                        currentUserInfo.imagePath = snapshot.child("imagePath").value as? String ?: ""
                        // After loading details, invoke the callback
                        onDetailsLoaded()
                    } else {
                        Toast.makeText(applicationContext, "User details not found.", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Failed to read user details.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun loadMentors(onDetailsLoaded: () -> Unit) {
        val databaseReference = firebasedatabase.getReference("mentors")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                topMentorList.clear()
                educationMentorList.clear()
                personalGrowthMentorList.clear()
                for (mentorSnapshot in snapshot.children) {
                    val mentor = mentorSnapshot.getValue(Mentor::class.java)?.apply {
                        // Set the mentor ID to the key of the snapshot
                        this.id = mentorSnapshot.key ?: ""
                    }
                    mentor?.let {
                        topMentorList.add(it)
                        when (it.type) {
                            MentorType.Education -> educationMentorList.add(it)
                            MentorType.PersonalGrowth -> personalGrowthMentorList.add(it)
                            else -> ""
                        }
                    }
                }
                onDetailsLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to load mentors: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadReviews(onReviewsLoaded: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Reviews are stored under a 'reviews' node in the database
            // Each review is under a sub-node named after the user's ID
            val databaseReference = firebasedatabase.getReference("reviews").child(currentUser.uid)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reviewList.clear()
                    for (reviewSnapshot in snapshot.children) {
                        val review = reviewSnapshot.getValue(Review::class.java)
                        review?.let {
                            reviewList.add(it)
                        }
                    }
                    onReviewsLoaded()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Failed to load reviews: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadBookedMentors(onBookingsLoaded: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Reviews are stored under a 'reviews' node in the database
            // Each review is under a sub-node named after the user's ID
            val databaseReference = firebasedatabase.getReference("bookings").child(currentUser.uid)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookings.clear()
                    for (bookingSnapshot in snapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let {
                            bookings.add(it)
                        }
                    }
                    onBookingsLoaded()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Failed to load Bookings.: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadUserChats(onChatsLoaded: () -> Unit) {
        val firebase: FirebaseUser = auth.currentUser!!
        val databaseReference: DatabaseReference = firebasedatabase.getReference("users")

        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for(dataSnapShot: DataSnapshot in snapshot.children){
                    val user = dataSnapShot.getValue(UserProfile::class.java)
                    if(user!!.id != firebase.uid){
                        userList.add(user)
                    }
                }
                onChatsLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to Load Chats", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(LOGIN_STATUS_KEY, false) && auth.currentUser != null
    }

}