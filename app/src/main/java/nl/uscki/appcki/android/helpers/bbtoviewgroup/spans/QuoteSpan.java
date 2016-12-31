package nl.uscki.appcki.android.helpers.bbtoviewgroup.spans;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

/**
 * Created by peter on 12/30/16.
 */

public class QuoteSpan implements UpdateAppearance, UpdateLayout, LeadingMarginSpan {
    private static final int STRIPE_WIDTH = 5;
    private static final int GAP_WIDTH = 10;

    private int mColor = App.getContext().getColor(R.color.colorAccent);

    public int getLeadingMargin(boolean first) {
        return STRIPE_WIDTH + GAP_WIDTH;
    }
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();
        p.setStyle(Paint.Style.FILL);
        p.setColor(mColor);
        c.drawRect(x, top, x + dir * STRIPE_WIDTH, bottom, p);
        p.setStyle(style);
        p.setColor(color);
    }
}
