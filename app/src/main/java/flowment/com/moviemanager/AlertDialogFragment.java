package flowment.com.moviemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by "Khaled Reguieg <a href="mailto:Khaled.Reguieg@gmail.com">Khaled Reguieg</a>" )
 * on 29.08.2015.
 */
public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                .setPositiveButton(R.string.error_okBtnText, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
