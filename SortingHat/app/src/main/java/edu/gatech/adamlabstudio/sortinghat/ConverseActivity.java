package edu.gatech.adamlabstudio.sortinghat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConverseActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int FLAG_DEFAULT = 28;

    private boolean mToggleMute = false;

    private Map<String,String> toPlay = new HashMap<String,String>();
    private UserSession mSession;
    private ConversationEngine mAnalyzer;
    private MediaPlayer mPlayer;

    //Views
    private Button mMuteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converse);

        //initialize mute/unmute button
        mMuteButton = (Button) findViewById(R.id.toggle_mute_btn);
        mMuteButton.setText("MUTE");

        mAnalyzer = new ConversationEngine();

        mSession = new UserSession();

        //media player initializations to play speech snippets
        mPlayer = new MediaPlayer();
        mPlayer.setLooping(false);

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                player.start();
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                player.reset();
            }
        });
    }

    public void recordUser(View v) {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, FLAG_DEFAULT);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_SHOW_UI);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    TextView mTextView = (TextView) findViewById(R.id.user_says);
                    mTextView.setText(result.get(0));
                    analyzeText(result.get(0));
                }
                break;
            }
        }
    }

    private void playMediaFile(Uri uri) {
        try {
            mPlayer.setDataSource(getApplicationContext(),uri);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void startNewUser(View v) {
        mSession.reset();
        Uri uri1= Uri.parse("android.resource://"+getPackageName()+"/raw/welcome");
        playMediaFile(uri1);
    }

    public void toggleMute(View v) {
        if (mToggleMute) {
            mMuteButton.setText(R.string.mute);
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_SHOW_UI);
            mToggleMute = false;
        } else {
            mMuteButton.setText(R.string.unmute);
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
            mToggleMute = true;
        }
    }

    private void analyzeText(String result) {

        int category = mAnalyzer.getCategory(result, mSession);

        Uri uri;
        switch(category) {
//            case ConversationEngine.WHICH_HOUSE:
//                uri= Uri.parse("android.resource://"+getPackageName()+"/raw/do_you_know_house");
//                break;
//            case ConversationEngine.RIGHT_THEN:
//                uri= Uri.parse("android.resource://"+getPackageName()+"/raw/right_then");
//                break;
            case ConversationEngine.WHICH_HOUSE_YES:
            uri= Uri.parse("android.resource://"+getPackageName()+"/raw/which_house");
            break;
            case ConversationEngine.WHY_HOUSE:
            uri= Uri.parse("android.resource://"+getPackageName()+"/raw/why_do_you_think");
            break;
            case ConversationEngine.GRYFFINDOR:
                uri= Uri.parse("android.resource://"+getPackageName()+"/raw/right_gryffindor");
                break;
            case ConversationEngine.RAVENCLAW:
                uri= Uri.parse("android.resource://"+getPackageName()+"/raw/ravenclaw");
                break;
            case ConversationEngine.SLYTHERIN:
                uri= Uri.parse("android.resource://"+getPackageName()+"/raw/sort_slytherin");
                break;
            case ConversationEngine.HUFFLEPUFF:
                uri= Uri.parse("android.resource://"+getPackageName()+"/raw/hufflepuff");
                break;
            case ConversationEngine.WHICH_HOUSE_NO:
            case ConversationEngine.HELP_MEMORY:
            default:
                uri= Uri.parse("android.resource://"+getPackageName()+"/raw/tell_memory");
        }

        playMediaFile(uri);
    }
}
