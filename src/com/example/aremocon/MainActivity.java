package com.example.aremocon;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.hardware.Pcremocon;

public class MainActivity extends Activity {


    private Button ch1Button;
    private Button ch2Button;
    private Button ch3Button;
    private SharedPreferences pref;
    private ToggleButton recButton;
    private Pcremocon pcremocon;
    
    private static final String TAG = "MainActivity";
    protected static final String KEY_1CH = "1CH";
    protected static final String KEY_2CH = "2CH";
    protected static final String KEY_3CH = "3CH";

    protected static String[] chKeys = {KEY_1CH, KEY_2CH, KEY_3CH}; 
    
    private boolean recSignal(int ch) {

    	String key = chKeys[ch];
	
			byte[] signal = pcremocon.recvSignal();
			String signalStr = convertByteToString(signal);

			SharedPreferences.Editor editor = pref.edit();
	        editor.putString(key, signalStr);
	        editor.commit();
	        
	        if (signal.length == 240)
	        	return true;
	        
	        return false;
    }


    private String convertByteToString(byte[] bytes) {
		StringBuffer strbuf = new StringBuffer(bytes.length * 2);

		for (int index = 0; index < bytes.length; index++) {
			int bt = bytes[index] & 0xff;
			if (bt < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Integer.toHexString(bt));
		}
		return strbuf.toString();
	}


	private boolean sendSignal(int ch) {

    	String key = chKeys[ch];
    	boolean flg = false;
        String signalStr = pref.getString(key, "");
        if (signalStr.length() > 0){
				
				byte[] signal = convertStringToByte(signalStr);
				// 送信chは固定
				flg = pcremocon.sendSignal(1, signal);
        	
        }
        return flg;
    }

    private byte[] convertStringToByte(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int index = 0; index < bytes.length; index++) {
			bytes[index] =
				(byte) Integer.parseInt(
					hex.substring(index * 2, (index + 1) * 2),
					16);
		}
		return bytes;
	}


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        pcremocon = new Pcremocon();
        
        ch1Button = (Button)findViewById(R.id.ch1_button);
        ch2Button = (Button)findViewById(R.id.ch2_button);
        ch3Button = (Button)findViewById(R.id.ch3_button);
        recButton = (ToggleButton)findViewById(R.id.rec_button);
        
        
        ch1Button.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {

                if (recButton.isChecked()){
                	Log.v(TAG, "Rec : 1CH");

                	String msg = "";
                	if (recSignal(0))
                		msg = "(1ch)記録完了";
                	else
                		msg = "(1ch)記録できませんでした";
            		Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();

                } else {
                	Log.v(TAG, "Send : 1CH");
                    if (sendSignal(0))
                    	Toast.makeText(v.getContext(), "(1ch)送信完了", Toast.LENGTH_SHORT).show();
                }
            }
        });
            
        ch2Button.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                if (recButton.isChecked()){
                	Log.v(TAG, "Rec : 2CH");
                	
                	String msg = "";
                	if (recSignal(1))
                		msg = "(2ch)記録完了";
                	else
                		msg = "(2ch)記録できませんでした";
            		Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
            		
                } else {
                	Log.v(TAG, "Send : 2CH");
                    if (sendSignal(1))
                    	Toast.makeText(v.getContext(), "(2ch)送信完了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        

        ch3Button.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                if (recButton.isChecked()){
                	Toast.makeText(v.getContext(), "(3ch)信号を送信してください", Toast.LENGTH_SHORT).show();
                	Log.v(TAG, "Rec : 3CH");
                	String msg = "";
                	if (recSignal(2))
                		msg = "(3ch)記録完了";
                	else
                		msg = "(3ch)記録できませんでした";
            		Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                		
                } else {
                	Log.v(TAG, "Send : 3CH");
                	if (sendSignal(2))
                		Toast.makeText(v.getContext(), "(3ch)送信完了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        Context context = this.getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);

        
    }
    
}