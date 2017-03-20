package nl.uscki.appcki.android.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import nl.uscki.appcki.android.R;

/**
 * TODO: document your custom view class.
 */
public class VotesGraphView extends View {
    private final float BAR_HEIGHT = convertDpToPixel(20);
    private final int DEFAULT_DIRECTION = 1;
    private final int DIRECTION_RIGHT = 1;
    private final int DIRECTION_LEFT = 0;

    private int barColor = Color.RED; // TODO: use a default from R.color...

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private Paint barPaint;
    private RectF roundEnd;
    private int barSize;

    private int viewSize;

    private int votes = 0;
    private int votesTotal = 1;
    private String votesStr = "";
    private int direction;
    int width;

    int paddingLeft = getPaddingLeft();
    int paddingTop = getPaddingTop();
    int paddingRight = getPaddingRight();
    int paddingBottom = getPaddingBottom();

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

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.VotesGraphView, defStyle, 0);

        barColor = a.getColor(
                R.styleable.VotesGraphView_barColor,
                barColor);

        direction = a.getInt(R.styleable.VotesGraphView_direction, DEFAULT_DIRECTION);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        barPaint = new Paint();
        barPaint.setColor(barColor);



        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        width = size.x - paddingLeft - paddingRight;

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(BAR_HEIGHT - 6);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        mTextWidth = mTextPaint.measureText(votesStr);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBar(canvas);
    }

    private final int round_bar_offset = 20;
    private final int round_bar_radius = 20;
    private final int round_bar_size = 40;

    private void drawBar(Canvas canvas) {
        RectF rect;

        int contentHeight = getHeight() - paddingTop - paddingBottom;
        if(direction == DIRECTION_LEFT) {
            int contentWidth = barSize + paddingLeft - 5;
            int x = paddingLeft + round_bar_offset;
            rect = new RectF(x, 0, contentWidth, BAR_HEIGHT);
            canvas.drawRect(rect, barPaint);

            roundEnd = new RectF(paddingLeft, 0, paddingLeft + round_bar_size, BAR_HEIGHT);
            canvas.drawRoundRect(roundEnd, round_bar_radius, round_bar_radius, barPaint);

            canvas.drawText(votesStr,
                    paddingLeft + mTextWidth,
                    paddingTop + (contentHeight + mTextHeight) - 25,
                    mTextPaint);
        } else if(direction == DIRECTION_RIGHT) {
            int contentWidth = barSize - paddingRight;
            int x = 5;
            rect = new RectF(x, 0, contentWidth - round_bar_offset, BAR_HEIGHT);
            canvas.drawRect(rect, barPaint);

            roundEnd = new RectF(contentWidth - round_bar_size, 0, contentWidth, BAR_HEIGHT);
            canvas.drawRoundRect(roundEnd, round_bar_radius, round_bar_radius, barPaint);

            canvas.drawText(votesStr,
                    contentWidth - mTextWidth - round_bar_offset,
                    paddingTop + (contentHeight + mTextHeight) - 25,
                    mTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(votes == 0) {
            barSize = 40;
        } else if (votes == votesTotal) {
            barSize = width - 40;
        } else {
            float percent = ((float)votes / (float)votesTotal) * 100;
            barSize = (width * (int)percent) / 100;
        }

        //MUST CALL THIS
        setMeasuredDimension(barSize, (int)BAR_HEIGHT);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    private float convertDpToPixel(float dp){
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
        votesStr = String.format("%d", votes);
        invalidateTextPaintAndMeasurements();
    }

    public int getVotesTotal() {
        return votesTotal;
    }

    public void setVotesTotal(int votesTotal) {
        this.votesTotal = votesTotal;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }
}
