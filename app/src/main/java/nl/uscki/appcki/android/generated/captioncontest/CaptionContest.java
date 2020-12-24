package nl.uscki.appcki.android.generated.captioncontest;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class CaptionContest implements IWilsonBaseItem {

    @Expose
    Integer id;

    @Expose
    List<Caption> captions;

    @Expose
    Integer mediaFileId;

    @Expose
    DateTime startdate;

    @Expose
    DateTime votedate;

    @Expose
    Boolean hasVoted;

    @Expose
    Boolean hasAdded;

    @Expose
    Boolean isCurrentContest;

    @Override
    public Integer getId() {
        return this.id;
    }

    public List<Caption> getCaptions() {
        return captions;
    }

    public Integer getMediaFileId() {
        return mediaFileId;
    }

    public DateTime getStartdate() {
        return startdate;
    }

    public DateTime getVotedate() {
        return votedate;
    }

    public boolean getHasVoted() {
        return hasVoted;
    }

    public boolean getHasAdded() {
        return hasAdded;
    }

    public void addCaption(Caption caption) {
        this.hasAdded = true;
        this.captions.add(caption);
    }

    public boolean getIsCurrentContest() {
        return isCurrentContest;
    }

    public Status getStatus() {
        if (!isCurrentContest && getStartdate().isBefore(DateTime.now())) {
            return Status.CLOSED;
        } else if (getStartdate().isAfter(DateTime.now())) {
            return Status.WAITING;
        } else if (getVotedate().isBefore(DateTime.now())) {
            return hasVoted ? Status.VOTED : Status.VOTING;
        } else {
            return hasAdded ? Status.ADDED : Status.ADDING;
        }
    }

    public enum Status {
        WAITING(false, false, false, false),
        ADDING(false, true, false, true),
        ADDED(false, false, false, true),
        VOTING(true, false, false, false),
        VOTED(false, false, true, false),
        CLOSED(false, false, true, false);

        private boolean canVote;
        private boolean canAdd;
        private boolean resultVisible;
        private boolean showClosesDate;

        Status(boolean canVote, boolean canAdd, boolean resultVisible, boolean showClosesDate) {
            this.canVote = canVote;
            this.canAdd = canAdd;
            this.resultVisible = resultVisible;
            this.showClosesDate = showClosesDate;
        }

        public boolean isCanVote() {
            return canVote;
        }

        public boolean isCanAdd() {
            return canAdd;
        }

        public boolean isResultVisible() {
            return resultVisible;
        }

        public boolean isShowClosesDate() {
            return showClosesDate;
        }
    }
}
