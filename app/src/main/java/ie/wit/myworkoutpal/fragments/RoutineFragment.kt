package ie.wit.myworkoutpal.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import ie.wit.myworkoutpal.R
import ie.wit.myworkoutpal.helpers.createLoader
import ie.wit.myworkoutpal.main.MainApp
import ie.wit.myworkoutpal.models.RoutineModel
import ie.wit.myworkoutpal.helpers.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_routine.view.*
import kotlinx.android.synthetic.main.fragment_routine.view.repsText
import kotlinx.android.synthetic.main.fragment_routine.view.routineTitleText
import kotlinx.android.synthetic.main.fragment_routine.view.setsText
import org.jetbrains.anko.support.v4.toast
import java.util.HashMap

class RoutineFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var loader : AlertDialog
   // lateinit var eventListener : ValueEventListener
    var routine = RoutineModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_routine, container, false)
        loader = createLoader(activity!!)
        activity?.title = getString(R.string.action_routine)

        setButtonListener(root)
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RoutineFragment().apply {
                arguments = Bundle().apply {}
            }
    }

     fun setButtonListener(layout: View){

        layout.routineButton.setOnClickListener{

            val routineTitleVal = layout.routineTitleText.text.toString()
            val repsVal = layout.repsText.text.toString()
            val setsVal = layout.setsText.text.toString()

            if(routine.routineTitle.isEmpty())
                toast("Please enter a routine")
            else {
                    writeNewRoutine(RoutineModel( routineTitle = routineTitleVal,
                        reps = repsVal,
                        sets = setsVal,
                        profilepic = app.userImage.toString(),
                        email = app.auth.currentUser?.email))
                }
            }

        }
/*
    override fun onResume() {
        super.onResume()
        if(this::class == ReportFragment::class)
            getAllRoutines(app.auth.currentUser!!.uid)
    }
*/
    override fun onPause() {
        super.onPause()
        if(app.auth.uid != null)
            app.database.child("user-routines")
                .child(app.auth.currentUser!!.uid)
                //.removeEventListener(eventListener)
    }

    fun writeNewRoutine(routine: RoutineModel) {
        // Create new routine at /routines & /routines/$uid
        showLoader(loader, "Adding Routine to Firebase")
        info("Firebase DB Reference : ${app.database}")
        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("routines").push().key
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        routine.uid = key
        val routineValues = routine.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/routines/$key"] = routineValues
        childUpdates["/user-routines/$uid/$key"] = routineValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }
}
