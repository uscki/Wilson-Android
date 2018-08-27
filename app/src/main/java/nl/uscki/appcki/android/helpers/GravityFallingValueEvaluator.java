package nl.uscki.appcki.android.helpers;

import android.animation.TypeEvaluator;
import android.util.Log;

/**
 * Class for animating an objects location as if taken by gravity. Bounces
 * optional.
 */
public class GravityFallingValueEvaluator implements TypeEvaluator<Float>{

    private int bounces = 1;
    private int halfTimes = 1;

    private float halftimeFractionLength = 1f;

    /**
     * Set the number of times this item should hit the floor. 1 means it does not bounce,
     * since it hits the floor only once
     */
    public void setBounces(int bounces) {
        this.bounces = Math.max(bounces, 1);
        this.halfTimes = ((bounces - 1) * 2) + 1;
        this.halftimeFractionLength = 1f/halfTimes;
    }

    /**
     * Get the height the view should bounce back to. It should always bounce back
     * less than the height it fell from in the first place.
     *
     * @param fraction
     * @param maxOverallHeight
     * @return
     */
    private float getMaxBounceHeight(float fraction, float maxOverallHeight) {
        int currentBounce = fraction > 0 ? (int) Math.ceil(fraction * bounces) : 1;
        return maxOverallHeight / currentBounce;
    }

    @Override
    public Float evaluate(float fraction, Float from, Float to) {
        int currentHalfBounce = fraction > 0 ? (int) Math.ceil(fraction * halfTimes) : 1;

        float height = getMaxBounceHeight(fraction, from);

        float startOfCurrentHalfBounce = (currentHalfBounce - 1f) * halftimeFractionLength;

        float fractionInCurrentHalfBounce = (fraction - startOfCurrentHalfBounce) / halftimeFractionLength;


        float currentHeight;
        if(currentHalfBounce % 2 == 0) {
            //Even means we're bouncing back
            currentHeight = getHeightBouncingTo(fractionInCurrentHalfBounce, height, to);
        } else {
            // Uneven means we're falling
            currentHeight = getHeightFallingFrom(fractionInCurrentHalfBounce, height, to);
        }

        return currentHeight;
    }

    /**
     * Calculate the height of the item given a falling trajectory with acceleration
     * @param fraction          Fraction through the fall
     * @param initialHeight     Height item was dropped from
     * @param bounceAt          Height item stops with falling, as though hitting something
     * @return Float giving current item height given trajectory and fraction
     */
    private float getHeightFallingFrom(float fraction, float initialHeight, float bounceAt) {
        if(fraction < 0 || fraction > 1) throw new IllegalArgumentException("Fraction has to be between 0 and 1 but was " + fraction);
        if(initialHeight < bounceAt) throw new IllegalArgumentException("Initial height has to be larger than bounce height");

        return initialHeight - (float) Math.sqrt(fraction * Math.pow(initialHeight - bounceAt, 2));
    }

    /**
     *
     * @param fraction          Fraction through the bounce
     * @param maxHeight         Maximum height item can bounce to
     * @param bouncedFrom       Height of surface item bounced off of
     * @return Float giving current item height given trajectory and fraction
     */
    private float getHeightBouncingTo(float fraction, float maxHeight, float bouncedFrom) {
        if(fraction < 0 || fraction > 1) throw new IllegalArgumentException("Fraction has to be between 0 and 1 but was " + fraction);
        if(maxHeight < bouncedFrom) throw new IllegalArgumentException("Maximum height has to be larger than bounced from height");

        return bouncedFrom + (float)Math.sqrt(fraction * Math.pow(maxHeight - bouncedFrom, 2));
    }
}
