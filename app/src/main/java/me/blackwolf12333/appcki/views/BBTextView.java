package me.blackwolf12333.appcki.views;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.events.LinkClickedEvent;

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
        SpannableString current=(SpannableString)this.getText();
        URLSpan[] spans=
                current.getSpans(0, current.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start=current.getSpanStart(span);
            int end=current.getSpanEnd(span);

            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(span.getURL()), start, end,
                    0);
        }
    }

    public static String bbcode(String text) {
        String html = text;

        Map<String,String> bbMap = new HashMap<>();

        /*bbMap.put("(\r\n|\r|\n|\n\r)", "<br/>");
        bbMap.put("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>");
        bbMap.put("\\[i\\](.+?)\\[/i\\]", "<span style='font-style:italic;'>$1</span>");
        bbMap.put("\\[u\\](.+?)\\[/u\\]", "<span style='text-decoration:underline;'>$1</span>");
        bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");
        bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");
        bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");
        bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");
        bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");
        bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");
        bbMap.put("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>");
        bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");
        bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style='text-indent:$1px;line-height:$2%;'>$3</p>");
        bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1");
        bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2");
        bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style='color:$1;'>$2</span>");
        bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style='font-size:$1;'>$2</span>");
        bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img src='$1' />");
        bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />");
        bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>");
        bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>");
        bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>");
        bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1'>$2</a>");
        bbMap.put("\\[youtube\\](.+?)\\[/youtube\\]", "<object width='640' height='380'><param name='movie' value='http://www.youtube.com/v/$1'></param><embed src='http://www.youtube.com/v/$1' type='application/x-shockwave-flash' width='640' height='380'></embed></object>");
        bbMap.put("\\[video\\](.+?)\\[/video\\]", "<video src='$1' />");*/

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
            try {
                EventBus.getDefault().post(new LinkClickedEvent(getURL()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getURL()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                App.getContext().startActivity(intent);
                super.onClick(widget);
            }
            catch (ActivityNotFoundException e) {
                // do something useful here
            }
        }
    }
}
