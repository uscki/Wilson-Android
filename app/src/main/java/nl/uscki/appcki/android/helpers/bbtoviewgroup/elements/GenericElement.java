package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;

/**
 * This abstract class contains the basic implementations for the BBCodeElement interface. It is not required for an
 * element to extend this class, it only provides basic functionality so that extensions can focus on element specific
 * code
 *
 * @author Ty Mees, modified by Peter Maatman
 * @version 1.2
 * @since 0.1
 */
public class GenericElement {

    public GenericElement(ArrayList<Object> content, String parameter) {
        this.content = content;
        this.parameter = parameter;
    }

    /**
     * Static string containing the 'element' name, only usefull because it is returned in the JSON output
     */
    protected String type = null;

    /**
     * ArrayList containing the content of this element. Content can be other BBCodeElements, or Strings
     */
    protected ArrayList<Object> content = new ArrayList<>();

    /**
     * Strng containing the parameter value for this tag. Can be empty.
     */
    protected String parameter = null;

    /**
     * This boolean indicates if the contents of this tag should be parsed. If set to false it treats all content as
     * Strings
     */
    protected boolean parseContents;

    /**
     * This boolean indicates if the contents of this tag should have it's emoticons replaced with unicode emojis
     */
    protected boolean replaceEmoji;

    /**
     * Setter for the content ArrayList
     * @param content The full content to be set, in an ArrayList, not null
     */
    public void setContent(ArrayList<Object> content)
    {
        this.content = content;
    }

    /**
     * Getter for the content ArrayList
     * @return The current content of this element
     */
    public ArrayList<Object> getContent()
    {
        return this.content;
    }

    /**
     * Setter for the parameter string
     * @param parameter The parameter content of this element
     */
    public void setParameter(String parameter)
    {
        this.parameter = parameter;
    }

    /**
     * Setter for the type string
     * @param type The type of this element
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for the parameter string
     * @return The parameter content of this element
     */
    public String getParameter()
    {
        return this.parameter;
    }

    /**
     * Getter for the parseConents boolean
     * @return Whether the contents should be parsed or not
     */
    public boolean getParseContents()
    {
        return this.parseContents;
    }

    /**
     * Getter for the replaceEmoji boolean
     * @return the replaceEmoji boolean's value
     */
    public boolean getReplaceEmoji()
    {
        return this.replaceEmoji;
    }

    /**
     * Getter for the type String
     * @return The element type
     */
    public String getType() {
        return type;
    }

    public SpannableStringBuilder getSpannedText() {
        return Parser.parse(getContent(), true);
    }

    public static GenericElement fromLinkedTreeUnit(LinkedTreeMap<String, Object> input) {
        String type = (String) input.get("type");
        ArrayList<Object> c = (ArrayList<Object>) input.get("content");
        String p = (String) input.get("parameter");
        switch (type) {
            case "B":
                return new B(c, p);
            case "I":
                return new I(c, p);
            case "U":
                return new U(c, p);
            case "S":
                return new S(c, p);
            case "Link":
                return new Link(c, p);
            case "Li":
                return new Li(c, p);
            case "Lu":
                return new Ul(c, p);
            case "Code":
                return new Code(c, p);
            case "Quote":
                return new Quote(c, p);
            case "Spoiler":
                return new Spoiler(c, p);
            default:
                return new GenericElement(c, p);
        }
    }
}
