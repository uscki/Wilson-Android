package nl.uscki.appcki.android.helpers.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.generated.ServerError;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.services.EventExportJobService;
import nl.uscki.appcki.android.services.NotificationReceiver;
import retrofit2.Call;
import retrofit2.Response;

public class AgendaSubscribeServiceHelper {
    // Action names accepted by this service
    public static final String ACTION_SUBSCRIBE_AGENDA
            = "nl.uscki.appcki.android.services.action.SUBSCRIBE_AGENDA";

    // Intent parameters accepted by this service
    public static final String PARAM_AGENDA_ID
            = "nl.uscki.appcki.android.services.extra.PARAM_AGENDA_ID";
    public static final String PARAM_SUBSCRIBE_COMMENT
            = "nl.uscki.appcki.android.services.extra.PARAM_AGENDA_SUBSCRIBE_COMMENT";


    private Context context;

    public AgendaSubscribeServiceHelper(Context context) {
        this.context = context;
    }

    /**
     * Extract the subscribe text from a remote input object, as is generated by the subscribe
     * action in the new agenda item notification
     * @param intent    Intent with which this service was started, if action is subscribe
     * @return          String containing user entered subscribe text
     */
    public CharSequence getSubscribeText(Intent intent) {
        Bundle remoteInput = android.support.v4.app.RemoteInput.getResultsFromIntent(intent);

        if(remoteInput != null) {
            return remoteInput.getCharSequence(PARAM_SUBSCRIBE_COMMENT);
        }

        return "";
    }

    /**
     * Subscribe the active user to an agenda event
     * @param agendaId              ID of the agenda event
     * @param subscribeComment      User entered subscription comment
     * @param intent                Intent with action subscribe, with which this service was started
     */
    public void handleAgendaSubscribeAction(final int agendaId, final String subscribeComment, final Intent intent) {
        if(agendaId < 0) {
            handleError(intent, context.getString(R.string.content_loading_error), false);
            return;
        }

        // Make API available
        ServiceGenerator.init();

        // Get token active
        UserHelper.getInstance().load();

        Services.getInstance().agendaService.subscribe(agendaId, subscribeComment)
                .enqueue(new retrofit2.Callback<ActionResponse<AgendaParticipantLists>>() {

                    @Override
                    public void onResponse(
                            Call<ActionResponse<AgendaParticipantLists>> call,
                            Response<ActionResponse<AgendaParticipantLists>> response)
                    {
                        if(response.isSuccessful()) {
                            handleSuccess(response.body().payload, agendaId, intent);
                        } else {
                            String errorMsg = context.getString(R.string.connection_error);
                            boolean allowSubscribe = true;
                            try {
                                Gson gson = new Gson();
                                ServerError error = gson.fromJson(
                                        response.errorBody().string(), ServerError.class);

                                if(error.getStatus() == 401) {
                                    errorMsg = context.getString(R.string.notauthorized);
                                    allowSubscribe = false;
                                }
                                if(error.getStatus() == 400) {
                                    errorMsg = context.getString(R.string.agenda_subscribe_bad_request_error);
                                    allowSubscribe = false;
                                } else if(error.getStatus() == 403) {
                                    errorMsg = context.getString(R.string.notloggedin);
                                    allowSubscribe = false;
                                } else if(error.getStatus() == 404) {
                                    errorMsg = context.getString(R.string.content_loading_error);
                                } else if (error.getStatus() == 500) {
                                    errorMsg = context.getString(R.string.unknown_server_error);
                                }

                            } catch (Exception e) {
                                Log.e(getClass().toString(), e.toString());
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.unknown_server_error),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                            handleError(intent, errorMsg, allowSubscribe);
                        }
                    }

                    @Override
                    public void onFailure(Call<ActionResponse<AgendaParticipantLists>> call, Throwable t) {
                        Log.e(getClass().toString(), t.getMessage());
                        handleError(intent, context.getString(R.string.unknown_server_error), true);
                    }
                });
    }

    /**
     * On successful subscribe attempt, update the notification and show a toast notification
     * indicating success
     *
     * @param response      Server response parsed as AgendaParticipantLists for subscribe action
     * @param agendaId      ID of agenda item subscribed to
     * @param intent        Intent with which this service was started
     */
    private void handleSuccess(
            AgendaParticipantLists response,
            int agendaId,
            Intent intent
    ) {
        EventBus.getDefault()
                .post(new AgendaItemSubscribedEvent(response, false));
        NotificationReceiver notificationReceiver = new NotificationReceiver(context);

        boolean allowExport = true;

        if(PermissionHelper.canExportCalendarAuto()) {
            allowExport = false;
            EventExportJobService
                    .enqueueExportAgendaToCalendarAction(context, agendaId);
        }

        notificationReceiver
                .buildNewAgendaItemNotificationFromIntent(intent, allowExport, false);

        Toast.makeText(
                context,
                context.getString(R.string.agenda_subscribe_success),
                Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Show a toast notification with an handleError and reset the original notification
     * @param intent    Intent with which this service was started
     * @param error     Error message to show
     */
    private void handleError(Intent intent, String error, boolean allowExport) {
        NotificationReceiver notificationReceiver = new NotificationReceiver(context);
        notificationReceiver.buildNewAgendaItemNotificationFromIntent(intent, allowExport, true);
        Toast.makeText(
                context,
                error,
                Toast.LENGTH_SHORT)
                .show();
    }
}
