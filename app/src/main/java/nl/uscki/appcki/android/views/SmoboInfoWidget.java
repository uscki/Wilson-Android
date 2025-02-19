package nl.uscki.appcki.android.views;


import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import nl.uscki.appcki.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmoboInfoWidget extends Fragment {
    ImageView infoIcon;
    TextView mainText;
    TextView subText;
    ImageButton contextButton;
    OnContextButtonClickListener onContextButtonClickListener;

    private String mainTextContent;
    private String subTextContent;
    InfoType type;

    public enum InfoType {
        PHONE,
        EMAIL,
        ADRESS,
        BIRTHDAY,
        HOMEPAGE
    }

    public SmoboInfoWidget(String subText, InfoType type) {
        // Required empty public constructor
        this.subTextContent = subText;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_smobo_info_widget, container, false);

        infoIcon = view.findViewById(R.id.smobo_info_icon);
        mainText = view.findViewById(R.id.smobo_info_main_text);
        subText = view.findViewById(R.id.smobo_info_sub_text);
        contextButton = view.findViewById(R.id.smobo_info_context_button);

        init();

        contextButton.setOnClickListener(v -> onContextButtonClickListener.onClick(mainText.getText().toString(), type));

        return view;
    }

    public void updateMaintext(String mainText) {
        this.mainTextContent = mainText;
        if(this.mainText != null) {
            init();
        }
    }

    private void init() {
        this.mainText.setText(this.mainTextContent);
        this.subText.setText(this.subTextContent);

        switch (this.type) {
            case PHONE:
                this.infoIcon.setImageResource(R.drawable.phone);
                this.contextButton.setImageResource(R.drawable.message_text_smobo);
                Linkify.addLinks(this.mainText, Linkify.PHONE_NUMBERS);
                this.mainText.setLinksClickable(true);
                break;
            case EMAIL:
                this.infoIcon.setImageResource(R.drawable.mail_ru);
                this.contextButton.setImageResource(R.drawable.email);
                break;
            case ADRESS:
                this.infoIcon.setImageResource(R.drawable.map_marker);
                this.contextButton.setImageResource(R.drawable.directions);
                break;
            case BIRTHDAY:
                this.infoIcon.setImageResource(R.drawable.cake);
                break;
            case HOMEPAGE:
                this.infoIcon.setImageResource(R.drawable.rc_earth);
                Linkify.addLinks(this.mainText, Linkify.WEB_URLS);
                this.mainText.setLinksClickable(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnContextButtonClickListener) {
            this.onContextButtonClickListener = (OnContextButtonClickListener) context;
        } else {
            throw new RuntimeException("Context does not implement OnContextButtonClickListener");
        }
        super.onAttach(context);
    }

    public interface OnContextButtonClickListener {
        void onClick(String mainText, InfoType type);
    }
}
