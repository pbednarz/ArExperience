package pl.solaris.arexperience.utils;

import android.animation.Animator;

/**
 * Created by pbednarz on 2015-02-12.
 */
public abstract class AnimationEndListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    abstract public void onAnimationEnd(Animator animation);

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
