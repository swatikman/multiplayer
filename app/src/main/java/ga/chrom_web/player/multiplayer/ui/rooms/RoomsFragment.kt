package ga.chrom_web.player.multiplayer.ui.rooms


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.data.Room
import ga.chrom_web.player.multiplayer.databinding.FragmentRoomsBinding
import ga.chrom_web.player.multiplayer.ui.player.PlayerActivity


class RoomsFragment : Fragment() {

    private lateinit var viewModel: RoomsFragmentViewModel
    private lateinit var roomsAdapter: RoomsAdapter
    private lateinit var binding: FragmentRoomsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rooms, container, false)

        roomsAdapter = RoomsAdapter()
        roomsAdapter.clickListener = { room ->
            startPlayer(room)
        }
        val layoutManager = LinearLayoutManager(activity)
        binding.rvRooms.layoutManager = layoutManager
        binding.rvRooms.adapter = roomsAdapter
        binding.isError = false
        binding.swiperefresh.setOnRefreshListener {
            // TODO: clear adapter ???
            roomsAdapter.clear()
            viewModel.refresh()
        }
        return binding.root
    }

    private fun startPlayer(room: Room) {
        val intent = Intent(activity, PlayerActivity::class.java)
        intent.putExtra(Room.INTENT_KEY, room)
        startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RoomsFragmentViewModel::class.java)
        viewModel.rooms.observe(this, Observer { rooms ->
            if (rooms != null) {
                roomsAdapter.addItems(rooms)
                binding.isError = false
            } else {
                binding.isError = true
            }
            binding.swiperefresh.isRefreshing = false
        })
    }

}
