package com.workstation.view;

import android.animation.Animator;
import android.view.animation.Interpolator;

/**
 * Created by tangyx on 16/1/19.
 */
public interface MaterialMenu {
    /**
     * Change icon without animation
     *
     * @param state new icon state
     */
    void setState(MaterialMenuDrawable.IconState state);

    /**
     * Return current icon state
     *
     * @return icon state
     */
    MaterialMenuDrawable.IconState getState();

    /**
     * Animate icon to given state.
     *
     * @param state new icon state
     */
    void animateState(MaterialMenuDrawable.IconState state);

    /**
     * Animate icon to given state and draw touch circle
     *
     * @param state new icon state
     */
    @Deprecated
    void animatePressedState(MaterialMenuDrawable.IconState state);

    /**
     * Set color of icon
     *
     * @param color new icon color
     */
    void setColor(int color);

    /**
     * Set visibility of icon
     *
     * @param visible new value for visibility
     */
    void setVisible(boolean visible);

    /**
     * Set duration of transformation animations
     *
     * @param duration new animation duration
     */
    void setTransformationDuration(int duration);

    /**
     * Set interpolator for transformation animations
     *
     * @param interpolator new interpolator
     */
    void setInterpolator(Interpolator interpolator);

    /**
     * Set listener for {@code MaterialMenuDrawable} animation events
     *
     * @param listener new listener or null to remove any listener
     */
    void setAnimationListener(Animator.AnimatorListener listener);

    /**
     * Enable RTL layout. Flips all icons horizontally
     *
     * @param rtlEnabled true to enable RTL layout
     */
    void setRTLEnabled(boolean rtlEnabled);

    /**
     *
     * @param animationState state to set value in
     */
    void setTransformationOffset(MaterialMenuDrawable.AnimationState animationState, float value);

    /**
     * @return {@link MaterialMenuDrawable} to be used for the detail
     */
    MaterialMenuDrawable getDrawable();
}
