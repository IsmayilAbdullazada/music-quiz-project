package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.data.models.Album
import com.example.myapplication.R
import com.example.myapplication.SearchAdapter
import com.example.myapplication.SearchType
import com.example.myapplication.data.db.Playlist
import com.example.myapplication.mvvm.SearchViewModel
import com.example.myapplication.data.models.Track
import com.example.myapplication.databinding.FragmentSearchBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchButton()
        observeSearchResults()
        observeErrors()
    }

    private fun observeErrors() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(
            onItemClicked = { item ->
                showDetails(item)
            },
            onItemLongClicked = { item ->
                showAddPlaylistDialog(item)
            }
        )
        binding.searchRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = searchAdapter
        }
    }


    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val query = binding.searchBar.text.toString()
            if (query.isNotBlank()) {
                val searchType =
                    if (binding.searchTrack.isChecked) SearchType.TRACK else SearchType.ALBUM
                searchForItems(query, searchType)
            } else {
                Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun searchForItems(query: String, searchType: SearchType) {
        viewModel.searchItems(query, searchType)
    }


    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { items ->
            searchAdapter.submitList(items)
        }
    }


    private fun showDetails(item: Any) {
        when (item){
            is Track -> {
                val detailsFragment = TrackDetailsFragment.newInstance(item.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit()
            }
            is Album -> {
                val detailsFragment = AlbumDetailsFragment.newInstance(item.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun showAddPlaylistDialog(item: Any){
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
                    viewModel.playlists.observe(viewLifecycleOwner){ playlists ->
                        val playlist = playlists.firstOrNull{ it.playlist.name == playlistName}
                        if(playlist != null){
                            addItemToPlaylist(item, playlist.playlist.id)
                        }
                    }

                } else {
                    Toast.makeText(context, "Playlist name cannot be empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun addItemToPlaylist(item: Any, playlistId: Long) {
        when (item) {
            is Track -> {
                viewModel.addTrackToPlaylist(playlistId, item.id)
                Toast.makeText(context, "Track added to playlist: ${item.title}", Toast.LENGTH_SHORT).show()
            }
            is Album -> {
                Toast.makeText(context, "Albums cannot be added to playlists", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "Item added to playlist", Toast.LENGTH_SHORT).show()
            }
        }
    }
}