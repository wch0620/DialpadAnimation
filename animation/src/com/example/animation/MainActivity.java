package com.example.animation;

import com.example.animation.widget.DialPadAnimLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private DialPadAnimLayout circularButton0;
	private DialPadAnimLayout circularButton1;
	private DialPadAnimLayout circularButton2;
	private DialPadAnimLayout circularButton3;

	private Button fast;
	
	private Button slow;

	private int duration = 400;
	
    private static final int STATE_EXPAND = 0;
    
    private static final int STATE_HIDE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fast = (Button) findViewById(R.id.fast);
		slow = (Button) findViewById(R.id.slow);
		circularButton0 = (DialPadAnimLayout) findViewById(R.id.circularButton0);
		circularButton1 = (DialPadAnimLayout) findViewById(R.id.circularButton1);
		circularButton2 = (DialPadAnimLayout) findViewById(R.id.circularButton2);
		circularButton3 = (DialPadAnimLayout) findViewById(R.id.circularButton3);

		fast.setOnClickListener(this);
		slow.setOnClickListener(this);
		circularButton0.setOnClickListener(this);
		circularButton1.setOnClickListener(this);
		circularButton2.setOnClickListener(this);
		circularButton3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fast:
			duration = 400;
			break;
		case R.id.slow:
			duration = 4000;
			break;
		case R.id.circularButton0:
			showAnimation(circularButton0);
			break;
		case R.id.circularButton1:
			showAnimation(circularButton1);
			break;
		case R.id.circularButton2:
			showAnimation(circularButton2);
			break;
		case R.id.circularButton3:
			showAnimation(circularButton3);
			break;
		}
	}
	
	private void showAnimation(DialPadAnimLayout dialpad) {
		dialpad.setDuration(duration);
		int state = dialpad.getState();
		if (state == STATE_EXPAND) {
			dialpad.hideDialPad();
		} else if(state == STATE_HIDE){
			dialpad.expandDialPad();
		}
	}
}
