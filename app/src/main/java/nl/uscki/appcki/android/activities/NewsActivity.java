package nl.uscki.appcki.android.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

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
    }

    private void initViews() {
        newsLong.setText(Parser.parse(item.getLongtextJSON(), true, newsLong));
        title.setText(item.getTitle());
        metadata.setText("(" + item.getPerson().getPostalname() + " / " + Utils.timestampConversion(item.getTimestamp()));
    }

}
