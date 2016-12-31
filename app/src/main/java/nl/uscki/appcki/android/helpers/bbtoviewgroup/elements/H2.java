package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;

/**
 * This class describes a header element, H2 to be exact.
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class H2 extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public H2(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "H2";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        SpannableStringBuilder str = Parser.parse(getContent(), true);
        str.setSpan(new TextAppearanceSpan(App.getContext(), R.style.AppTheme_TextSubHeader), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
