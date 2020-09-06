package com.grimace.metronomeamplified.state

class AppState {

    var mSong: Song? = null
        private set

    var mBpm: Int = 108
        private set

    var mSectionNumber: Int = 0
        private set

    var mRepeatOfSection: Int = 0
        private set

    var mSectionProgress: Float = 0.0f
        private set

    fun loadSong(song: Song) {
        mSong = song
        mBpm = 108
        mSectionNumber = 0
        mRepeatOfSection = 0
        mSectionProgress = 0.0f
    }
}