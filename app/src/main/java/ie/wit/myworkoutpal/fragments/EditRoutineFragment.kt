package ie.wit.myworkoutpal.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog

import ie.wit.myworkoutpal.R
import ie.wit.myworkoutpal.main.MainApp
import ie.wit.myworkoutpal.models.RoutineModel
import ie.wit.myworkoutpal.helpers.createLoader
import ie.wit.myworkoutpal.helpers.hideLoader
import ie.wit.myworkoutpal.helpers.showLoader
import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditRoutineFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editRoutine: RoutineModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            editRoutine = it.getParcelable("editroutine")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(activity!!)

        root.editRoutineTitle.setText(editRoutine!!.routineTitle.toString())
        root.editReps.setText(editRoutine!!.reps.toString())
        root.editSets.setText(editRoutine!!.sets.toString())

        root.editUpdateButton.setOnClickListener {
            showLoader(loader, "Updating Routine on Server...")
            updateRoutineData()
            updateRoutine(editRoutine!!.uid, editRoutine!!)
            updateUserRoutine(app.currentUser!!.uid,
                editRoutine!!.uid, editRoutine!!)
        }

        return root
    }


    companion object {
        @JvmStatic
        fun newInstance(routine: RoutineModel) =
            EditRoutineFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editroutine",routine)
                }
            }
    }

    fun updateRoutineData() {
        editRoutine!!.routineTitle = root.editRoutineTitle.text.toString()
        editRoutine!!.reps = root.editReps.text.toString()
        editRoutine!!.sets = root.editSets.text.toString()
    }

    fun updateUserRoutine(userId: String, uid: String?, routine: RoutineModel) {
        app.database.child("user-routines").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(routine)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, ReportFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Routine error : ${error.message}")
                    }
                })
    }

    fun updateRoutine(uid: String?, routine: RoutineModel) {
        app.database.child("routines").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(routine)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase routine error : ${error.message}")
                    }
                })
    }
}
