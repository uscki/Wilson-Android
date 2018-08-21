package nl.uscki.appcki.android.helpers;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import nl.uscki.appcki.android.R;

public class WrongTextfieldHelper {

    /**
     * Show a message alerting the user a text field should be non-empty by showing
     * a toast notification and highlighting the edit text fragment in question
     * @param context       Context reference
     * @param wrongField    EditText field that should not be empty
     */
    public static void alertEmptyTextfield(Context context, EditText wrongField) {
        Toast.makeText(context, "Veld mag niet leeg zijn", Toast.LENGTH_SHORT).show();
        animateIncorrectView(context, wrongField);
    }

    private static void animateIncorrectView(Context context, final View view) {
        Drawable backgroundFrom = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            backgroundFrom = context.getDrawable(R.drawable.red_border);
        } else {
            context.getResources().getDrawable(R.drawable.red_border);
        }
        Drawable backgroundTo = view.getBackground();

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new DrawableTypeEvaluator(), backgroundFrom, backgroundTo);
        colorAnimator.setDuration(120);
        colorAnimator.setRepeatCount(2);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackground((Drawable) valueAnimator.getAnimatedValue());
            }
        });
        colorAnimator.start();
    }

    public static void alertIncorrectViews(Context context, final List<View> wrongViews) {
        Toast.makeText(context, "Incorrecte waardes", Toast.LENGTH_SHORT).show();
        for(View view : wrongViews) {
            animateIncorrectView(context, view);
        }
    }
}
