package ie.wit.myworkoutpal.main

import android.app.Application
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import ie.wit.myworkoutpal.models.RoutineModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import ie.wit.myworkoutpal.models.RoutineStore

class MainApp : Application(), AnkoLogger {

    var routines = ArrayList<RoutineModel>()
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var storage: StorageReference
    lateinit var userImage: Uri


    override fun onCreate() {
        super.onCreate()
        info("MyWorkourPal started")
    }
}