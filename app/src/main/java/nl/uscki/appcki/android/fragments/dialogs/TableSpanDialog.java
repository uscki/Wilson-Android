package nl.uscki.appcki.android.fragments.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.elements.GenericElement;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * Created by peter on 4/5/17.
 */

public class TableSpanDialog extends android.support.v4.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_table_dialog, null);

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.table_dialog_table);
        createTable(tableLayout);

        builder.setView(view).setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDialog().cancel();
            }
        });
        return builder.create();
    }

    private TableRow createRow(List<Object> columns) {
        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        for (Object o : columns) {
            BBTextView view = new BBTextView(row.getContext());
            view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_cell_border));
            GenericElement element = GenericElement.fromLinkedTreeUnit((LinkedTreeMap) o);
            SpannableStringBuilder str = Parser.parse(element.getContent(), true, view);
            view.setText(str);

            row.addView(view);
        }

        return row;
    }

    private void createTable(TableLayout layout) {
        if (getArguments().getSerializable("table") instanceof ArrayList) {
            for (Object o : (List)getArguments().getSerializable("table")) {
                List<Object> columns = GenericElement.fromLinkedTreeUnit((LinkedTreeMap) o).getContent();
                Log.e("TableDialog", columns.size() + "");
                layout.addView(createRow(columns));
            }
        }
    }
}
