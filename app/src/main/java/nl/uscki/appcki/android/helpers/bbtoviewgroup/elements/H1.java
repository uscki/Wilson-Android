package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

/**
 * This class describes a header element, H1 to be exact.
 *
 * @author Ty Mees
 * @version 1.4
 * @since 0.2
 */
public class H1 extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public H1(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "H1";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        String content = (String) getContent().get(0);
        SpannableString str = new SpannableString(content);
        str.setSpan(new TextAppearanceSpan(App.getContext(), R.style.AppTheme_TextHeader), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return null;
    }
}
