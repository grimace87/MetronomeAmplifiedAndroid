#include <algorithm>
#include "songplayer.h"

SongPlayer::SongPlayer() :
    mSectionNumber(0),
    mSectionRepetition(0),
    mSectionProgress(0.0),
    mFileBuffers(),
    mSong(nullptr),
    mSectionReaderHead(0) {
}

void SongPlayer::initialise(AAssetManager* assetManager) {
    mFileBuffers.createBuffers(assetManager);
}

void SongPlayer::release() {
    mFileBuffers.releaseBuffers();
}

void SongPlayer::setSong(Song* song) {
    mSong.reset(song);
}

void SongPlayer::fillWithAudio(int16_t* buffer, std::vector<int16_t>& source, size_t sourceOffset, size_t samples) {
    if (sourceOffset + samples > source.size()) {
        samples = source.size() - sourceOffset;
    }
    for (size_t i = 0; i < samples; i++) {
        buffer[i] = source[sourceOffset + i];
    }
}

void SongPlayer::fillWithSilence(int16_t* buffer, size_t samples) {
    for (size_t i = 0; i < samples; i++) {
        buffer[i] = 0;
    }
}

void SongPlayer::fillFrames(int16_t* buffer, size_t length) {

    if (!mSong) {
        return;
    }
    int64_t samplesToBeConsumed = length;
    while (samplesToBeConsumed > 0) {

        Section& section = mSong->getSection(mSectionNumber);
        int64_t sectionLengthInSamples = section.getLengthInSamples();
        SectionNotePosition notePositionAtHead = section.getNoteSampleOffsetForHeadPosition(mSectionReaderHead);
        int noteIndex = notePositionAtHead.noteIndex;
        int64_t noteOffsetInSamples = notePositionAtHead.noteOffsetInSamples;

        Note &noteAtHead = section.getNote(noteIndex);
        std::vector<int16_t> &audioBuffer = mFileBuffers.getBuffer((int)noteAtHead.getAccentNumber());
        int64_t samplesIntoCurrentNote = mSectionReaderHead - noteOffsetInSamples;
        auto samplesInThisSound = (int64_t)audioBuffer.size();
        int64_t samplesForThisNote = section.getLengthOfNoteInSamples(noteIndex);

        bool isInAudio = samplesIntoCurrentNote < samplesInThisSound;
        if (isInAudio) {
            size_t audioSamples = std::min(
                    noteOffsetInSamples + samplesInThisSound - mSectionReaderHead,
                    samplesToBeConsumed);
            fillWithAudio(buffer + (length - samplesToBeConsumed), audioBuffer, samplesIntoCurrentNote, audioSamples);
            samplesToBeConsumed -= audioSamples;
            mSectionReaderHead += audioSamples;
        } else {
            size_t silentSamples = std::min(
                    noteOffsetInSamples + samplesForThisNote - mSectionReaderHead,
                    samplesToBeConsumed);
            fillWithSilence(buffer + (length - samplesToBeConsumed), silentSamples);
            samplesToBeConsumed -= silentSamples;
            mSectionReaderHead += silentSamples;
        }

        if (mSectionReaderHead >= sectionLengthInSamples) {
            mSectionReaderHead -= sectionLengthInSamples;
            mSectionRepetition++;
            if (mSectionRepetition >= section.getRepetitions()) {
                mSectionRepetition = 0;
                mSectionNumber++;
                if (mSectionNumber >= mSong->getSectionCount()) {
                    mSectionNumber = 0;
                }
            }
        }
    }
}
