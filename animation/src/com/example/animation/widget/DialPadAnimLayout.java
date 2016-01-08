package com.example.animation.widget;

import java.util.ArrayList;
import java.util.List;

import com.example.animation.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * 2015-12-29 19:14:37
 * @author wujiaohai
 *
 */
public class DialPadAnimLayout extends RelativeLayout {
    
    /**
     * 动画时间
     */
    private int duration = 400;
    
    /**
     * 保存所有子View
     */
    private List<View> mChildView = new ArrayList<View>();
    
    /***
     * 监听器
     */
    private OnAnimationListener mAnimationListener;
    
    /**
     * 正常宽度
     */
    private int mNormalWidth;
    
    /**
     * 变成圆之后的宽度，实际上等于高度
     */
    private int mCircleWidth;
    
    private Drawable mBackgroundDrawable;
    
    private float mCornerRadius;
    
    /**
     * 开始的颜色
     */
    private int mFromColor;
    
    /**
     * 最终颜色
     */
    private int mToColor;
    
    /**
     * 动画已经开启
     */
    private boolean isStartedAnimation = false;
    
    private static final int STATE_EXPAND = 0;
    
    private static final int STATE_HIDE = 1;
    
    /**
     * 状态
     */
    private int mState = STATE_EXPAND;
    
    public DialPadAnimLayout(Context context) {
        this(context, null);
    }
    
    public DialPadAnimLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public DialPadAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.dialpad_animation, 0, 0);
        if (attr == null) {
            return;
        }

        mFromColor = getResources().getColor(R.color.cpb_green);
        mToColor = getResources().getColor(R.color.cpb_blue);
        
        try {
        	mCornerRadius = attr.getDimension(R.styleable.dialpad_animation_cornerRadius, 0);
            mBackgroundDrawable = attr.getDrawable(R.styleable.dialpad_animation_dial_background);
        } finally {
            attr.recycle();
        }
        
        if(mBackgroundDrawable == null) {
        	mBackgroundDrawable = createDrawable();
        }
        
        setBackground(mBackgroundDrawable);
        
    }
    
    public interface OnAnimationListener {
        
        public void onAnimationStart();
        
        public void onAnimationEnd();
        
    }
    
	private Drawable createDrawable() {
		GradientDrawable drawable = (GradientDrawable) getResources()
				.getDrawable(R.drawable.cpb_background).mutate();
		drawable.setColor(getResources().getColor(R.color.cpb_green));
		drawable.setCornerRadius(mCornerRadius);

		return drawable;
	}
    
    
    /***
     * 以下为对外接口
     * @param listener
     */
    public void setOnAnimationListener(OnAnimationListener listener) {
        mAnimationListener = listener;
    }
    
    public void setDuration(int duration) {
    	this.duration = duration;
    }
    
    public int getState() {
    	return mState;
    }
    
    public void expandDialPad() {
    	Log.d("wujiaohai", "isStartedAnimation" + isStartedAnimation);
    	if(!isStartedAnimation)
    		showDialpadAnimation(mCircleWidth, mNormalWidth);
    }
    
    public void hideDialPad() {
    	Log.d("wujiaohai", "isStartedAnimation" + isStartedAnimation);
    	if(!isStartedAnimation)
    		hideDialpadAnimation(mNormalWidth, mCircleWidth);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mNormalWidth == 0) {
            mNormalWidth = getWidth();
            mCircleWidth = getHeight();
        }
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        int count = getChildCount();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                mChildView.add(getChildAt(i));
            }
        }
    }
    
    private void showDialpadAnimation(final int fromWidth, final int toWidth) {
        
    	mState = STATE_EXPAND;
    	
        //step1
        ValueAnimator widthAnimation = ValueAnimator.ofInt(fromWidth, toWidth);
        widthAnimation.setDuration(duration/2);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                deformationLayout(animation, fromWidth, toWidth);
            }
        });
        
        //step2 child alpha
        List<ObjectAnimator> alphaObjectAnimator = new ArrayList<ObjectAnimator>();
		for (int i = 1; i < mChildView.size(); i++) {
			ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(
					mChildView.get(i), "alpha", 0, 1);
			alphaAnimation.setDuration(duration / 4);
			alphaObjectAnimator.add(alphaAnimation);
		}
        
        //step3 translate
        ObjectAnimator translateAnimation1 = null;
        ObjectAnimator translateAnimation2 = null;
        ObjectAnimator scaleAnimation1 = null;
        ObjectAnimator scaleAnimation2 = null;
		if (mChildView != null && mChildView.size() > 2) {
			int width = Math.abs(fromWidth - toWidth) / 2;
			translateAnimation1 = ObjectAnimator.ofFloat(mChildView.get(1),
					"translationX", width, 0);
			translateAnimation1.setDuration(duration / 4);
			translateAnimation2 = ObjectAnimator.ofFloat(
					mChildView.get(mChildView.size() - 1), "translationX",
					-width, 0);
			translateAnimation2.setDuration(duration / 4);
		} else if(mChildView != null && mChildView.size() <= 2) {
        	scaleAnimation1 = ObjectAnimator.ofFloat(mChildView.get(1), "scaleX",
                    0.5f, 1.0f);
        	scaleAnimation1.setDuration(duration/2);
        	scaleAnimation2 = ObjectAnimator.ofFloat(mChildView.get(1), "scaleY",
        			0.5f, 1.0f);
        	scaleAnimation2.setDuration(duration/2);
        }
        
        // step4 icon
        final View icon = mChildView.get(0);
        ObjectAnimator iconAnimation1 = ObjectAnimator.ofFloat(icon, "scaleX",
                1.0f, 0.5f);
        iconAnimation1.setDuration(duration/2);
        ObjectAnimator iconAnimation2 = ObjectAnimator.ofFloat(icon, "scaleY",
                1.0f, 0.5f);
        iconAnimation2.setDuration(duration/2);
        ObjectAnimator iconAnimation3 = ObjectAnimator.ofFloat(icon, "alpha",
                1.0f, 0.0f);
        iconAnimation3.setDuration(duration/2);
        iconAnimation1.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator arg0) {
            	
            }
            
            @Override
            public void onAnimationRepeat(Animator arg0) {
                
            }
            
            @Override
            public void onAnimationEnd(Animator arg0) {
            	icon.setVisibility(View.GONE);
            }
            
            @Override
            public void onAnimationCancel(Animator arg0) {
                
            }
        });
        
        // step 5
        ObjectAnimator bgColorAnimation = ObjectAnimator.ofInt(
                mBackgroundDrawable, "color", mToColor, mFromColor);
        bgColorAnimation.setEvaluator(new ArgbEvaluator());
        bgColorAnimation.setDuration(duration);
        bgColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				mBackgroundDrawable.setColorFilter(value, android.graphics.PorterDuff.Mode.SRC_IN);
			}
		});
        
        ObjectAnimator parentAnimation = ObjectAnimator.ofFloat(this,
                "translationX", 400, 0);
        parentAnimation.setDuration(duration);

        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(parentAnimation).with(bgColorAnimation).with(iconAnimation1)
        	.with(iconAnimation2).with(iconAnimation3);
        animatorSet.play(widthAnimation).after(iconAnimation1);
		if (translateAnimation1 != null) {
			animatorSet.play(translateAnimation1).after(iconAnimation1);
		}
		if (translateAnimation2 != null) {
			animatorSet.play(translateAnimation2).after(iconAnimation1);
		}
		if (scaleAnimation1 != null) {
			animatorSet.play(scaleAnimation1).after(iconAnimation1);
		}
		if (scaleAnimation2 != null) {
			animatorSet.play(scaleAnimation2).after(iconAnimation1);
		}
        for (ObjectAnimator animation : alphaObjectAnimator) {
			if (translateAnimation1 != null) {
				animatorSet.play(animation).after(translateAnimation1);
			} else {
				animation.setDuration(duration / 2);
				animatorSet.play(animation).after(iconAnimation1);
			}
        }
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new onAnimatorListener());
        animatorSet.start();
        
    }
    
    private class onAnimatorListener implements AnimatorListener {

        @Override
        public void onAnimationCancel(Animator arg0) {
            
        }

        @Override
        public void onAnimationEnd(Animator arg0) {
        	isStartedAnimation = false;
            if (mAnimationListener != null) {
                mAnimationListener.onAnimationEnd();
            }
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {
            
        }

        @Override
        public void onAnimationStart(Animator arg0) {
        	isStartedAnimation = true;
            if (mAnimationListener != null) {
                mAnimationListener.onAnimationStart();
            }
        }
        
    }
    
    /***
     * 变形
     */
    private void deformationLayout(ValueAnimator animation, int fromWidth, int toWidth) {
        int leftOffset;
        int rightOffset;
        int targetWidth;
        int targetHeight;
        if (fromWidth > toWidth) {
            targetWidth = fromWidth;
            targetHeight = toWidth;
        } else {
            targetWidth = toWidth;
            targetHeight = fromWidth;
        }

        int value = (Integer) animation.getAnimatedValue();
        leftOffset = (targetWidth - value) / 2;
        rightOffset = targetWidth - leftOffset;
        
        mBackgroundDrawable.setBounds(leftOffset, 0, rightOffset, targetHeight);
        mBackgroundDrawable.invalidateSelf();

    }
    
    private void hideDialpadAnimation(final int fromWidth, final int toWidth) {
        
    	mState = STATE_HIDE;
    	
        //step1
        ValueAnimator widthAnimation = ValueAnimator.ofInt(fromWidth, toWidth);
        widthAnimation.setDuration(duration/2);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                deformationLayout(animation, fromWidth, toWidth);
            }
        });
        
        //step2 child alpha
        List<ObjectAnimator> alphaObjectAnimator = new ArrayList<ObjectAnimator>();
        for (int i = 1; i < mChildView.size(); i++) {
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(
                    mChildView.get(i), "alpha", 1, 0);
            alphaAnimation.setDuration(duration / 4);
            alphaObjectAnimator.add(alphaAnimation);
        }
        
        //step3 translate
        ObjectAnimator translateAnimation1 = null;
        ObjectAnimator translateAnimation2 = null;
        ObjectAnimator scaleAnimation1 = null;
        ObjectAnimator scaleAnimation2 = null;
        if (mChildView != null && mChildView.size() > 2) {
            int width = Math.abs(fromWidth - toWidth) / 2;
            translateAnimation1 = ObjectAnimator.ofFloat(
                    mChildView.get(1), "translationX", 0, width);
            translateAnimation1.setDuration(duration/2);
            translateAnimation2 = ObjectAnimator.ofFloat(
                    mChildView.get(mChildView.size() - 1), "translationX", 0,
                    -width);
            translateAnimation2.setDuration(duration/2);
        } else if(mChildView != null && mChildView.size() <= 2) {
        	scaleAnimation1 = ObjectAnimator.ofFloat(mChildView.get(1), "scaleX",
                    1.0f, 0.5f);
        	scaleAnimation1.setDuration(duration/2);
        	scaleAnimation2 = ObjectAnimator.ofFloat(mChildView.get(1), "scaleY",
        			1.0f, 0.5f);
        	scaleAnimation2.setDuration(duration/2);
        }
        
        // step4 icon
        final View icon = mChildView.get(0);
        ObjectAnimator iconAnimation1 = ObjectAnimator.ofFloat(icon, "scaleX",
                0.5f, 1.0f);
        iconAnimation1.setDuration(duration/2);
        ObjectAnimator iconAnimation2 = ObjectAnimator.ofFloat(icon, "scaleY",
                0.5f, 1.0f);
        iconAnimation2.setDuration(duration/2);
        ObjectAnimator iconAnimation3 = ObjectAnimator.ofFloat(icon, "alpha",
                0.0f, 1.0f);
        iconAnimation3.setDuration(duration / 2);
        iconAnimation1.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator arg0) {
                icon.setVisibility(View.VISIBLE);
                icon.setScaleX(0.5f);
                icon.setScaleY(0.5f);
            }
            
            @Override
            public void onAnimationRepeat(Animator arg0) {
                
            }
            
            @Override
            public void onAnimationEnd(Animator arg0) {
                
            }
            
            @Override
            public void onAnimationCancel(Animator arg0) {
                
            }
        });
        
        // step 5
        ObjectAnimator bgColorAnimation = ObjectAnimator.ofInt(
                mBackgroundDrawable, "color", mFromColor, mToColor);
        bgColorAnimation.setEvaluator(new ArgbEvaluator());
        bgColorAnimation.setDuration(duration);
        bgColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				mBackgroundDrawable.setColorFilter(value, android.graphics.PorterDuff.Mode.SRC_IN);
			}
		});
        
        ObjectAnimator parentAnimation = ObjectAnimator.ofFloat(this,
                "translationX", 0, 400);
        parentAnimation.setDuration(duration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(parentAnimation).with(bgColorAnimation).with(widthAnimation);
        for (ObjectAnimator animation : alphaObjectAnimator) {
        	animatorSet.play(animation);
        }
        if(translateAnimation1 != null) {
        	animatorSet.play(translateAnimation1);
        }
        if(translateAnimation2 != null) {
        	animatorSet.play(translateAnimation2);
        }
        if(scaleAnimation1 != null) {
        	animatorSet.play(scaleAnimation1);
        }
        if(scaleAnimation2 != null) {
        	animatorSet.play(scaleAnimation2);
        }
//        animatorSet.play(iconAnimation).after(widthAnimation);
        animatorSet.play(iconAnimation1).with(iconAnimation2).with(iconAnimation3).after(widthAnimation);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        
        animatorSet.addListener(new onAnimatorListener());
        animatorSet.start();
        
    }

}

