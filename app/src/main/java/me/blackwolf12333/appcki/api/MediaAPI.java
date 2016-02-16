package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.android.volley.Response;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.api.common.APISingleton;
import me.blackwolf12333.appcki.api.common.GsonRequest;
import me.blackwolf12333.appcki.api.common.VolleyAPI;
import me.blackwolf12333.appcki.events.MediaFileEvent;
import me.blackwolf12333.appcki.generated.media.MediaFile;
import me.blackwolf12333.appcki.api.media.ImageRequest;
import me.blackwolf12333.appcki.helpers.UserHelper;

/**
 * Created by peter on 2/6/16.
 */
public class MediaAPI extends VolleyAPI {
    public void getMediaFile(Integer id) {
        String url = this.url + "media/get?id=" + id;
        Log.d("MediaAPI", url);
        GsonRequest<MediaFile> request = new GsonRequest<>(url, MediaFile.class, getHeaders(), new Response.Listener<MediaFile>() {
            @Override
            public void onResponse(MediaFile response) {
                EventBus.getDefault().post(new MediaFileEvent(response));
            }
        }, errorListener);
        APISingleton.getInstance(App.getContext()).addToRequestQueue(request);
    }

    public void getMediaFileBitmap(MediaFile file) {
        getMediaFileBitmap(file.getId(), file.getMimetype());
    }

    public void getMediaFileBitmap(final Integer id, String mimetype) {
        String type = getFiletypeFromMime(mimetype);
        String url = String.format(ImageRequest.URL, type, id);

    }

    public static HashMap<String, String> getBitmapHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        if(UserHelper.getInstance().isLoggedIn()) { // avoid nullpointer
            headers.put("Cookie", "cookiestring=" + UserHelper.getInstance().getUser().getPerson().getCookiestring());
        }

        return headers;
    }

    public static String getFiletypeFromMime(String mimeType) {
        switch(mimeType) {
            case "image/gif":						return "Photo_Gif";
            case "image/png":						return "Photo_Png";
            case "image/pjpeg":
            case "image/jpg":
            case "image/jpeg":						return "Photo_Jpeg";
            case "plain/text":						return "Text_Plain";
            case "application/pdf":					return "Application_Pdf";
            case "application/x-shockwave-flash":	return "Application_Flash";
            case "audio/wav":						return "Audio_Wave";
            case "audio/x-wav":						return "Audio_Wave";
            case "audio/mp3":						return "Audio_Mp3";
            case "audio/mpeg":						return "Audio_Mp3";
            case "video/x-msvideo":					return "Video_Avi";
            case "video/x-flv":						return "Video_Flv";
            case "video/3gpp":						return "Video_Tgpp";
            case "video/mp4":						return "Video_MP4";
            case "video/x-ms-wmv":					return "Video_WMV";
            case "video/quicktime":					return "Video_Mov";
            case "video/mpeg":						return "Video_Mpeg";
            default :								return "Others_Other";
        }
    }
}
