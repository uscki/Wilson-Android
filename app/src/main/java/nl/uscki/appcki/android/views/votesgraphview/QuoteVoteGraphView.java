package nl.uscki.appcki.android.views.votesgraphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.quotes.Quote;

public class QuoteVoteGraphView extends VotesGraphView<Quote> {

    private int total;
    private int positive;
    private int negative;

    private float fractionPositive;
    private float fractionNegative;

    private int positiveWidth;
    private int negativeWidth;

    private int positiveTextWidth;
    private int negativeTextWidth;

    private Paint paintPositive;
    private Paint paintNegative;
    private TextPaint textPaint;

    @Override
    void prepareItem() {

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(BAR_HEIGHT - 6);
        textPaint.setColor(getResources().getColor(R.color.colorWhite));


        paintPositive = new Paint();
        paintPositive.setColor(getResources().getColor(R.color.colorGreen));

        paintNegative = new Paint();
        paintNegative.setColor(getResources().getColor(R.color.colorRed));

        positive = item.getPositiveVotes();
        negative = item.getNegativeVotes();
        total = positive + negative;

        if(total <= 0) {
            fractionPositive = .5f;
            fractionNegative = .5f;
        } else {
            fractionNegative = negative / (float) total;
            fractionPositive = positive / (float) total;
        }

        positiveTextWidth = (int) textPaint.measureText(Integer.toString(positive));
        negativeTextWidth = (int) textPaint.measureText(Integer.toString(negative));
    }

    @Override
    void drawItemBars(Canvas canvas) {
        int wEffective = getEffectiveContentWidth() - positiveTextWidth - negativeTextWidth - bar_margin - 2* round_bar_size;

        positiveWidth = (int) (fractionPositive * wEffective + positiveTextWidth + round_bar_size);
        negativeWidth = (int) (fractionNegative * wEffective + negativeTextWidth + round_bar_size);

        drawRectangle(0, positiveWidth, true, paintPositive, canvas);
        drawRectangle(positiveWidth + bar_margin, negativeWidth, false, paintNegative, canvas);

        canvas.drawText(
                Integer.toString(positive),
                getPaddingLeft() + round_bar_offset,
                getPaddingTop() + getHeight() + textPaint.getFontMetrics().bottom - (int) convertDpToPixel(8.3333333f),
                textPaint);

        canvas.drawText(
                Integer.toString(negative),
                positiveWidth + bar_margin + negativeWidth - negativeTextWidth + round_bar_offset,
                getPaddingTop() + getHeight() + textPaint.getFontMetrics().bottom - (int) convertDpToPixel(8.333333f),
                textPaint
        );
    }

    @Override
    int measure() {
        return width;
    }

    public QuoteVoteGraphView(Context context) {
        super(context);
    }

    public QuoteVoteGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuoteVoteGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
