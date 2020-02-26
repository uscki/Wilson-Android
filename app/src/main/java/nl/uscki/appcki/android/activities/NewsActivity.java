package nl.uscki.appcki.android.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;
import retrofit2.Response;

public class NewsActivity extends BasicActivity {
    NewsItem item;

    BBTextView newsLong;
    TextView title;
    TextView metadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsLong = findViewById(R.id.news_item_content);
        title = findViewById(R.id.news_item_title);
        metadata = findViewById(R.id.news_item_metadata);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getStringExtra("item") != null) {
            item = new Gson().fromJson(getIntent().getStringExtra("item"), NewsItem.class);
            initViews();
        }
        if (getIntent().getIntExtra("id", 0) != 0) {
            Services.getInstance().newsService.getNewsResource(getIntent().getIntExtra("id", 0)).enqueue(new Callback<NewsItem>() {
                @Override
                public void onSucces(Response<NewsItem> response) {
                    item = response.body();
                    initViews();
                }
            });
        }
    }

    private void initViews() {
        if (item.getLongtext() != null) {
            newsLong.setText(Parser.parse(item.getLongtext(), true, newsLong));
        } else {
            newsLong.setText(Parser.parse(item.getShorttext(), true, newsLong));
        }

        title.setText(item.getTitle());
        metadata.setText("(" + item.getPerson().getPostalname() + " / " + Utils.timestampConversion(item.getTimestamp().getMillis(), false) + ")");
    }

}
