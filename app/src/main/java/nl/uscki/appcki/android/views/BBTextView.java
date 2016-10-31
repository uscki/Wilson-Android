package nl.uscki.appcki.android.views;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

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
        String parsed = bbcode(text.toString());
        this.setMovementMethod(LinkMovementMethod.getInstance());
        super.setText(Html.fromHtml(parsed), type);
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

    public static String bbcode(String text) {
        String html = text;

        Map<String,String> bbMap = new HashMap<>();
        bbMap.put("\\[link url=(.+?)\\](.+?)\\[/link\\]", "<a href='$1'>$2</a> ");
        bbMap.put("(\\r\\n|\\r|\\n|\\n\\r)", "<br/>");

        for (Map.Entry entry: bbMap.entrySet()) {
            html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
        }

        return html;
    }

    private static class DefensiveURLSpan extends URLSpan {
        public DefensiveURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            EventBus.getDefault().post(new LinkClickedEvent(getURL()));
            super.onClick(widget);
        }
    }
}
