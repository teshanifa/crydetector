package cc.wthr.crydetector;

import cc.wthr.crydetector.CryDetector.ICryListener;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, ICryListener, OnCompletionListener {
    private Button mButtonCry1;
    private Button mButtonCry2;
    private Button mButtonCry3;
    private Button mButtonCry4;
    private Button mButtonNocry1;
    private Button mButtonNocry2;
    private Button mButtonNocry3;
    private Button mButtonNocry4;
    private Button mButtonMic;
    private CryDetector mCryDetector;
    private int mTotalCries = 0;
    private int mTotalSamples = 0;
	private MediaPlayer mPlayer;
	private Handler mUpdateUIHandler;
	private TextView mHits;
	private TextView mSamples;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mHits = (TextView)findViewById(R.id.hits);
        mSamples = (TextView)findViewById(R.id.samples);
        
        mButtonCry1 = (Button)findViewById(R.id.button_cry1);
        mButtonCry1.setOnClickListener(this);
        mButtonCry2 = (Button)findViewById(R.id.button_cry2);
        mButtonCry2.setOnClickListener(this);
        mButtonCry3 = (Button)findViewById(R.id.button_cry3);
        mButtonCry3.setOnClickListener(this);
        mButtonCry4 = (Button)findViewById(R.id.button_cry4);
        mButtonCry4.setOnClickListener(this);
        mButtonNocry1 = (Button)findViewById(R.id.button_nocry1);
        mButtonNocry1.setOnClickListener(this);
        mButtonNocry2 = (Button)findViewById(R.id.button_nocry2);
        mButtonNocry2.setOnClickListener(this);
        mButtonNocry3 = (Button)findViewById(R.id.button_nocry3);
        mButtonNocry3.setOnClickListener(this);
        mButtonNocry4 = (Button)findViewById(R.id.button_nocry4);
        mButtonNocry4.setOnClickListener(this);
        mButtonMic = (Button)findViewById(R.id.button_mic);
        mButtonMic.setOnClickListener(this);
        
        mCryDetector = new CryDetector();
        mCryDetector.setCryListener(this);
        
        mUpdateUIHandler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		mHits.setText(String.format("%d", mTotalCries));
        		mSamples.setText(String.format("%d", mTotalSamples));
        	}
        };
    }

    public void onClick(View view) {
    	mTotalCries = 0;
    	mTotalSamples = 0;
    	mUpdateUIHandler.sendEmptyMessage(0);
		if(mPlayer != null) {
	    	Log.d("MainActivity", "Release playing session");
			onCompletion(mPlayer);
		}
		
		if(view == mButtonMic) {
			mCryDetector.link(0);
		} else {
			int soundId = R.raw.cry1;
			
			if(view == mButtonCry1) {
				soundId = R.raw.cry1;
			} else if(view == mButtonCry2) {
				soundId = R.raw.cry2;			
			} else if(view == mButtonCry3) {
				soundId = R.raw.cry3;			
			} else if(view == mButtonCry4) {
				soundId = R.raw.cry5;			
			} else if(view == mButtonNocry1) {
				soundId = R.raw.nocry1;			
			} else if(view == mButtonNocry2) {
				soundId = R.raw.nocry2;			
			} else if(view == mButtonNocry3) {
				soundId = R.raw.nocry3;			
			} else if(view == mButtonNocry4) {
				soundId = R.raw.nocry5;			
			}
			
			try { 
				mPlayer = MediaPlayer.create(this, soundId);
				mCryDetector.link(mPlayer.getAudioSessionId());
				mPlayer.setOnCompletionListener(this);				
				mPlayer.start();
			} catch(Throwable e) {
				Log.d("MainActivity", "Link visualizer error", e);	
			}
		}
	}

	public void onCryReceived() {
		Log.d("CRY", "Got cry");
		mTotalCries++;
		mUpdateUIHandler.sendEmptyMessage(0);
	}
	
	public void onSampleReceived() {
		mTotalSamples++;
		mUpdateUIHandler.sendEmptyMessage(0);
	}

	public synchronized void onCompletion(MediaPlayer mp) {
		Log.d("MainActivity", "onCompletion");
		try {
			if(mPlayer != null) {
				mPlayer = null;
				mCryDetector.unlink();
				mp.stop();
				//mp.release();
			}
		} catch(Throwable e) {
			Log.d("MainActivity", "onCompletion", e);
		}
	}
}