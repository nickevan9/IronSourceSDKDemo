package com.ironsource.ironsourcesdkdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;

public class DemoActivity extends Activity implements RewardedVideoListener, InterstitialListener {

    private final String FALLBACK_USER_ID = "userId";
    private Button mVideoButton;

    private Button mInterstitialShowButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        //The integrationHelper is used to validate the integration. Remove the integrationHelper before going live!
        IntegrationHelper.validateIntegration(this);
        initUIElements();

        startIronSourceInitTask();

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                IronSource.loadInterstitial();
            }

        }.start();

        //Network Connectivity Status
        IronSource.shouldTrackNetworkState(this, true);

    }
    @SuppressLint("StaticFieldLeak")
    private void startIronSourceInitTask(){

        // getting advertiser id should be done on a background thread
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return IronSource.getAdvertiserId(DemoActivity.this);
            }

            @Override
            protected void onPostExecute(String advertisingId) {
                if (TextUtils.isEmpty(advertisingId)) {
                    advertisingId = FALLBACK_USER_ID;
                }
                // we're using an advertisingId as the 'userId'
                initIronSource(advertisingId);
            }
        };
        task.execute();
    }

    private void initIronSource(String userId) {
        // Be sure to set a listener to each product that is being initiated
        // set the IronSource rewarded video listener
        IronSource.setRewardedVideoListener(this);
        // set the interstitial listener
        IronSource.setInterstitialListener(this);
        // set the IronSource user id
        IronSource.setUserId(userId);
        // init the IronSource SDK
        IronSource.init(this, "85460dcd");

        updateButtonsState();

    }


    @Override
    protected void onResume() {
        super.onResume();
        // call the IronSource onResume method
        IronSource.onResume(this);
        updateButtonsState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call the IronSource onPause method
        IronSource.onPause(this);
        updateButtonsState();
    }

    /**
     * Handle the button state according to the status of the IronSource producs
     */
    private void updateButtonsState() {
            handleVideoButtonState(IronSource.isRewardedVideoAvailable());
            handleInterstitialShowButtonState(false);
    }

    /**
     * initialize the UI elements of the activity
     */
    private void initUIElements() {
        mVideoButton = findViewById(R.id.rv_button);
        mVideoButton.setOnClickListener(view -> {
                // check if video is available
                if (IronSource.isRewardedVideoAvailable())
                    //show rewarded video
                    IronSource.showRewardedVideo();
        });



        mInterstitialShowButton = findViewById(R.id.is_button_2);
        mInterstitialShowButton.setOnClickListener(view -> {
                // check if interstitial is available
                if (IronSource.isInterstitialReady()) {
                    //show the interstitial
                    IronSource.showInterstitial();
            }
        });

    }


    /**
     * Set the Rewareded Video button state according to the product's state
     *
     * @param available if the video is available
     */
    public void handleVideoButtonState(final boolean available) {
        final String text;
        final int color;
        if (available) {
            color = Color.BLUE;
            text = getResources().getString(R.string.show) + " " + getResources().getString(R.string.rv);
        } else {
            color = Color.BLACK;
            text = getResources().getString(R.string.initializing) + " " + getResources().getString(R.string.rv);
        }
        runOnUiThread(() -> {
            mVideoButton.setTextColor(color);
            mVideoButton.setText(text);
            mVideoButton.setEnabled(available);

        });
    }

    /**
     * Set the Show Interstitial button state according to the product's state
     *
     * @param available if the interstitial is available
     */
    public void handleInterstitialShowButtonState(final boolean available) {
        final int color;
        if (available) {
            color = Color.BLUE;
        } else {
            color = Color.BLACK;
        }
        runOnUiThread(() -> {
            mInterstitialShowButton.setTextColor(color);
            mInterstitialShowButton.setEnabled(available);
        });
    }

    // --------- IronSource Rewarded Video Listener ---------

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
    }

    @Override
    public void onRewardedVideoAvailabilityChanged(boolean b) {
        handleVideoButtonState(b);
    }

    @Override
    public void onRewardedVideoAdStarted() {
    }

    @Override
    public void onRewardedVideoAdEnded() {
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {

    }

    @Override
    public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
    }

    @Override
    public void onRewardedVideoAdClicked(Placement placement) {
        
    }

    // --------- IronSource Interstitial Listener ---------

    @Override
    public void onInterstitialAdClicked() {
    }

    @Override
    public void onInterstitialAdReady() {
        handleInterstitialShowButtonState(true);
    }

    @Override
    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
        handleInterstitialShowButtonState(false);
    }


    @Override
    public void onInterstitialAdOpened() {
    }

    @Override
    public void onInterstitialAdClosed() {
        handleInterstitialShowButtonState(false);
    }

    @Override
    public void onInterstitialAdShowSucceeded() {
    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
        handleInterstitialShowButtonState(false)  ;
    }


}
