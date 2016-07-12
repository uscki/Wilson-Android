package me.blackwolf12333.appcki.api;

import me.blackwolf12333.appcki.api.media.ImageService;
import me.blackwolf12333.appcki.api.media.MediaService;

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

    private Services() {
        agendaService = ServiceGenerator.createService(AgendaService.class);
        newsService = ServiceGenerator.createService(NewsService.class);
        shoutboxService = ServiceGenerator.createService(ShoutboxService.class);
        meetingService = ServiceGenerator.createService(MeetingService.class);
        imageService = ServiceGenerator.createService(ImageService.class);
        mediaService = ServiceGenerator.createService(MediaService.class);
    }

    public static Services getInstance() {
        if (instance == null) {
            instance = new Services();
        }

        return instance;
    }
}
