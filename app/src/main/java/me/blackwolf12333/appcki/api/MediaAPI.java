package me.blackwolf12333.appcki.api;

import android.graphics.Bitmap;

import java.util.HashMap;

import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.media.MediaFile;
import me.blackwolf12333.appcki.helpers.UserHelper;

/**
 * Created by peter on 2/6/16.
 */
public class MediaAPI {
    public static String URL = "https://www.uscki.nl/?pagina=Media/MediaObject/%s&mediaFile=%d";
    public static String API_URL = App.getContext().getString(R.string.apiurl) + "media/";
    private static HashMap<String, Bitmap> cache = new HashMap<>();

    public static Bitmap getFromCache(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }
        return null;
    }

    public static boolean cacheContains(String url) {
        return cache.containsKey(url);
    }

    public static void putInCache(Bitmap bitmap, String url) {
        if (cache.size() < 5) {
            cache.put(url, bitmap);
        } else {
            String first = cache.keySet().iterator().next();
            cache.remove(first);
            cache.put(url, bitmap);
        }
    }

    public static HashMap<String, String> getBitmapHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        if(UserHelper.getInstance().isLoggedIn()) { // avoid nullpointer
            headers.put("Cookie", "cookiestring=" + UserHelper.getInstance().getPerson().getCookiestring());
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
