package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;

import java.util.ArrayList;

import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.fragments.dialogs.TableSpanDialog;
import nl.uscki.appcki.android.helpers.bbparser.spans.ClickableSpan;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a table container element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.13
 */
public class Table extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Table(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Table";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = new SpannableStringBuilder("Show table");

        str.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (widget.getContext() instanceof BasicActivity) {
                    BasicActivity activity = (BasicActivity) widget.getContext();
                    Bundle args = new Bundle();
                    args.putSerializable("table", getContent());

                    DialogFragment fragment = new TableSpanDialog();
                    fragment.setArguments(args);
                    fragment.show(activity.getSupportFragmentManager(), "table_dialog");
                }
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }
}
