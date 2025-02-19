package nl.uscki.appcki.android.views.votesgraphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.captioncontest.Caption;

public class CaptionVotesGraphView extends VotesGraphView<Caption> {

    public CaptionVotesGraphView(Context context) {
        super(context);
    }

    public CaptionVotesGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptionVotesGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Margin to the left of the vote bar, between vote bar and vote string
     */
    private final int VOTE_STRING_MARGIN = (int) convertDpToPixel(6);

    /**
     * Paint used to draw the vote graph for this poll option (based on poll option colour)
     */
    private Paint barPaint;

    /**
     * Paint used to draw the vote string for this poll option
     */
    private TextPaint textPaint;

    /**
     * The width of the bar for this poll option
     */
    private int barWidth;

    /**
     * The expected width of the text showing the vote count (not drawn in this view, but used
     * for calculating bar width
     */
    private float expectedTextWidth;

    /**
     * The fraction of the total votes this poll option got
     */
    private float fraction;

    /**
     * The string showing the number of votes for this poll option
     */
    private String voteString;

    @Override
    void prepareItem() {
        barPaint = new Paint();
        barPaint.setColor(getDefaultPaint().getColor());
        String colorName = getDefaultPaint().toString();

        textPaint = new TextPaint();
        textPaint.setTextSize(BAR_HEIGHT - 16);
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.colorBlack));
        voteString = String.format(Locale.getDefault(), "(%d)", item.getVotes());
        expectedTextWidth = textPaint.measureText(voteString);

        fraction = (float) item.getVotes() / (float) item.getMaxVotes();
    }

    @Override
    void drawItemBars(Canvas canvas) {
        drawRectangle(0, barWidth, false, barPaint, canvas);
        canvas.drawText(
                voteString,
                barWidth + VOTE_STRING_MARGIN,
                getPaddingTop() + getHeight() + textPaint.getFontMetrics().bottom - (int) convertDpToPixel(10),
                textPaint);
    }

    int measure() {
        barWidth = round_bar_size + (int) (
                (getEffectiveContentWidth() - VOTE_STRING_MARGIN - round_bar_size - expectedTextWidth) * fraction
        );

        return width;
    }
}
