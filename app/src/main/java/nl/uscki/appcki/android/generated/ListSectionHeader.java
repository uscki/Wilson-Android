package nl.uscki.appcki.android.generated;

public class ListSectionHeader implements IWilsonBaseItem {

    private String dividingListHeader;
    private String dividingSubHeader;
    private String messageBody;
    private String helpText;
    private boolean bottomDividerVisible;

    public ListSectionHeader(String header) {
        this.dividingListHeader = header;
    }

    public String getDividingListHeader() {
        return dividingListHeader;
    }

    public String getDividingSubHeader() {
        return dividingSubHeader;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setSubHeader(String subHeader) {
        this.dividingSubHeader = subHeader;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getHelpText() {
        return this.helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public void setBottomDividerVisible(boolean visible) {
        this.bottomDividerVisible = visible;
    }

    public boolean isBottomDividerVisible() {
        return bottomDividerVisible;
    }

    @Override
    public Object getId() {
        return 0;
    }
}
