package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.agenda.Agenda;

/**
 * Created by peter on 1/25/16.
 */
public class AgendaEvent {
    public Agenda agenda;

    public AgendaEvent(Agenda agenda) {
        this.agenda = agenda;
    }
}
