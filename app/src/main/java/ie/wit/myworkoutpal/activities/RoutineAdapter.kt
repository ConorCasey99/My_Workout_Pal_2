package ie.wit.myworkoutpal.activities


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.myworkoutpal.R
import ie.wit.myworkoutpal.models.RoutineModel
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_routine.view.*

interface RoutineListener {
    fun onRoutineClick(routine: RoutineModel)
}

class RoutineAdapter constructor(private var routines: ArrayList<RoutineModel>,
                                 private val listener: RoutineListener, reportall: Boolean)
    : RecyclerView.Adapter<RoutineAdapter.MainHolder>() {

    val reportAll = reportall

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_routine,
                parent,
                false
            )
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val routine = routines[holder.adapterPosition]
        holder.bind(routine, listener, reportAll)
    }

    override fun getItemCount(): Int = routines.size

    fun removeAt(position: Int){
        routines.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(routine: RoutineModel,  listener : RoutineListener, reportAll: Boolean) {
            itemView.tag = routine
            itemView.routineTitle.text = routine.routineTitle
            itemView.reps.text = routine.reps
            itemView.sets.text = routine.sets

            if(!reportAll)
                itemView.setOnClickListener { listener.onRoutineClick(routine) }

            if(!routine.profilepic.isEmpty()) {
                Picasso.get().load(routine.profilepic.toUri())
                    //.resize(180, 180)
                    .transform(CropCircleTransformation())
                    .into(itemView.imageIcon)
            }
            else
                itemView.imageIcon.setImageResource(R.mipmap.ic_launcher)

        }
    }
}