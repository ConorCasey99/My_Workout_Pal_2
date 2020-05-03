package ie.wit.myworkoutpal.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import ie.wit.myworkoutpal.R
import ie.wit.myworkoutpal.activities.RoutineAdapter
import ie.wit.myworkoutpal.activities.RoutineListener
import ie.wit.myworkoutpal.main.MainApp
import ie.wit.myworkoutpal.models.RoutineModel
import ie.wit.myworkoutpal.helpers.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


open class ReportFragment : Fragment(), AnkoLogger,
    RoutineListener {

    lateinit var app: MainApp
    lateinit var loader : AlertDialog
    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_report, container, false)
        activity?.title = getString(R.string.action_report)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        //var query = FirebaseDatabase.getInstance()
            //.reference
            //.child("user-routines").child(app.auth.currentUser?.uid)

        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = root.recyclerView.adapter as RoutineAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                deleteRoutine((viewHolder.itemView.tag as RoutineModel).uid)
                deleteUserRoutine(app.currentUser!!.uid,
                    (viewHolder.itemView.tag as RoutineModel).uid)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onRoutineClick(viewHolder.itemView.tag as RoutineModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ReportFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    open fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllRoutines(app.currentUser!!.uid)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }

    fun deleteUserRoutine(userId: String, uid: String?) {
        app.database.child("user-routines").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Routine error : ${error.message}")
                    }
                })
    }

    fun deleteRoutine(uid: String?) {
        app.database.child("routines").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Routine error : ${error.message}")
                    }
                })
    }

    override fun onRoutineClick(routine: RoutineModel) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, EditRoutineFragment.newInstance(routine))
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        if(this::class == ReportFragment::class)
        getAllRoutines(app.currentUser!!.uid)
    }

    fun getAllRoutines(userId: String?) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading Routine from Firebase")
        val routinesList = ArrayList<RoutineModel>()
        app.database.child("user-routines").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Routine error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val routine = it.
                        getValue<RoutineModel>(RoutineModel::class.java)

                        routinesList.add(routine!!)
                        root.recyclerView.adapter =
                            RoutineAdapter(routinesList, this@ReportFragment, reportall = false)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("user-routines").child(userId)
                            .removeEventListener(this)
                    }
                }
            })
    }
}