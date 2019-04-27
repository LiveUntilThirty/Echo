package com.dota.echo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class Parrot {
    private AudioTrack speaker;
    private AudioRecord mic;

    private volatile boolean isPlaying;

    private int channel = AudioFormat.CHANNEL_IN_STEREO;
    private int simpleSize = AudioFormat.ENCODING_PCM_16BIT;
    private int simpleRate = 48000;
    private int buffer = AudioRecord.getMinBufferSize(simpleRate, channel, simpleSize);
    private int tracker_buffer = AudioTrack.getMinBufferSize(simpleRate, channel, simpleSize);

    public void init() {
        if (speaker == null) {
            speaker = new AudioTrack(AudioManager.STREAM_MUSIC, simpleRate, channel, simpleSize, tracker_buffer, AudioTrack.MODE_STREAM);
            speaker.setStereoVolume(0.7f, 0.7f);
        }
        if (mic == null) {
            mic = new AudioRecord(MediaRecorder.AudioSource.MIC, simpleRate, channel, simpleSize, buffer);
        }
    }

    public void dispose() {
        isPlaying = false;
        speaker.stop();
        speaker.release();
        mic.stop();
        mic.release();
        speaker = null;
        mic = null;
    }

    public void recordAndPlay() {
        stopRecordPlay();
        new RecordPlayThread().start();
    }

    public void stopRecordPlay() {
        isPlaying = false;
    }

    class RecordPlayThread extends Thread {
        @Override
        public void run() {
            if (speaker != null && mic != null) {
                isPlaying = true;
                try {
                    mic.startRecording();
                    speaker.play();
                    byte[] audio_buffer = new byte[buffer];
                    while (isPlaying) {
                        int result = mic.read(audio_buffer, 0, audio_buffer.length);
                        if (result > 0) {
                            byte[] tmp_buffer = new byte[result];
                            System.arraycopy(audio_buffer, 0, tmp_buffer, 0, result);
                            speaker.write(tmp_buffer, 0, tmp_buffer.length);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    speaker.stop();
                    speaker.release();
                    mic.stop();
                    mic.release();
                }
            }
        }
    }
}
