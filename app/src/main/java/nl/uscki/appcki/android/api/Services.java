package nl.uscki.appcki.android.api;

import com.squareup.picasso.Picasso;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.api.media.ImageService;
import nl.uscki.appcki.android.api.media.MediaService;
import nl.uscki.appcki.android.api.media.PicassaMediaDownloader;

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
    public PeopleService peopleService;
    public QuoteService quoteService;
    public SmoboService smoboService;
    public Picasso picasso;

    private Services() {
        agendaService = ServiceGenerator.createService(AgendaService.class);
        newsService = ServiceGenerator.createService(NewsService.class);
        shoutboxService = ServiceGenerator.createService(ShoutboxService.class);
        meetingService = ServiceGenerator.createService(MeetingService.class);
        imageService = ServiceGenerator.createService(ImageService.class);
        mediaService = ServiceGenerator.createService(MediaService.class);
        userService = ServiceGenerator.createService(UserService.class);
        peopleService = ServiceGenerator.createService(PeopleService.class);
        quoteService = ServiceGenerator.createService(QuoteService.class);
        smoboService = ServiceGenerator.createService(SmoboService.class);
        picasso = new Picasso.Builder(App.getContext())
                .downloader(new PicassaMediaDownloader(ServiceGenerator.client))
                .build();
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
