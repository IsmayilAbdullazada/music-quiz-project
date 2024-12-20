package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.PlaylistAdapter
import com.example.myapplication.R
import com.example.myapplication.data.db.Playlist
import com.example.myapplication.data.db.PlaylistWithTracks
import com.example.myapplication.mvvm.SearchViewModel
import com.example.myapplication.databinding.FragmentPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupAddPlaylistButton()
        observePlaylists()
        observeErrors()
        viewModel.getAllPlaylists()
    }

    private fun observeErrors() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter(
            onItemClicked = { playlist ->
                showPlaylistDetails(playlist.playlist.id)
            }
        )
        binding.playlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
        }
    }
    private fun observePlaylists(){
        viewModel.playlists.observe(viewLifecycleOwner){ playlists ->
            playlistAdapter.submitList(playlists)
        }
    }
    private fun setupAddPlaylistButton(){
        binding.addPlaylistButton.setOnClickListener{
            showAddPlaylistDialog()
        }
    }
    private fun showAddPlaylistDialog(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_playlist, null)
        val playlistNameEditText = dialogView.findViewById<TextInputEditText>(R.id.playlistNameEditText)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Playlist")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val playlistName = playlistNameEditText.text.toString()
                if (playlistName.isNotBlank()) {
                    viewModel.insertPlaylist(Playlist(name = playlistName))
                    viewModel.getAllPlaylists()
                } else {
                    Toast.makeText(context, "Playlist name cannot be empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showPlaylistDetails(playlistId: Long){
        val playlistDetailsFragment = PlaylistDetailsFragment.newInstance(playlistId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, playlistDetailsFragment)
            .addToBackStack(null)
            .commit()
    }
}