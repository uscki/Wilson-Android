package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.media.ImageService;
import nl.uscki.appcki.android.api.media.MediaService;

/**
 * Created by peter on 7/12/16.
 */
public class Services {
    private static Services instance;

    public AgendaService agendaService;
    public NewsService newsService;
    public ShoutboxService shoutboxService;
    public MeetingService meetingService;
    public ImageService imageService;
    public MediaService mediaService;
    public UserService userService;

    private Services() {
        agendaService = ServiceGenerator.createService(AgendaService.class);
        newsService = ServiceGenerator.createService(NewsService.class);
        shoutboxService = ServiceGenerator.createService(ShoutboxService.class);
        meetingService = ServiceGenerator.createService(MeetingService.class);
        imageService = ServiceGenerator.createService(ImageService.class);
        mediaService = ServiceGenerator.createService(MediaService.class);
        userService = ServiceGenerator.createService(UserService.class);
    }

    public static Services getInstance() {
        if (instance == null) {
            instance = new Services();
        }

        return instance;
    }
}
