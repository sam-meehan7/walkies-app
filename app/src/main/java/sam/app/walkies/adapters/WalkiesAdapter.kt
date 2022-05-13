package sam.app.walkies.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sam.app.walkies.databinding.WalkiesLocationCardBinding
import sam.app.walkies.models.WalkiesLocationModel

interface walkiesLocationListener {
    fun onwalkiesLocationClick(walkiesLocation: WalkiesLocationModel)
}

class walkiesLocationAdapter constructor(private var walkiesLocations: List<WalkiesLocationModel>,
                                         private val listener: walkiesLocationListener
) :
        RecyclerView.Adapter<walkiesLocationAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = WalkiesLocationCardBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val walkiesLocation = walkiesLocations[holder.adapterPosition]
        holder.bind(walkiesLocation, listener)
    }

    override fun getItemCount(): Int = walkiesLocations.size

    class MainHolder(private val binding : WalkiesLocationCardBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(walkiesLocation: WalkiesLocationModel, listener: walkiesLocationListener) {
            binding.walkiesLocationTitle.text = walkiesLocation.title
            binding.description.text = walkiesLocation.description
            binding.difficultyLevel.text = walkiesLocation.difficulty
            Picasso.get().load(walkiesLocation.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onwalkiesLocationClick(walkiesLocation) }
        }
    }
}
