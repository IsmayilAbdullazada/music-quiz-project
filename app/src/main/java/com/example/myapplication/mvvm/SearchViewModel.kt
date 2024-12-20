package com.example.myapplication.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.SearchType
import com.example.myapplication.data.db.Playlist
import com.example.myapplication.data.db.PlaylistWithTracks
import com.example.myapplication.data.models.Album
import com.example.myapplication.data.models.Track
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SearchRepository(application)

    private val _searchResults = MutableLiveData<List<Any>>()
    val searchResults: LiveData<List<Any>> = _searchResults

    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track> = _track

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> = _album

    private val _playlists = MutableLiveData<List<PlaylistWithTracks>>()
    val playlists: LiveData<List<PlaylistWithTracks>> = _playlists

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun searchItems(query: String, searchType: SearchType) {
        viewModelScope.launch {
            try {
                val items = when(searchType){
                    SearchType.TRACK -> repository.searchTracks(query)
                    SearchType.ALBUM -> repository.searchAlbums(query)
                    else -> {
                        _error.value = "Unknown search type"
                        emptyList()
                    }
                }
                _searchResults.value = items
            } catch (e: Exception) {
                _error.value = "Error when fetching items: ${e.message}"
            }
        }
    }
    fun fetchTrack(id: Long) {
        viewModelScope.launch {
            try {
                val fetchedTrack = repository.getTrack(id)
                _track.value = fetchedTrack
            } catch (e: Exception) {
                _error.value = "Error when fetching track: ${e.message}"
            }
        }
    }

    fun fetchAlbum(id: Long) {
        viewModelScope.launch {
            try {
                val fetchedAlbum = repository.getAlbum(id)
                _album.value = fetchedAlbum
            } catch (e: Exception) {
                _error.value = "Error when fetching track: ${e.message}"
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long){
        viewModelScope.launch {
            try {
                repository.addTrackToPlaylist(playlistId, trackId)
            } catch (e: Exception){
                _error.value = "Error adding track to playlist: ${e.message}"
            }
        }
    }

    fun insertPlaylist(playlist: Playlist){
        viewModelScope.launch {
            try {
                repository.insertPlaylist(playlist)
            } catch (e: Exception){
                _error.value = "Error when adding playlist: ${e.message}"
            }
        }
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            try {
                val allPlaylists = repository.getAllPlaylists()
                _playlists.value = allPlaylists
            } catch (e: Exception) {
                _error.value = "Error when fetching playlists: ${e.message}"
            }
        }
    }
}