#UI_第二弹：拨号盘动画


###最近看到拨号盘的变形动画比较有意思，所以自己动手试试。

##一、需要考虑的问题：
###1、拨号盘可能会有单卡机型，双卡机型，还会有视频通话，多人会话等。要考虑到全部都兼容的情况。
###2、用图片背景变形，有阴影效果的话，图片位置需要微调。（比较繁琐，自己调）
###3、用颜色做背景，颜色渐变，圆角。
###4、局部动画与整体动画。

###二、微信公众号：
**关注微信公众号，获取密码，了解更多。**
**微信公众号：jike_android**

![公众号](https://github.com/wch0620/StatusBar/raw/master/WeiXin/qrcode.jpg)

##三、好啦不多说，直接上图：
![android](https://github.com/wch0620/DialpadAnimation/raw/dial_pad_animation_dev/gif/animation.gif)


##四、动画：
###变形动画：
```
        //step1
        ValueAnimator widthAnimation = ValueAnimator.ofInt(fromWidth, toWidth);
        widthAnimation.setDuration(duration/2);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                deformationLayout(animation, fromWidth, toWidth);
            }
        });
```
###变形：

```
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
```
###动画播放：
可通过AnimatorSet将这些动画组合起来
```
	animatorSet.play(iconAnimation1).with(iconAnimation2).with(iconAnimation3).after(widthAnimation);
```
