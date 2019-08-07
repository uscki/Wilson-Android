package nl.uscki.appcki.android.fragments.agenda;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import retrofit2.Response;

/**
 * Created by peter on 6/28/16.
 */
public class SubscribeDialogFragment extends DialogFragment {

    @BindView(R.id.agenda_dialog_note)
    EditText note;

    @BindView(R.id.agenda_dialog_registration_question_answer)
    EditText registrationQuestionAnswer;

    @BindView(R.id.subscribe_agenda_question)
    TextView registrationQuestion;

    @BindView(R.id.agenda_registration_question_options)
    RecyclerView possibleAnswersView;

    private Button positiveButton;

    private PossibleAnswersAdapter adapter;

    AgendaActivity activity;

    private Callback<ActionResponse<AgendaParticipantLists>> agendaSubscribeCallback =
        new Callback<ActionResponse<AgendaParticipantLists>>() {
            @Override
            public void onSucces(Response<ActionResponse<AgendaParticipantLists>> response) {
                EventBus.getDefault()
                        .post(new AgendaItemSubscribedEvent(
                                response.body().payload,
                                false
                        ));
            }

            @Override
            public void onError(Response<ActionResponse<AgendaParticipantLists>> response) {
                super.onError(response);
                Toast.makeText(getContext(), R.string.agenda_subscribe_failed, Toast.LENGTH_SHORT).show();
            }
        };

    private DialogInterface.OnShowListener onDialogShowListener =
            new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    final AlertDialog dialog = (AlertDialog) dialogInterface;
                    if (activity.getAgendaItem().getQuestion() != null && !activity.getAgendaItem().getQuestion().isEmpty()) {
                        positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setEnabled(registrationQuestionAnswer.getText().length() > 0);
                        registrationQuestionAnswer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                positiveButton.setEnabled(charSequence.length() > 0);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                            }
                        });
                    }
                }
            };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        this.activity = (AgendaActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

        View view = activity.getLayoutInflater().inflate(R.layout.agenda_subscribe_dialog, null);
        ButterKnife.bind(this, view);

        final AgendaItem item = this.activity.getAgendaItem();

        // Issues with restarting when item is not yet loaded on activity.
        // Worst case, this dialog is not visible after activity, but no crash
        if(item == null) return builder.create();

        builder.setTitle("Inschrijven")
                .setView(view)
                .setPositiveButton(R.string.agenda_dialog_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (item.getQuestion() == null || item.getQuestion().isEmpty()) {
                            subscribeWithNote();
                        } else if (item.getPossibleAnswers() == null || item.getPossibleAnswers().length == 0) {
                            subscribeWithOpenQuestion();
                        } else {
                            subscribeWithMultipleChoice();
                        }
                    }
                })
                .setNegativeButton(R.string.agenda_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SubscribeDialogFragment.this.getDialog().cancel();
                    }
                });

        prepareNote();
        prepareQuestion();
        prepareAnsweringMechanism();

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(onDialogShowListener);

        return dialog;
    }

    private void prepareNote() {
        if(this.activity.getAgendaItem().getUserParticipation() != null) {
            note.setText(this.activity.getAgendaItem().getUserParticipation().getNote());
        }
    }

    private void prepareQuestion() {
        if (this.activity.getAgendaItem().getQuestion() != null && !this.activity.getAgendaItem().getQuestion().isEmpty()) {
            registrationQuestion.setText(this.activity.getAgendaItem().getQuestion());
            registrationQuestion.setVisibility(View.VISIBLE);
            registrationQuestionAnswer.setVisibility(View.VISIBLE);
        }
    }

    private void prepareAnsweringMechanism() {
        if (this.activity.getAgendaItem().getPossibleAnswers() != null && this.activity.getAgendaItem().getPossibleAnswers().length > 0) {
            registrationQuestionAnswer.setVisibility(View.GONE);
            possibleAnswersView.setVisibility(View.VISIBLE);
            adapter = new PossibleAnswersAdapter(this.activity.getAgendaItem().getPossibleAnswersAsWilsonItemList());
            adapter.setParentElements(possibleAnswersView, this);
            adapter.setUserParticipation(this.activity.getAgendaItem().getUserParticipation());
            possibleAnswersView.setAdapter(adapter);
            notifySelectionMade();
        } else if (this.activity.getAgendaItem().getUserParticipation() != null &&
                this.activity.getAgendaItem().getUserParticipation().getAnswer() != null &&
                !this.activity.getAgendaItem().getUserParticipation().getAnswer().isEmpty())
        {
            registrationQuestionAnswer.setText(this.activity.getAgendaItem().getUserParticipation().getAnswer());
        }
    }

    public void notifySelectionMade() {
        if (positiveButton != null && adapter.getSelectedValue() != null) {
            positiveButton.setEnabled(true);
        }
    }

    private void subscribeWithNote() {
        if (this.activity.getAgendaItem().getQuestion() == null || this.activity.getAgendaItem().getQuestion().isEmpty()) {
            this.activity.getAgendaItem().getUserParticipation().setNote(note.getText().toString().trim());
            Services.getInstance().agendaService
                    .subscribe(
                            this.activity.getAgendaItem().getId(),
                            note.getText().toString().trim()
                    )
                    .enqueue(agendaSubscribeCallback);
        }
    }

    private void subscribeWithOpenQuestion() {
        if ((this.activity.getAgendaItem().getQuestion() != null || !this.activity.getAgendaItem().getQuestion().isEmpty()) && !registrationQuestionAnswer.getText().toString().trim().isEmpty()) {
            this.activity.getAgendaItem().getUserParticipation().setNote(note.getText().toString().trim());
            this.activity.getAgendaItem().getUserParticipation().setAnswer(registrationQuestionAnswer.getText().toString().trim());
            Services.getInstance().agendaService
                    .subscribe(this.activity.getAgendaItem().getId(),
                            note.getText().toString().trim(),
                            registrationQuestionAnswer.getText().toString().trim()
                    ).enqueue(agendaSubscribeCallback);
        }
    }

    private void subscribeWithMultipleChoice() {
        if (adapter.getSelectedValue() != null) {
            this.activity.getAgendaItem().getUserParticipation().setNote(note.getText().toString().trim());
            this.activity.getAgendaItem().getUserParticipation().setAnswer(adapter.getSelectedValue());
            Services.getInstance().agendaService
                    .subscribe(this.activity.getAgendaItem().getId(),
                            note.getText().toString().trim(),
                            adapter.getSelectedValue()
                    ).enqueue(agendaSubscribeCallback);
        }
    }
}
