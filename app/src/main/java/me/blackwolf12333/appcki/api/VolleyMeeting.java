package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.android.volley.Response;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.common.VolleyAPI;
import me.blackwolf12333.appcki.events.MeetingEvent;
import me.blackwolf12333.appcki.events.MeetingOverviewEvent;
import me.blackwolf12333.appcki.generated.meeting.Meeting;
import me.blackwolf12333.appcki.generated.meeting.MeetingOverview;

/**
 * Created by peter on 2/14/16.
 */
public class VolleyMeeting extends VolleyAPI {
    private static VolleyMeeting instance;

    public static synchronized VolleyMeeting getInstance( ) {
        if (instance == null)
            instance = new VolleyMeeting();
        return instance;
    }

    public void getMeetingOverview() {
        this.apiCall(new MeetingOverviewCall());
    }

    public void getMeeting(Integer id) {
        this.apiCall(new MeetingCall(id));
    }

    public void setSlot(Integer id, String note) {
        this.apiCall(new SetSlotCall(id, note));
    }

    public class SetSlotCall extends Call<Boolean> {
        public SetSlotCall(Integer id, String note) {
            this.url = "meetings/slots/" + id;
            this.arguments = new HashMap<>();
            this.arguments.put("notes", note);
            this.arguments.put("canAttend", true);
            this.type = Boolean.class;
            this.responseListener = new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    Log.d("VolleyMeeting", "response: " + response);
                }
            };
        }
    }

    public class MeetingCall extends Call<Meeting> {
        public MeetingCall(Integer id) {
            this.url = "meetings/" + id;
            this.arguments = new HashMap<>();
            //this.arguments.put("id", id);
            this.type = Meeting.class;
            this.responseListener = new Response.Listener<Meeting>() {
                @Override
                public void onResponse(Meeting response) {
                    EventBus.getDefault().post(new MeetingEvent(response));
                }
            };
        }
    }

    public class MeetingOverviewCall extends Call<MeetingOverview> {
        public MeetingOverviewCall() {
            this.url = "meetings/";
            this.arguments = new HashMap<>();
            //this.arguments.put("sort", "startdate,starttime,asc");
            this.type = MeetingOverview.class;
            this.responseListener = new Response.Listener<MeetingOverview>() {
                @Override
                public void onResponse(MeetingOverview response) {
                    EventBus.getDefault().post(new MeetingOverviewEvent(response));
                }
            };
        }
    }
}
