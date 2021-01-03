package nl.uscki.appcki.android.api;

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
    public MediaService mediaService;
    public UserService userService;
    public QuoteService quoteService;
    public SmoboService smoboService;
    public PollService pollService;
    public PermissionsService permissionsService;
    public ShopService shopService;
    public ForumService forumService;
    public BBService bbService;

    private Services() {
        agendaService = ServiceGenerator.createService(AgendaService.class);
        newsService = ServiceGenerator.createService(NewsService.class);
        shoutboxService = ServiceGenerator.createService(ShoutboxService.class);
        meetingService = ServiceGenerator.createService(MeetingService.class);
        mediaService = ServiceGenerator.createService(MediaService.class);
        userService = ServiceGenerator.createService(UserService.class);
        quoteService = ServiceGenerator.createService(QuoteService.class);
        smoboService = ServiceGenerator.createService(SmoboService.class);
        pollService = ServiceGenerator.createService(PollService.class);
        permissionsService = ServiceGenerator.createService(PermissionsService.class);
        shopService = ServiceGenerator.createService(ShopService.class);
        forumService = ServiceGenerator.createService(ForumService.class);
        bbService = ServiceGenerator.createService(BBService.class);
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
