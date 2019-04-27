package com.dota.echo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    private Button mBtnRecordAudio;
    private Button mBtnPlayAudio;
    private Chronometer mChronometerTime;
    boolean isRecording = false;//是否录放的标记
    int frequency = 44100;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int recBufSize, playBufSize;
    AudioRecord audioRecord;
    AudioTrack audioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mBtnRecordAudio = findViewById(R.id.main_btn_record_sound);
        mBtnPlayAudio = findViewById(R.id.main_btn_play_sound);
        mChronometerTime = findViewById(R.id.record_audio_chronometer_time);
        //这两个值设置小了会导致声音断断续续播放,有杂音的可能性是发声处离录音太近(硬件那边说的),因为我的需求是将录制的声音传送给硬件播放,所以不存在这个问题
        recBufSize = AudioRecord.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding) * 4;
        playBufSize = AudioTrack.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding) * 4;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                channelConfiguration, audioEncoding, recBufSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                channelConfiguration, audioEncoding,
                playBufSize, AudioTrack.MODE_STREAM);

        mBtnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isRecording = true;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        StartRecord();
                        Log.e("tag", "start");
                    }
                });
                thread.start();
            }
        });

        mBtnPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            }
        });
        mChronometerTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer)
            {
                // TODO Auto-generated method stub
                //如果从开始计时到现在超过50秒则停止计时
                if (SystemClock.elapsedRealtime() - mChronometerTime.getBase() > 10 * 1000)
                {
                    //Log.d("onChronometerTick", mRecorder.toString());
                    mChronometerTime.stop();

                }
            }
        });
    }
    public void StartRecord()
    {
        try
        {
            byte[] buffer = new byte[recBufSize];
            audioRecord.startRecording();
            audioTrack.play();

            while (isRecording)
            {
                int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);

                byte[] tmpBuf = new byte[bufferReadResult];
                System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
                audioTrack.write(tmpBuf, 0, tmpBuf.length);
            }
            audioTrack.stop();
            audioRecord.stop();
        }
        catch (Throwable t)
        {
            Toast.makeText(Main.this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
