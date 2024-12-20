package com.example.myapplication.mvvm

import android.content.Context
import android.util.Log
import com.example.myapplication.data.models.Album
import com.example.myapplication.data.models.Track
import com.example.myapplication.deezerapi.DeezerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myapplication.data.db.AppDatabase
import com.example.myapplication.data.db.Playlist
import com.example.myapplication.data.db.PlaylistTrack

class SearchRepository(private val context: Context) {
    private val deezerService = DeezerApi.retrofitService
    private val playlistDao = AppDatabase.getDatabase(context).playlistDao()

    suspend fun searchTracks(query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            val response =  deezerService.searchTracks(query)
            response.tracks
        }
    }

    suspend fun searchAlbums(query: String): List<Album> {
        return withContext(Dispatchers.IO) {
            val response = deezerService.searchAlbums(query)
            Log.d("SearchRepository", "searchAlbums: response.albums = ${response.albums}")
            response.albums
        }
    }

    suspend fun getTrack(id: Long): Track {
        return withContext(Dispatchers.IO) {
            deezerService.getTrack(id)
        }
    }

    suspend fun getAlbum(id: Long): Album {
        return withContext(Dispatchers.IO) {
            deezerService.getAlbum(id)
        }
    }

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO){
            playlistDao.insertPlaylistTrack(PlaylistTrack(playlistId = playlistId, trackId = trackId))
        }
    }
    suspend fun insertPlaylist(playlist:Playlist): Long{
        return withContext(Dispatchers.IO){
            playlistDao.insertPlaylist(playlist)
        }
    }
    suspend fun getAllPlaylists() = withContext(Dispatchers.IO){
        playlistDao.getAllPlaylists()
    }
}