package nl.uscki.appcki.android.views.votesGraphView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.poll.PollOption;

public class PollVotesGraphView extends VotesGraphView<PollOption> {

    /**
     * Paint used to draw the vote graph for this poll option (based on poll option colour)
     */
    private Paint barPaint;

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

    @Override
    void prepareItem() {
        barPaint = new Paint();
        try {
            barPaint.setColor(Color.parseColor(item.getColor().toLowerCase()));
        } catch(Exception e) {
            barPaint.setColor(getResources().getColor(R.color.colorAccent));
        }

        Paint p = new TextPaint();
        p.setTextSize(20);
        p.setAntiAlias(true);
        expectedTextWidth = p.measureText(String.format(Locale.getDefault(), "(%d)", item.getMaxVote()));

        fraction = (float) item.getVoteCount() / (float) item.getMaxVote();
    }

    @Override
    void drawItemBars(Canvas canvas) {
        drawRectangle(0, barWidth, false, barPaint, canvas);
    }

    int measure() {
        barWidth = (int) (fraction * (
                        getEffectiveContentWidth(1) - convertDpToPixel(expectedTextWidth)
        ));

        Log.e(getClass().getSimpleName(), String.format("Calculated bar width for %d votes is %d based on fraction %f", item.getVoteCount(), width, fraction));
        return barWidth;
    }

    public PollVotesGraphView(Context context) {
        super(context);
    }

    public PollVotesGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PollVotesGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
