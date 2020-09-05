package com.grimace.metronomeamplified.state

class AppState {

    var mSong: Song? = null
        private set

    var mBpm: Int = 108
        private set

    fun loadSong(song: Song) {
        mSong = song
        mBpm = 108
    }
}