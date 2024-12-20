package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.HorizontalTrackAdapter
import com.example.myapplication.data.db.PlaylistTrack
import com.example.myapplication.data.db.PlaylistWithTracks
import com.example.myapplication.data.models.Track
import com.example.myapplication.mvvm.SearchViewModel
import com.example.myapplication.databinding.FragmentPlaylistDetailsBinding
import kotlinx.coroutines.launch

class PlaylistDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistDetailsBinding
    private val viewModel: SearchViewModel by viewModels()
    private var playlistId: Long? = null
    private lateinit var trackAdapter: HorizontalTrackAdapter
    private val trackDetailsMap = mutableMapOf<Long, Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlistId = it.getLong(ARG_PLAYLIST_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observePlaylists()
        observeErrors()
    }

    private fun observeErrors() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }
    private fun setupRecyclerView() {
        trackAdapter = HorizontalTrackAdapter(
            onTrackClicked = { track ->
                showTrackDetails(track)
            }
        )
        binding.trackRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = trackAdapter
        }
    }

    private fun observePlaylists() {
        viewModel.playlists.observe(viewLifecycleOwner){ playlists ->
            val currentPlaylist = playlists.firstOrNull{ it.playlist.id == playlistId}
            currentPlaylist?.let{
                binding.playlistName.text = it.playlist.name
                val tracks = it.tracks
                val tracksWithDetails = tracks.map{ track ->
                    track to trackDetailsMap[track.trackId]
                }
                trackAdapter.submitList(tracksWithDetails)
                fetchTrackDetails(tracks)
            }
        }
        viewModel.getAllPlaylists()

    }
    private fun fetchTrackDetails(tracks:List<PlaylistTrack>){
        for(track in tracks) {
            viewModel.fetchTrack(track.trackId)
            viewModel.track.observe(viewLifecycleOwner){ trackDetails ->
                if(trackDetails != null) {
                    trackDetailsMap[trackDetails.id.toLong()] = trackDetails
                    val tracksWithDetails = tracks.map{ track ->
                        track to trackDetailsMap[track.trackId]
                    }
                    trackAdapter.submitList(tracksWithDetails)
                }
            }

        }

    }
    private fun showTrackDetails(track:Track){
        Toast.makeText(context, "Opening track: ${track.title}", Toast.LENGTH_SHORT).show()
        //TODO: Open track details fragment here
    }
    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: Long) = PlaylistDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_PLAYLIST_ID, playlistId)
            }
        }
    }
}