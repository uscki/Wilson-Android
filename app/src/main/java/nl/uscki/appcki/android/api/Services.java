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
    public QuoteService quoteService;
    public SmoboService smoboService;
    public PollService pollService;
    public PermissionsService permissionsService;

    private Services() {
        agendaService = ServiceGenerator.createService(AgendaService.class);
        newsService = ServiceGenerator.createService(NewsService.class);
        shoutboxService = ServiceGenerator.createService(ShoutboxService.class);
        meetingService = ServiceGenerator.createService(MeetingService.class);
        imageService = ServiceGenerator.createService(ImageService.class);
        mediaService = ServiceGenerator.createService(MediaService.class);
        userService = ServiceGenerator.createService(UserService.class);
        quoteService = ServiceGenerator.createService(QuoteService.class);
        smoboService = ServiceGenerator.createService(SmoboService.class);
        pollService = ServiceGenerator.createService(PollService.class);
        permissionsService = ServiceGenerator.createService(PermissionsService.class);
    }

    // after calling this, the next time getInstance is called all services will be regenerated
    public static void invalidate() {
        if(instance != null) {
            instance = null;
        }
    }

    public static Services getInstance() {
        if (instance == null) {
            instance = new Services();
        }

        return instance;
    }
}
