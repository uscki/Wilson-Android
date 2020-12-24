package nl.uscki.appcki.android.fragments.media.captioncontest;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.media.adapters.CaptionAdapter;
import nl.uscki.appcki.android.generated.captioncontest.Caption;
import nl.uscki.appcki.android.generated.captioncontest.CaptionContest;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.getSystemService;

public class CaptionContestDetailFragment extends Fragment {

    public static final String ARG_CAPTION_CONTEST_ID = "ARG_CAPTION_CONTEST_ID";

    private int id = -1;
    private CaptionContest captionContest;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView headerTextview;
    private ImageView image;
    private TextView closesTextView;
    private RecyclerView captions;
    private CaptionAdapter adapter;
    private FloatingActionButton addCaptionFab;

    public CaptionContestDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.id = getArguments().getInt(ARG_CAPTION_CONTEST_ID, -1);
        }
        loadCaptionContest();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.caption_contest_menu, menu);

        MenuItem shareMenuItem = menu.findItem(R.id.caption_contest_menu_share);
        MenuItem archiveMenuItem = menu.findItem(R.id.caption_contest_menu_archive);

        shareMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TITLE, String.format(
                        Locale.getDefault(), "Caption contest van %s",  // TODO string resource
                        captionContest.getStartdate().toString("EEEE d MMMM Y") // TODO string resource
                ));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Captioncontest van uscki.nl");

                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_TEXT, String.format(
                        "Heb je deze captioncontest gezien?\n%s", // TODO string resource
                        getString(R.string.incognito_website_agenda_caption_contest_url, captionContest.getId()))
                );
                intent.setType("text/*");
                getActivity().startActivity(Intent.createChooser(intent, "Send to...")); // TODO string resource
                return true;
            }
        });

        archiveMenuItem.setOnMenuItemClickListener(item -> {
            Fragment ccArchiveFragment = new CaptionContestHistoryFragment();
            OpenFragmentEvent event = new OpenFragmentEvent(ccArchiveFragment);
            EventBus.getDefault().post(event);
            return true;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caption_contest_detail, container, false);
        this.swipeRefreshLayout = view.findViewById(R.id.caption_contest_swipe_refresh_layout);
        this.headerTextview = view.findViewById(R.id.caption_contest_title);
        this.image = view.findViewById(R.id.caption_contest_image);
        this.closesTextView = view.findViewById(R.id.caption_contest_closes_date);
        this.captions = view.findViewById(R.id.caption_contest_captions);
        this.adapter = new CaptionAdapter();
        this.captions.setAdapter(adapter);
        this.addCaptionFab = view.findViewById(R.id.caption_contest_add_caption_fab);

        return view;
    }

    private void loadCaptionContest() {
        if(this.id >= 0) {
            Services.getInstance().captionContestService.getCaptionContest(this.id).enqueue(captionContestCallback);
        } else {
            Services.getInstance().captionContestService.getCurrentCaptionContest().enqueue(captionContestCallback);
        }
    }

    private void showVoteConfirmation(Caption caption) {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialogbox_confirm_caption_vote_layout, null);
        Glide.with(getContext())
                .load(MediaAPI.getMediaUri(captionContest.getMediaFileId(), MediaAPI.MediaSize.NORMAL))
                .error(R.drawable.ic_wilson)
                .into((ImageView) dialogView.findViewById(R.id.imageView3));
        BBTextView captionText = dialogView.findViewById(R.id.BBTextView);
        captionText.setText(Parser.parse(caption.getCaption(), true, captionText));

        CheckBox checkBox = dialogView.findViewById(R.id.checkbox);

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Stemmen!", (dialog, which) -> { // TODO string resource
                    if(checkBox.isChecked()) {
                        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getContext());
                        p.edit().putBoolean(PREFERENCE_SHOW_CAPTION_CONTEST_VOTE_CONFIRMATION, false).apply();
                    }
                    vote(caption);
                })
                .setNegativeButton("Annuleren", null) // Todo string resource
                .show();
    }

    private void showAddCaptionDialog() {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_caption_layout, null);

        EditText editText = dialogView.findViewById(R.id.add_caption_editbox);
        CheckBox checkBox = dialogView.findViewById(R.id.add_caption_hide_name_checkbox);

        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Objects.requireNonNull(getContext()), InputMethodManager.class);
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Onderschrift toevoegen", (dialog, which) -> { // TODO string resource
                    if(inputMethodManager != null)
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        if (editText.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Je hebt geen onderschrift ingevuld!", Toast.LENGTH_LONG).show(); // TODO string resources
                        } else {
                        Services.getInstance().captionContestService
                                .addCaption(editText.getText().toString(), checkBox.isChecked())
                                .enqueue(new Callback<ActionResponse<Caption>>() {
                                    @Override
                                    public void onSucces(Response<ActionResponse<Caption>> response) {
                                        if (response.body() != null) {
                                            captionContest.addCaption(response.body().payload);
                                            setupView();
                                            Toast.makeText(getContext(), "Onderschrift toegevoegd", Toast.LENGTH_LONG).show(); // TODO string resources
                                        }
                                    }
                                });
                        }
                })
                .setNegativeButton("Annuleren", (dialog, which) -> {
                    if(inputMethodManager != null)
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                })
                .show();
    }


    // TODO add option in settings to revert
    public static final String PREFERENCE_SHOW_CAPTION_CONTEST_VOTE_CONFIRMATION
            = "NL.USCKI.WILSON.PREFERENCE.CAPTIONCONTEST.SHOW_CONFIRM";

    private void vote(Caption caption) {
        Services.getInstance().captionContestService.vote(caption.getId()).enqueue(new Callback<ActionResponse<CaptionContest>>() {
            @Override
            public void onSucces(Response<ActionResponse<CaptionContest>> response) {
                if(response.body() != null) {
                    captionContest = response.body().payload;
                    setupView();
                    Toast.makeText(getContext(), "Je hebt gestemd op deze Captioncontest!", Toast.LENGTH_LONG).show(); // TODO string resources;
                } else {
                    Toast.makeText(getContext(), "Er is iets fout gegaan bij het registreren van je stem", Toast.LENGTH_LONG).show(); // TODO string resources
                }
            }
        });
    }

    private void setupView() {
        CaptionContest.Status status = this.captionContest.getStatus();
        swipeRefreshLayout.setRefreshing(false);
        headerTextview.setText(String.format(Locale.getDefault(), "Captioncontest %s",
                captionContest.getStartdate().toString("EEEE d MMMM Y"))); // TODO string resource
        loadImage();
        if(attachedSwipeCallback != null) {
            attachedSwipeCallback.attachToRecyclerView(null);
        }

        setupVotingMechanics(status.isCanVote());
        setupAddCaptionButton(status.isCanAdd());
        setupClosesTextView(status.isShowClosesDate(), status.isCanAdd());

        adapter.setContest(this.captionContest);
        adapter.update(captionContest.getCaptions());
    }

    private void setupVotingMechanics(boolean canVote) {
        if (canVote) {
            attachedSwipeCallback = new ItemTouchHelper(swipeCallback);
            attachedSwipeCallback.attachToRecyclerView(captions);
        } else if (attachedSwipeCallback != null) {
            attachedSwipeCallback.attachToRecyclerView(null);
        }
    }

    private void setupAddCaptionButton(boolean canAdd) {
        if(canAdd) {
            addCaptionFab.setVisibility(View.VISIBLE);
            addCaptionFab.setOnClickListener(v -> showAddCaptionDialog());
        } else {
            addCaptionFab.setVisibility(View.GONE);
            addCaptionFab.setOnClickListener(null);
        }
    }

    private void setupClosesTextView(boolean showClosesDate, boolean canAdd) {
        if(showClosesDate && canAdd) {
            closesTextView.setText(String.format(Locale.getDefault(), "Je kunt nog onderschriften toevoegen tot: %s", // TODO resources (same as history fragment)
                    captionContest.getVotedate().toString("EEEE d MMMM Y")));
            closesTextView.setVisibility(View.VISIBLE);
        } else if (showClosesDate) {
            closesTextView.setText(String.format(Locale.getDefault(), "Je kunt stemmen vanaf %s",
                    captionContest.getVotedate().toString("EEEE d MMMM Y"))); // TODO string resources (same as history fragment)
            closesTextView.setVisibility(View.VISIBLE);
        } else {
            closesTextView.setVisibility(View.GONE);
        }
    }

    private void loadImage() {
        Glide.with(getContext())
                .load(MediaAPI.getMediaUri(captionContest.getMediaFileId(), MediaAPI.MediaSize.LARGE))
                .thumbnail(
                        Glide.with(getContext())
                                .load(MediaAPI.getMediaUri(captionContest.getMediaFileId(), MediaAPI.MediaSize.NORMAL))
                                .fitCenter()
                                .thumbnail(
                                        Glide.with(getContext())
                                                .load(MediaAPI.getMediaUri(captionContest.getMediaFileId(), MediaAPI.MediaSize.SMALL))
                                )
                )
                .fitCenter()
                .error(R.drawable.ic_wilson)
                .into(image);

        image.setOnClickListener(v -> {
            image.setTransitionName("captioncontest_detail");
            Intent intent = new FullScreenMediaActivity.SingleImageIntentBuilder(headerTextview.getText().toString(), "captioncontest_detail")
                    .media(captionContest.getMediaFileId())
                    .build(getContext());

            getActivity().startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), image, image.getTransitionName()).toBundle());
        });
    }

    private final Callback<CaptionContest> captionContestCallback = new Callback<CaptionContest>() {
        @Override
        public void onSucces(Response<CaptionContest> response) {
            captionContest = response.body();
            setupView();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(this.captionContest != null) setupView();
    }

    private ItemTouchHelper attachedSwipeCallback;

    /**
     * Handle swipe action on Caption Contest options
     *
     * See also https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
     */
    ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NotNull RecyclerView recyclerView,
                              @NotNull RecyclerView.ViewHolder viewHolder,
                              @NotNull RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            CaptionAdapter.CaptionViewHolder captionViewHolder = (CaptionAdapter.CaptionViewHolder) viewHolder;
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getContext());
            if(p.getBoolean(PREFERENCE_SHOW_CAPTION_CONTEST_VOTE_CONFIRMATION, true)) {
                showVoteConfirmation(captionViewHolder.getCaption());
            } else {
                vote(((CaptionAdapter.CaptionViewHolder) viewHolder).getCaption());
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if(viewHolder != null) {
                final View foregroundView = ((CaptionAdapter.CaptionViewHolder) viewHolder).foreground;

                // Make sure the foreground view is the one that moves, keeping the background fixed
                // in place
                getDefaultUIUtil().onSelected(foregroundView);
            }
        }

        @Override
        public void onChildDrawOver(@NotNull Canvas c, @NotNull RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((CaptionAdapter.CaptionViewHolder) viewHolder).foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((CaptionAdapter.CaptionViewHolder) viewHolder).foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }

        @Override
        public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView,
                                @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((CaptionAdapter.CaptionViewHolder) viewHolder).foreground;

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    };
}