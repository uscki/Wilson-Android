package nl.uscki.appcki.android.helpers;

import android.content.Context;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;

public class AgendaSubscribedHelper {

    public static final int AGENDA_NOT_SUBSCRIBED = 0;
    public static final int AGENDA_SUBSCRIBED = 1;
    public static final int AGENDA_ON_BACKUP_LIST = 2;

    public static int isSubscribed(AgendaParticipantLists lists) {
        int myId = UserHelper.getInstance().getPerson().getId();

        for(int i = 0; i < lists.getParticipants().size(); i++) {
            if(lists.getParticipants().get(i).getPerson().getId().equals(myId)) {
                return AGENDA_SUBSCRIBED;
            }
        }

        for(int i = 0; i < lists.getBackupList().size(); i++) {
            if(lists.getBackupList().get(i).getPerson().getId().equals(myId)) {
                return AGENDA_ON_BACKUP_LIST;
            }
        }

        return AGENDA_NOT_SUBSCRIBED;

    }

    public static int isSubscribed(AgendaItem item) {
        AgendaParticipantLists lists = new AgendaParticipantLists();
        lists.setParticipants(item.getParticipants());
        lists.setBackupList(item.getBackupList());
        return isSubscribed(lists);
    }

    public static String getWhen(AgendaItem item) {
        String when;
        if (item.getEnd() != null) {
            boolean sameDay = item.getStart().getDayOfYear() == item.getEnd().getDayOfYear() &&
                    item.getStart().getYear() == item.getEnd().getYear();

            if(sameDay) {
                when = item.getStart().toString("EEEE dd MMMM YYYY HH:mm" + " - " + item.getEnd().toString("HH:mm"));
            } else {
                when = item.getStart().toString("EEEE dd MMMM YYYY HH:mm") + " - " + item.getEnd().toString("EEEE dd MMMM YYYY HH:mm");
            }
        } else {
            when = item.getStart().toString("EEEE dd MMMM YYYY HH:mm");
        }
        return when;
    }

    public static String getParticipantsSummary(Context context, AgendaItem item) {
        String participantsSummary;
        if(item.getMaxregistrations() == null) {
            participantsSummary = context.getString(R.string.agenda_item_n_registrations, item.getParticipants().size());
        } else if(item.getMaxregistrations().equals(0)) {
            participantsSummary = context.getString(R.string.agenda_prepublished_event_registration_closed_short_message);
        } else if(item.getBackupList().size() > 0) {
            participantsSummary = context.getString(
                    R.string.agenda_item_n_registration_plus_backup,
                    item.getParticipants().size(),
                    item.getBackupList().size(),
                    item.getMaxregistrations());
        } else {
            participantsSummary = context.getString(
                    R.string.agenda_item_n_registrations_max,
                    item.getParticipants().size(),
                    item.getMaxregistrations()
            );
        }

        return participantsSummary;
    }
}
