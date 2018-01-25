package nl.uscki.appcki.android.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
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

    @BindView(R.id.news_item_content)
    BBTextView newsLong;
    @BindView(R.id.news_item_title)
    TextView title;
    @BindView(R.id.news_item_metadata)
    TextView metadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getStringExtra("item") != null) {
            item = new Gson().fromJson(getIntent().getStringExtra("item"), NewsItem.class);
            initViews();
        }
        if (getIntent().getIntExtra("id", 0) != 0) {
            Services.getInstance().newsService.get(getIntent().getIntExtra("id", 0)).enqueue(new Callback<NewsItem>() {
                @Override
                public void onSucces(Response<NewsItem> response) {
                    item = response.body();
                    initViews();
                }
            });
        }
    }

    private void initViews() {
        if (item.getLongtextJSON() != null) {
            newsLong.setText(Parser.parse(item.getLongtextJSON(), true, newsLong));
        } else {
            newsLong.setText(Parser.parse(item.getShorttextJSON(), true, newsLong));
        }

        title.setText(item.getTitle());
        metadata.setText("(" + item.getPerson().getPostalname() + " / " + Utils.timestampConversion(item.getTimestamp()));
    }

}
