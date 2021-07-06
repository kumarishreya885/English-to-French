/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.miwok;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
//...............text to speech.................//
    private TextToSpeech mTTS;
    private TextView mEditText;
    private Button mButtonSpeak;
//................text to speech...............//
//...........translate...............//
private EditText inputToTranslate;
    private TextView translatedTv;
    Translate translate;
    /*translate*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);
        TextView numbers = (TextView)findViewById(R.id.numbers);
        numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1= new Intent(MainActivity.this,Numbers.class);
                startActivity(i1);
            }
        });
        //...............text to speech.....................//
        /*pronounciation*/
        mButtonSpeak = findViewById(R.id.button_speak);
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRENCH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mButtonSpeak.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        mEditText = findViewById(R.id.translatedTv);
        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        /*pronounciationi*/

        inputToTranslate = findViewById(R.id.inputToTranslate);
        translatedTv = findViewById(R.id.translatedTv);
        Button translateButton = findViewById(R.id.translateButton);

        translateButton.setOnClickListener(v -> {

            if (checkInternetConnection()) {

                //If there is internet connection, get translate service and start translation:
                getTranslateService();
                translate();

            } else {

                //If not, display "no connection" warning:
                translatedTv.setText(getResources().getString(R.string.no_connection));
            }

        });
    }

    /*pronounciatioin+translate*/
    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {



            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }
    public void translate() {

        //Get input text to be translated:
        String originalText = inputToTranslate.getText().toString();
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("fr"), Translate.TranslateOption.model("base"));
        String translatedText = translation.getTranslatedText();

        //Translated text and original text are set to TextViews:
        translatedTv.setText(translatedText);

    }

    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)

        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }


    //pronounciation.......................
    private void speak() {
        String text = mEditText.getText().toString();
        mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
    }
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
//translate//

//..................text to speech...................//

    public void openColorsList(View view){
        Intent i11= new Intent(this,colors.class);
        startActivity(i11);
    }
    public void openFamilyList(View view){
        Intent i3= new Intent(this,Family.class);
        startActivity(i3);
    }
    public void openPhrasesList(View view) {
        Intent i4 = new Intent(this, Phrases.class);
        startActivity(i4);
    }



}
