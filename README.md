# **Music Search and Playlist Manager App**

## **Overview**

This application provides users with a seamless experience to search for music tracks and albums, view detailed information about them, and manage custom playlists. The app leverages the Deezer API for fetching music data and incorporates robust caching and persistence strategies to enhance user experience.

### **Key Features**

1. **Search Functionality:**
   - Search for tracks or albums using keywords.
   - View search results in a grid layout with album covers and track details.

2. **Detailed View:**
   - Get detailed information about tracks and albums, including contributors, release dates, genres, and more.
   - Play preview clips of tracks.

3. **Playlist Management:**
   - Create, view, and manage custom playlists.
   - Add tracks to playlists directly from the search results.

4. **Intuitive Navigation:**
   - Easily switch between Search and Playlist sections using a bottom navigation bar.

5. **Media Integration:**
   - Glide library for optimized image loading and caching.
   - MediaPlayer for audio playback of track previews.

### **Illustrative Screenshots**

*   **Search for Music:** Users can search for tracks and albums using the search screen.

    <img src="./screenshots/search_screen_track.jpg" alt="Search Screen (Track)" width="300">
    <img src="./screenshots/search_screen_album.jpg" alt="Search Screen (Album)" width="300">

*   **View Album and Track Details:** Detailed information about albums and tracks can be viewed on separate detail screens.

    <img src="./screenshots/album_details.jpg" alt="Album Details" width="300">
    <img src="./screenshots/track_details.jpg" alt="Track Details" width="300">

*   **Create and Manage Playlists:** Users can create custom playlists and add/remove tracks to/from them.

    <img src="./screenshots/playlist_screen.jpg" alt="Playlist Screen" width="300">
    <img src="./screenshots/playlist_details.jpg" alt="Playlist Details" width="300">
    <img src="./screenshots/add_playlist_from_search.jpg" alt="Add Track to Playlist" width="300">


*   **Audio Preview:** You can play preview of the music track
   
*  **Remove Playlists**:  You can remove playlist from playlist screen

---

## **Technical Architecture**

### **Architecture Overview**

The app follows the **Model-View-ViewModel (MVVM)** architecture to ensure separation of concerns, improved testability, and maintainability.

### **Architecture Diagram**

```mermaid
graph TD

  %% UI Layer
  subgraph "UI Layer"
    MainActivity -->|Navigates to| SearchFragment
    MainActivity -->|Navigates to| PlaylistFragment
    MainActivity -->|Navigates to| QuizFragment

    SearchFragment -->|Opens Album Details| AlbumDetailsFragment
    SearchFragment -->|Opens Track Details| TrackDetailsFragment
    PlaylistFragment -->|Opens Playlist Details| PlaylistDetailsFragment
    QuizFragment -->|Opens Quiz Details| QuizDetailsFragment
    QuizDetailsFragment -->|Starts Quiz Play| QuizPlayFragment
  end

  %% ViewModel Layer
  subgraph "ViewModel Layer"
    SearchViewModel
    PlaylistViewModel
    QuizViewModel
  end

  %% Repository Layer
  subgraph "Repository Layer"
    SearchRepository
    PlaylistRepository
    QuizRepository
  end

  %% Data Sources
  subgraph "Data Sources"
    DeezerApi
    TrackTable
    AlbumTable
    PlaylistTable
    QuizTable
    QuizQuestionTable
  end

  %% UI Layer to ViewModel Layer
  SearchFragment -->|Submits Search Query| SearchViewModel
  PlaylistFragment -->|Requests Playlist Data| PlaylistViewModel
  QuizFragment -->|Requests Quiz Data| QuizViewModel

  AlbumDetailsFragment -->|Displays Album Details| SearchViewModel
  TrackDetailsFragment -->|Displays Track Details| SearchViewModel
  PlaylistDetailsFragment -->|Displays Playlist Details| PlaylistViewModel
  QuizDetailsFragment -->|Displays Quiz Details| QuizViewModel
  QuizPlayFragment -->|Fetches Quiz Questions| QuizViewModel

  %% ViewModel Layer to Repository Layer
  SearchViewModel -->|Executes Search Operations| SearchRepository
  SearchViewModel -->|Retrieves Track/Album Details| SearchRepository
  PlaylistViewModel -->|Fetches Playlists| PlaylistRepository
  PlaylistViewModel -->|Manages Track Associations| PlaylistRepository
  QuizViewModel -->|Fetches Quizzes| QuizRepository
  QuizViewModel -->|Retrieves Quiz Questions| QuizRepository
  QuizViewModel -->|Retrieves Playlist Data for Quizzes| PlaylistRepository

  %% Repository Layer to Data Sources
  SearchRepository -->|Checks Local Data| TrackTable
  SearchRepository -->|Checks Local Data| AlbumTable
  SearchRepository -->|Fetches Remote Data| DeezerApi
  SearchRepository -->|Saves Data Locally| TrackTable
  SearchRepository -->|Saves Data Locally| AlbumTable

  PlaylistRepository -->|Retrieves Playlist Data| PlaylistTable
  PlaylistRepository -->|Retrieves Track Information| SearchRepository

  QuizRepository -->|Retrieves Quiz Data| QuizTable
  QuizRepository -->|Retrieves Quiz Questions| QuizQuestionTable
  QuizRepository -->|Retrieves Track Data for Questions| SearchRepository
  QuizRepository -->|Retrieves Associated Playlist| PlaylistRepository

  %% Responsibilities
  SearchViewModel -->|Search Tracks/Albums| SearchRepository
  SearchViewModel -->|Retrieve Track/Album Details| SearchRepository

  PlaylistViewModel -->|Create/Update/Delete Playlists| PlaylistRepository
  PlaylistViewModel -->|Associate/Remove Tracks| PlaylistRepository

  QuizViewModel -->|Create/Delete Quizzes| QuizRepository
  QuizViewModel -->|Manage Question Associations| QuizRepository

 

```

### **Implementation Choices**

#### **MVVM Pattern**
- **View Layer:** Contains fragments for search, album details, and playlist management. Each fragment interacts with the ViewModel to display data and handle user interactions.
- **ViewModel Layer:** Acts as the logic holder for fetching and managing data. Uses `LiveData` to observe and propagate changes to the UI. Coroutine scopes are utilized for async operations.
- **Repository Layer:** Manages data sources. It checks for cached data in Room first and fetches from the Deezer API using Retrofit if needed.

#### **Caching Mechanism**
- Images are cached using the Glide library, optimizing performance and reducing network calls.
- Tracks, albums, and playlists are stored in a Room database to support offline access and persistent user data.

#### **Recent Searches**
- Recent searches are saved in Room with timestamps to allow users to revisit previous queries.

#### **Retrofit Integration**
- Retrofit and Gson are used for making and parsing network requests to the Deezer API. HTTP logging is added for easier debugging of API calls.

---

## **Unresolved Technical Challenges**

1. **Error Handling:**
   - While basic error handling exists, some edge cases (e.g., network timeouts or partial API responses) need more comprehensive handling.

2. **Playlist Details:**
   - Clicking on a playlist currently shows a placeholder action. The feature to display playlist details and manage tracks within a playlist remains to be implemented.

3. **Search Performance Optimization:**
   - For large datasets, the search and result rendering process can be slow. Implementing pagination or on-demand data loading might improve performance.

4. **Audio Playback Issues:**
   - Playback of track previews occasionally fails due to unsupported formats or network errors. A fallback mechanism is required to improve user experience.

5. **Dark Mode Support:**
   - The app does not yet have a dedicated dark mode. Adding this feature would enhance usability and aesthetics for users preferring darker themes.

---
### **Contributors and Contributions**

- **Yusif Askari**:
  - Implemented the **Repository Layer**, managing interactions with data sources (local and remote).
  - Designed and integrated the **caching mechanism** using Room for offline support and Glide for image optimization.
  - Set up the **Room database** for storing tracks, albums, playlists, and recent searches.

- **Ismayil Abdullazada**:
  - Developed the **ViewModel Layer**, ensuring smooth data flow between the UI and repositories.
  - Built the **Fragment-based UI Layer**, implementing the navigation and user interface for search, playlists, and quiz management.
  - Integrated **LiveData** and ViewModel-based architecture for seamless state management in the app.



