package nl.uscki.appcki.android.views.votesGraphView;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * TODO: document your custom view class.
 */
public abstract class VotesGraphView<T extends IWilsonBaseItem> extends View {
    protected final float BAR_HEIGHT = convertDpToPixel(20);

    // TODO remove, only useful for quotes
//    private final int DEFAULT_DIRECTION = 1;
//    private final int DIRECTION_RIGHT = 1;
//    private final int DIRECTION_LEFT = 0;

    private int barColor = Color.RED; // TODO: use a default from R.color...

//    private TextPaint textPaint;
//    private float textWidth;
//    private float textHeight;

//    private Paint barPaint;
//    private RectF roundEnd;
//    private int barSize;

    protected T item;

    // TODO remove in favour of item (next 3)
    private int votes = 0;
    private int votesTotal = 1;
    private String voteStr = "";
    private int direction;
    int width;

//    float minVoteWidth = 0;

//    private boolean drawVoteCount = false;

//    // TODO get rid of this
//    public void setDrawVoteCount(boolean drawVoteCount) {
//        this.drawVoteCount = drawVoteCount;
//    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.VotesGraphView, defStyle, 0);
//
//        barColor = a.getColor(
//                R.styleable.VotesGraphView_barColor,
//                barColor);
//
//        direction = a.getInt(R.styleable.VotesGraphView_direction, DEFAULT_DIRECTION);
//
//        a.recycle();
//
//        textPaint = new TextPaint();
//        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        textPaint.setTextAlign(Paint.Align.LEFT);
//        barPaint = new Paint();
//        barPaint.setColor(barColor);
//
//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        Point size = new Point();
//        wm.getDefaultDisplay().getSize(size);
//        width = size.x - getPaddingLeft() - getPaddingRight();

        if(item != null) prepareItem();

//        if(drawVoteCount) {
////            calculateMinimalVoteSize();
//            invalidateTextPaintAndMeasurements();
//        }
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        this.width = w - getPaddingLeft() - getPaddingRight();
//    }

    abstract void prepareItem();

    abstract void drawItemBars(Canvas canvas);

    abstract int measure();

//    private void calculateMinimalVoteSize() {
//        this.minVoteWidth = textPaint.measureText("0") + round_bar_radius;
//    }

//    private void invalidateTextPaintAndMeasurements() {
//        textPaint.setTextSize(BAR_HEIGHT - 6);
//        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
//        textWidth = textPaint.measureText(voteStr);
//
//        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//        textHeight = fontMetrics.bottom;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawBar(canvas);
        drawItemBars(canvas);
    }

    final int round_bar_offset = (int)convertDpToPixel(6.6666665f);
    final int round_bar_radius = (int)convertDpToPixel(6.6666665f);
    final int round_bar_size = (int)convertDpToPixel(13.333333f);
    final int bar_margin = (int)convertDpToPixel(1.666666f);


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


//    private void drawBar(Canvas canvas) {
//        RectF rect;
//
//        int contentHeight = getHeight() - paddingTop - paddingBottom;
//        if(direction == DIRECTION_LEFT) {
//            int contentWidth = barSize + paddingLeft - (int)convertDpToPixel(1.666666f);
//            int x = paddingLeft + round_bar_offset;
//            rect = new RectF(x, 0, contentWidth, BAR_HEIGHT);
//            canvas.drawRect(rect, barPaint);
//
//            roundEnd = new RectF(paddingLeft, 0, paddingLeft + round_bar_size, BAR_HEIGHT);
//            canvas.drawRoundRect(roundEnd, round_bar_radius, round_bar_radius, barPaint);
//
//            if(drawVoteCount) {
//                canvas.drawText(voteStr,
//                        paddingLeft + textWidth,
//                        paddingTop + contentHeight + textHeight - (int)convertDpToPixel(8.333333f),
//                        textPaint);
//            }
//        } else if(direction == DIRECTION_RIGHT) {
//            int contentWidth = barSize - paddingRight;
//            int x = (int)convertDpToPixel(1.666666f);
//            rect = new RectF(x, 0, contentWidth - round_bar_offset, BAR_HEIGHT);
//            canvas.drawRect(rect, barPaint);
//
//            roundEnd = new RectF(contentWidth - round_bar_size, 0, contentWidth, BAR_HEIGHT);
//            canvas.drawRoundRect(roundEnd, round_bar_radius, round_bar_radius, barPaint);
//
//            if(drawVoteCount) {
//                canvas.drawText(
//                        voteStr,
//                        contentWidth - textWidth - round_bar_offset,
//                        paddingTop + contentHeight + textHeight - (int)convertDpToPixel(8.333333f),
//                        textPaint);
//            }
//        }
//    }

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

        if(targetWidth < finalWidth) {
            Log.e(getClass().getSimpleName(),
                    String.format(
                            "Requested size of %d but can only set %d. Content may be cut off",
                            targetWidth, finalWidth));
        }

        setMeasuredDimension(finalWidth, finalHeight);
    }


//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        float textMargin = getResources().getDimension(R.dimen.text_margin);
//        int maxWidth = drawVoteCount ? width : width - (int) convertDpToPixel(6.6666665f + textMargin + textWidth);
//        // TODO figure out interesting ways to:
//        // 1) Make sure both the vote count and rounded corner are shown on the opposite bar, if the
//        //          current bar has 100% of the votes
//        // 2) Actually, mainly that I suppose?
//        if(votes == 0) {
//            barSize = (int)convertDpToPixel(6.6666665f + textWidth);
//        } else if (votes == votesTotal) {
//            barSize = maxWidth - (int)convertDpToPixel(6.6666665f);
//        } else {
//            float percent = ((float) votes / (float) votesTotal) * 100;
//            barSize = (maxWidth * (int)percent) / 100 - (int)convertDpToPixel(textWidth);
//        }
//
//        //MUST CALL THIS
//        setMeasuredDimension(barSize, (int)BAR_HEIGHT);
//    }
//
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
//
//
//    // TODO remove in favour of setVoteObject
//    public void setVotes(int votes) {
//        this.votes = votes;
//        voteStr = Integer.toString(votes);
//        invalidateTextPaintAndMeasurements();
//    }
//
//    // TODO why is this not used?
//    public void setVotesAnimated(int votes) {
//        setVotes(votes);
//        requestLayout();
//        invalidate();
//    }


//    // TODO move to poll (not required for quotes anymore)
//    public void setVotesTotal(int votesTotal) {
//        this.votesTotal = votesTotal;
//    }
//
//
//    // TODO extract from T
//    public void setBarColor(int barColor) {
//        this.barColor = barColor;
//        barPaint.setColor(this.barColor);
//        postInvalidate();
//    }


    /**
     * Constructors
     *
     * TODO do we need all of them? Looks a bit overkill
     * @param context
     */
    public VotesGraphView(Context context) {
        super(context);
        init(null, 0);
    }

    public VotesGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VotesGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
}
