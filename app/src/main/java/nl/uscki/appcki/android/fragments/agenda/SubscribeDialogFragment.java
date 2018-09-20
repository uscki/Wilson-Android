package nl.uscki.appcki.android.fragments.agenda;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
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

    private AgendaItem item;

    private Callback<AgendaParticipantLists> agendaSubscribeCallback =
        new Callback<AgendaParticipantLists>() {
            @Override
            public void onSucces(Response<AgendaParticipantLists> response) {
                EventBus.getDefault()
                        .post(new AgendaItemSubscribedEvent(
                                response.body(),
                                false
                        ));
            }
        };

    private DialogInterface.OnShowListener onDialogShowListener =
            new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    final AlertDialog dialog = (AlertDialog) dialogInterface;
                    if(item.getQuestion() != null && !item.getQuestion().isEmpty()) {
                        positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setEnabled(false);
                        registrationQuestionAnswer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                positiveButton.setEnabled(charSequence.length() > 0);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) { }
                        });
                    }
                }
            };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.agenda_subscribe_dialog, null);
        ButterKnife.bind(this, view);

        item = (AgendaItem) getArguments().getSerializable("agenda_item");

        builder.setTitle("Inschrijven")
                .setView(view)
                .setPositiveButton(R.string.agenda_dialog_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(item.getQuestion() == null || item.getQuestion().isEmpty()) {
                            subscribeWithNote();
                        } else if(item.getPossibleAnswers() == null || item.getPossibleAnswers().length == 0) {
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

        if(item.getQuestion() != null && item.getQuestion() != "") {
            registrationQuestion.setText(item.getQuestion());
            registrationQuestion.setVisibility(View.VISIBLE);
            registrationQuestionAnswer.setVisibility(View.VISIBLE);
        }

        if(item.getPossibleAnswers() != null && item.getPossibleAnswers().length > 0) {
            registrationQuestionAnswer.setVisibility(View.GONE);
            possibleAnswersView.setVisibility(View.VISIBLE);
            adapter = new PossibleAnswersAdapter(item.getPossibleAnswersAsWilsonItemList());
            adapter.setParentElements(possibleAnswersView, this);
            possibleAnswersView.setAdapter(adapter);
        }

        AlertDialog dialog =  builder.create();

        dialog.setOnShowListener(onDialogShowListener);

        return dialog;
    }

    public void notifySelectionMade() {
        if(positiveButton != null && adapter.getSelectedValue() != null) {
            positiveButton.setEnabled(true);
        }
    }

    private void subscribeWithNote() {
        if(item.getQuestion() == null || item.getQuestion().isEmpty()) {
            Services.getInstance().agendaService
                    .subscribe(item.getId(),
                            note.getText().toString().trim()
                    ).enqueue(agendaSubscribeCallback);
        }
    }

    private void subscribeWithOpenQuestion() {
        if((item.getQuestion() != null || !item.getQuestion().isEmpty()) && !registrationQuestionAnswer.getText().toString().trim().isEmpty()) {
            Services.getInstance().agendaService
                    .subscribe(item.getId(),
                            note.getText().toString().trim(),
                            registrationQuestionAnswer.getText().toString().trim()
                    ).enqueue(agendaSubscribeCallback);
        }
    }

    private void subscribeWithMultipleChoice() {
        if(adapter.getSelectedValue() != null) {
            Services.getInstance().agendaService
                    .subscribe(item.getId(),
                            note.getText().toString().trim(),
                            adapter.getSelectedValue()
                    ).enqueue(agendaSubscribeCallback);
        }
    }
}
