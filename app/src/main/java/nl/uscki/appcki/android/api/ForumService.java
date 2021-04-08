package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.forum.Forum;
import nl.uscki.appcki.android.generated.forum.Post;
import nl.uscki.appcki.android.generated.forum.RecentTopic;
import nl.uscki.appcki.android.generated.forum.Topic;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ForumService {
    @GET("fora/")
    Call<Pageable<Forum>> getFora(@Query("page") int page, @Query("size") int size, @Query("sort") String... sort);

    @GET("fora/{forum}/can_post")
    Call<Boolean> canPost(@Path("forum") int forumId);

    @GET("fora/{forum}/topics/")
    Call<Pageable<Topic>> getTopics(@Path("forum") int forumId, @Query("page") int page, @Query("size") int size, @Query("sort") String... sort);

    @GET("fora/{forum}/topics/{topic}/posts/")
    Call<Pageable<Post>> getPosts(@Path("forum") int forumId, @Path("topic") int topicId, @Query("page") int page, @Query("size") int size, @Query("sort") String... sort);

    @GET("fora/recent/")
    Call<Pageable<RecentTopic>> getRecent(@Query("page") int page, @Query("size") int size, @Query("sort") String... sort);

    @POST("fora/{forum}/topics/new")
    Call<ActionResponse<Topic>> createTopic(@Path("forum") int forumId, @Query("content") String content, @Query("name") String name, @Query("signature") String signature, @Query("title") String title);

    @POST("fora/{forum}/topics/{topic}/mark_read")
    Call<ActionResponse<Boolean>> markRead(@Path("forum") int forumId, @Path("topic") int topicId);

    @POST("fora/{forum}/topics/{topic}/posts/new")
    Call<ActionResponse<Post>> addPost(@Path("forum") int forumId, @Path("topic") int topicId, @Query("content") String content, @Query("name") String name, @Query("signature") String signature);

    @POST("fora/{forum}/topics/{topic}/posts/{post}/edit")
    Call<ActionResponse<Post>> updatePost(@Path("forum") int forumId, @Path("topic") int topicId, @Path("post") int postId, @Query("content") String content, @Query("name") String name, @Query("signature") String signature);
}
