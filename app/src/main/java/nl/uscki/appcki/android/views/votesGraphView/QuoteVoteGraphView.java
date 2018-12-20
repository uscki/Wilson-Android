package nl.uscki.appcki.android.views.votesGraphView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import nl.uscki.appcki.android.generated.quotes.Quote;

public class QuoteVoteGraphView extends VotesGraphView<Quote> {
    @Override
    void prepareItem() {

    }

    @Override
    void drawItemBars(Canvas canvas) {

    }

    @Override
    int measure() {
        return 100;
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
