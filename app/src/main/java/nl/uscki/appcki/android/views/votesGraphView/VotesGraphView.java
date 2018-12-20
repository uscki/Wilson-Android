package nl.uscki.appcki.android.views.votesGraphView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.poll.PollOption;
import nl.uscki.appcki.android.generated.quotes.Quote;

/**
 * TODO: document your custom view class.
 */
public abstract class VotesGraphView<T extends IWilsonBaseItem> extends View {

    /**
     * Static dimensions
     */
    final float BAR_HEIGHT = convertDpToPixel(20);
    final int round_bar_offset = (int)convertDpToPixel(6.6666665f);
    final int round_bar_radius = (int)convertDpToPixel(6.6666665f);
    final int round_bar_size = (int)convertDpToPixel(13.333333f);
    final int bar_margin = (int)convertDpToPixel(1.666666f);


    /**
     * Item that is drawn using this view
     */
    protected T item;

    /**
     * Last measured full width of this view
     */
    int width;

    private void init() {
        if(item != null) prepareItem();
    }

    abstract void prepareItem();

    abstract void drawItemBars(Canvas canvas);

    abstract int measure();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawItemBars(canvas);
    }

    /**
     * Calculate the effective content width that can be used to draw bars. This
     * is the actual content width, minus all space used for padding.
     */
    protected int getEffectiveContentWidth() {
        return width - getPaddingLeft() - getPaddingRight();
    }

    /**
     * Draw a default bar, starting from <i>start</i>, <i>width</i> wide. Excluding padding but
     * including rounded corners, which are drawn within the range of the bar. It is up to the
     * implementing class to ensure that the bar falls within the effective content width
     *
     * @param start         Start position of the bar
     * @param width         Desired width of the bar, including rounded corner
     * @param roundedLeft   Boolean, true iff the left side of the bar should be rounded, false iff
     *                      the right side should be
     * @param paint         Paint to use for drawing the bar
     */
    protected void drawRectangle(int start, int width, boolean roundedLeft, Paint paint, Canvas canvas) {
        RectF squareBar, roundedCorner;

        start += getPaddingLeft();

        if(roundedLeft) {
            roundedCorner = new RectF(start, 0, start + round_bar_size, BAR_HEIGHT);
            start += round_bar_offset;
            width -= round_bar_offset;
        } else {
            roundedCorner = new RectF(start + width - round_bar_size, 0, start + width, BAR_HEIGHT);
            width -= round_bar_offset;
        }

        squareBar = new RectF(start, 0, start + width, BAR_HEIGHT);
        canvas.drawRoundRect(roundedCorner, round_bar_radius, round_bar_radius, paint);
        canvas.drawRect(squareBar, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int finalWidth, finalHeight;

        int targetWidth = measure();
        int targetHeight = (int) BAR_HEIGHT;

        if(widthSpecMode == MeasureSpec.EXACTLY) {
            finalWidth = this.width;
        } else {
            finalWidth = targetWidth;
            if(widthSpecMode == MeasureSpec.AT_MOST) {
                finalWidth = Math.min(finalWidth, this.width);
            }
        }

        if(heightSpecMode == MeasureSpec.EXACTLY) {
            finalHeight = h;
        } else {
            finalHeight = targetHeight;
            if(heightSpecMode == MeasureSpec.AT_MOST) {
                finalHeight = Math.min(finalHeight, h);
            }
        }

        String itemSTr = "None";
        if(item instanceof PollOption) {
            itemSTr = ((PollOption) item).getName();
        } else if (item instanceof Quote) {
            itemSTr = ((Quote) item).getQuote();
        }

        Log.e(getClass().getSimpleName(), String.format("Measured width for [%s] is %d but setting %d. Mode is %s", itemSTr, targetWidth, finalWidth, MeasureSpec.toString(widthMeasureSpec)));

        if(finalWidth < targetWidth) {
            Log.e(getClass().getSimpleName(),
                    String.format(
                            "Requested size of %d but can only set %d. Content may be cut off",
                            targetWidth, finalWidth));
        }

        setMeasuredDimension(finalWidth, finalHeight);
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    protected float convertDpToPixel(float dp){
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Get a paint for the default accent color
     * @return  Paint
     */
    protected Paint getDefaultPaint() {
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorAccent));
        return p;
    }

    public void setVoteItem(@NonNull T item) {
        this.item = item;
        prepareItem();
    }

    /**
     * Constructors
     * @param context
     */
    public VotesGraphView(Context context) {
        super(context);
        init();
    }

    public VotesGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VotesGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
}
