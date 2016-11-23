package nl.uscki.appcki.android.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.events.LinkClickedEvent;

/**
 * Created by peter on 2/7/16.
 */
public class BBTextView extends TextView {
    public BBTextView(Context context) {
        super(context);
    }

    public BBTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BBTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.setMovementMethod(LinkMovementMethod.getInstance());
        super.setText(Html.fromHtml(text.toString()), type);
        fixTextView();
    }

    private void fixTextView() {
        SpannableString current = (SpannableString) this.getText();
        URLSpan[] spans = current.getSpans(0, current.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = current.getSpanStart(span);
            int end = current.getSpanEnd(span);

            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(span.getURL()), start, end, 0);
        }
    }

    private static class DefensiveURLSpan extends URLSpan {
        public DefensiveURLSpan(String url) {
            super(url);
        }

        public Parcelable.Creator CREATOR = new Parcelable.Creator<DefensiveURLSpan>() {

            @Override
            public DefensiveURLSpan createFromParcel(Parcel source) {
                return new DefensiveURLSpan(source.readString());
            }

            @Override
            public DefensiveURLSpan[] newArray(int size) {
                return new DefensiveURLSpan[size];
            }
        };

        @Override
        public void onClick(View widget) {
            EventBus.getDefault().post(new LinkClickedEvent(getURL()));
            super.onClick(widget);
        }
    }
}
