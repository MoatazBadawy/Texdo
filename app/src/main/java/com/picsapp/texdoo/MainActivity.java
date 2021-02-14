package com.picsapp.texdoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, AdapterView.OnItemSelectedListener {

    EditText editTextWriteText;
    Button speakButton;
    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;
    ImageView micButton;
    ImageView scannerButton;
    Spinner spinner;
    static String speed="Normal";
    TextView mTextView;
    UpdateManager mUpdateManager;
    TextView txtFlexibleUpdateProgress;
    // The request code used in ActivityCompat.requestPermissions() and returned in the Activity's onRequestPermissionsResult()
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // make the app support only arabic "Right to left"
        // even if the language of the device on english or others
        ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_LTR);
        StatusBlackIcons();
        StateDayText();
        TextTooSpeech();
        Spinner();
        ScannerButton();
        SpeechRecorder();
        UpdateGooglePlay();
        CallFlexibleUpdate(txtFlexibleUpdateProgress);
        CheckPermissions();
        // count Text in editText
        editTextWriteText = findViewById(R.id.text_write);
        editTextWriteText.addTextChangedListener(EditTextCount);
    }

    /*
     * Other Methods
    */
    private void StatusBlackIcons () {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private void StateDayText() {
        Calendar mCalendar = Calendar.getInstance();
        int timeOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        TextView mWelcomeText = findViewById(R.id.text_welcome);
        if(timeOfDay >= 6 && timeOfDay < 12){
            mWelcomeText.setText(R.string.good_morning); }
        else if(timeOfDay >= 12 && timeOfDay < 16){
            mWelcomeText.setText(R.string.good_afternoon); }
        else if(timeOfDay >= 16 && timeOfDay < 22){
            mWelcomeText.setText(R.string.good_evening); }
        else if(timeOfDay >= 23){
            mWelcomeText.setText(R.string.sleep); }
        else if(timeOfDay >= 0){
            mWelcomeText.setText(R.string.sleep); }
    }
    private void ScannerButton () {
        scannerButton = findViewById(R.id.scanner_button);
        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ScannerActivity.class);
                startActivity(i);
            }
        });
    }


    /*
     * Methods for textTooSpeech button
     */
    private void TextTooSpeech() {
        textToSpeech = new TextToSpeech(this,this);
        speakButton = findViewById(R.id.speech_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeed();
                speakOut();
            }
        });
    }
    private void speakOut() {
        editTextWriteText = findViewById(R.id.text_write);
        String text = editTextWriteText.getText().toString();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        speechRecognizer.stopListening();
    }
    private void setSpeed( ){
        if(speed.equals("0.1x")){
            textToSpeech.setSpeechRate(0.1f);
        }
        if(speed.equals("0.5x")){
            textToSpeech.setSpeechRate(0.5f);
        }
        if(speed.equals("1.0x")){
            textToSpeech.setSpeechRate(0.9f); // default speed 1.0f
        }
        if(speed.equals("1.5x")){
            textToSpeech.setSpeechRate(1.5f);
        }
        if(speed.equals("2.0x")){
            textToSpeech.setSpeechRate(2.0f);
        }

    }
    private void Spinner () {
        spinner = findViewById(R.id.spinner_speed);
        loadSpinnerData();
        spinner.setOnItemSelectedListener(this);
    }
    private void loadSpinnerData() {
        List<String> lables = new ArrayList<>();
        lables.add("0.1x");
        lables.add("0.5x");
        lables.add("1.0x");
        lables.add("1.5x");
        lables.add("2.0x");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(3-1);

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        speed = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","Error");
            } else {
                speakButton.setEnabled(true);
                speakOut();
            }
        }
    }
    private final TextWatcher EditTextCount =new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            mTextView = findViewById(R.id.count_text);
            mTextView.setText(String.valueOf(0+s.length()));
        }
    };


    /*
     * Methods for SpeechRecorder button
    */
    private void SpeechRecorder () {
        micButton = findViewById(R.id.speak_button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Locale.getDefault() if you want to make it display default language
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,  "en-US");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); //this was missing

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_on);

            }

            @Override
            public void onBeginningOfSpeech() {
                editTextWriteText.setText("");
                editTextWriteText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                editTextWriteText.setHint("Tap to enter text ...");
                micButton.setImageResource(R.drawable.ic_mic_off);
            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editTextWriteText.setText(data.get(0));
                editTextWriteText.setHint("Tap to enter text ...");
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_on);
                ArrayList data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String word = (String) data.get(data.size() - 1);
                editTextWriteText.setText(word);

                Log.i("TEST", "partial_results: " + word);
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.ic_mic_on);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    /*
     * Methods for CheckPermissions && UpdateGooglePlay
     */
    public void CheckPermissions () {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void UpdateGooglePlay () {
        // Initialize the Update Manager with the Activity and the Update Mode
        mUpdateManager = UpdateManager.Builder(this).mode(UpdateManagerConstant.FLEXIBLE);
        // Call start() to check for updates and install them
        mUpdateManager.start();

        // Callback from Flexible Update Progress
        // This is only available for Flexible mode
        // Find more from https://developer.android.com/guide/playcore/in-app-updates#monitor_flexible
        txtFlexibleUpdateProgress = findViewById(R.id.txt_flexible_progress);
        mUpdateManager.addFlexibleUpdateDownloadListener(new UpdateManager.FlexibleUpdateDownloadListener() {
            @Override
            public void onDownloadProgress(final long bytesDownloaded, final long totalBytes) {
                txtFlexibleUpdateProgress.setText("Downloading: " + bytesDownloaded + " / " + totalBytes);
            }
        });
    }
    public void CallFlexibleUpdate(View view) {
        // Start a Flexible Update
        mUpdateManager.mode(UpdateManagerConstant.FLEXIBLE).start();
        txtFlexibleUpdateProgress.setVisibility(View.VISIBLE);
    }
}