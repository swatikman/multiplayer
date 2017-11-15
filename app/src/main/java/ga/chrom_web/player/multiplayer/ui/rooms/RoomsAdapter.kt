package ga.chrom_web.player.multiplayer.ui.rooms


import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.data.Room
import ga.chrom_web.player.multiplayer.databinding.ListRoomsBinding
import java.util.ArrayList

class RoomsAdapter : RecyclerView.Adapter<RoomsAdapter.DataBindingViewHolder<ListRoomsBinding>> {

    private var rooms: ArrayList<Room> = ArrayList()
    var clickListener: ((Room) -> Unit)? = null

    constructor() : super() {
    }

    fun addItems(rooms: ArrayList<Room>) {
        val oldSize = rooms.size
        this.rooms.addAll(rooms)
        notifyItemRangeInserted(oldSize, rooms.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<ListRoomsBinding> {
        val binding = DataBindingUtil.inflate<ListRoomsBinding>(LayoutInflater.from(parent.context),
                R.layout.list_rooms, parent, false)
        return DataBindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<ListRoomsBinding>, position: Int) {
        holder.binding.room = rooms[position]
        holder.binding.root.setOnClickListener {
            clickListener?.invoke(rooms[position])
        }
    }

    override fun getItemCount() = rooms.size

    class DataBindingViewHolder<T : ViewDataBinding> : RecyclerView.ViewHolder {
        var binding: T

        constructor(binding: T) : super(binding.root) {
            this.binding = binding
        }
    }
}
