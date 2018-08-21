package nl.uscki.appcki.android.helpers;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.widget.Toast;

import nl.uscki.appcki.android.R;

public class WrongTextfieldHelper {

    /**
     * Show a message alerting the user a text field should be non-empty by showing
     * a toast notification and highlighting the edit text fragment in question
     * @param context       Context reference
     * @param wrongField    EditText field that should not be empty
     */
    public static void alertEmptyTextfield(Context context, final EditText wrongField) {
        Toast.makeText(context, "Veld mag niet leeg zijn", Toast.LENGTH_SHORT).show();

        Drawable backgroundFrom = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            backgroundFrom = context.getDrawable(R.drawable.red_border);
        } else {
            context.getResources().getDrawable(R.drawable.red_border);
        }
        Drawable backgroundTo = wrongField.getBackground();

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new DrawableTypeEvaluator(), backgroundFrom, backgroundTo);
        colorAnimator.setDuration(120);
        colorAnimator.setRepeatCount(2);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                wrongField.setBackground((Drawable) valueAnimator.getAnimatedValue());
            }
        });
        colorAnimator.start();
    }
}
