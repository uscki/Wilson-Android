package nl.uscki.appcki.android.views.votesgraphview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * This abstract class is extended when a colored bar, indicating a number (e.g. number of votes)
 * should be shown on the screen. The bar is always of a set height, but this class makes it
 * easier to calculate a percentage of the available width and draw the (vote) bar within that
 * width
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
     * Default color resource can be set in XML
     */
    private int defaultColorResource;

    /**
     * Item that is drawn using this view
     */
    protected T item;

    /**
     * Last measured full width of this view
     */
    int width;


    /**
     * This method is called when the item is set by the calling view. In this function,
     * calculations that depend on the item should be made, e.g. width such that the bar covers
     * a certain percentage of the available width. For available width,
     * see {@link #getEffectiveContentWidth()}
     */
    abstract void prepareItem();

    /**
     * This method is called on a layout update. Using measurements calculated in {@link #prepareItem()},
     * the bar should be drawn on the screen. For a default bar with one rounded corner, use
     * {@link #drawRectangle(int, int, boolean, Paint, Canvas)}
     * @param canvas
     */
    abstract void drawItemBars(Canvas canvas);

    /**
     * Calculate the final width this view element should take. Depending on how this view element
     * is included in the parent, that view will be set to the final view by this abstract class.
     * However, it is not guaranteed this width can be set, so during development, check if this
     * view does what it should
     *
     * @return  Integer for the preferred width of this element (use {@link #width} for default)
     */
    abstract int measure();

    /**
     * Calculate the effective content width that can be used to draw bars. This
     * is the actual content width allocated to this view, excluding paddings. When making
     * calculations, always use this width. If paddings are not taken into account, content may
     * be cropped.
     */
    protected int getEffectiveContentWidth() {
        return width - getPaddingLeft() - getPaddingRight();
    }

    /**
     * Draw a default bar, starting from <i>start</i>, <i>width</i> wide. Excluding padding but
     * including rounded corners, which are drawn within the range of the bar. It is up to the
     * implementing class to ensure that the bar falls within the effective content width (see
     * {@link #getEffectiveContentWidth()})
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

        // Some safeguards for developing
        if(finalWidth < targetWidth) {
            Log.e(getClass().getSimpleName(),
                    String.format(
                            "Requested width (size) of %d but can only set %d. Content may be cut off",
                            targetWidth, finalWidth));
        }

        // Some safeguards for developing (Cont.d)
        if(finalHeight < targetHeight) {
            Log.e(getClass().getSimpleName(),
                    String.format(
                            "Requested height (size) of %d but can only set %d. Content may be cut off",
                            targetHeight, finalWidth));
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
        p.setColor(this.defaultColorResource);
        return p;
    }

    /**
     * Set the item that contains the numbers used to visualize a bar
     * @param item  Item
     */
    public void setVoteItem(@NonNull T item) {
        this.item = item;
        prepareItem();
    }


    public VotesGraphView(Context context) {
        super(context);
        this.defaultColorResource = getDefaultColorFromResources(context);
        init();
    }

    public VotesGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
        init();
    }

    public VotesGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttrs(context, attrs);
        init();
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VotesGraphView, 0, 0);
        int defaultColor = getDefaultColorFromResources(context);
        try {
            this.defaultColorResource = ta.getColor(R.styleable.VotesGraphView_barColor, defaultColor);
        } finally {
            ta.recycle();
        }
    }

    private int getDefaultColorFromResources(Context context) {
        int defaultColor;
        if  (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            defaultColor = context.getColor(R.color.colorPrimary);
        } else {
            defaultColor = context.getResources().getColor(R.color.colorPrimary);
        }
        return defaultColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawItemBars(canvas);
    }

    private void init() {
        if(item != null) prepareItem();
    }
}
