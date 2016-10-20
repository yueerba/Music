// IMediaPlaybackService.aidl
package com.music;
import com.music.entity.MusicInfo;
// Declare any non-default types here with import statements

interface IMediaPlaybackService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void play(int pos);
    void playById(int id);
    void rePlay();
    boolean isPlaying();
    void pause();
    void stop();
    void pre();
    void next();
    void seekTo(int pos);
    long duration();
    long position();
    long seek(long pos);
    void refreshPlayList(in List<MusicInfo> list);
    List<MusicInfo> getPlayList();
    void removeMusic(in MusicInfo music);
    MusicInfo getCurrentMusic();
    String getTrackName();
    String getAlbumName();
    void exit();
    int getCurrentPos();
}
