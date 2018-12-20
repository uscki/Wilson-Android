package nl.uscki.appcki.android.views.votesGraphView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
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
            getDefaultPaint();
        }

        Paint p = new TextPaint();
        p.setTextSize(getResources().getDimension(R.dimen.lb_basic_card_content_text_size));
        p.setAntiAlias(true);
        expectedTextWidth = p.measureText(String.format(Locale.getDefault(), "(%d)", item.getMaxVote()));

        fraction = (float) item.getVoteCount() / (float) item.getMaxVote();
    }

    @Override
    void drawItemBars(Canvas canvas) {
        drawRectangle(0, barWidth, false, barPaint, canvas);
    }

    int measure() {
        barWidth = round_bar_size + (int) (
                (getEffectiveContentWidth() - round_bar_size - expectedTextWidth) * fraction
            );

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
