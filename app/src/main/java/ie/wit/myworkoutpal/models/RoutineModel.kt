package ie.wit.myworkoutpal.models


import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoutineModel(var uid: String = "",
                        var routineTitle: String = "N/A",
                        var reps: String = "N/A",
                        var sets: String = "N/A",
                        var email: String? = "joe@bloggs.com") : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "routineTitle" to routineTitle,
            "reps" to reps,
            "sets" to sets,
            "email" to email
        )
    }
}