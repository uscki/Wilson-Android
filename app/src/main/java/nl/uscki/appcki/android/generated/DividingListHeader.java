package nl.uscki.appcki.android.generated;

public class DividingListHeader implements IWilsonBaseItem {

    private String dividingListHeader;
    private String dividingSubHeader;
    private String messageBody;
    private boolean bottomDividerVisible;

    public DividingListHeader(String header) {
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
