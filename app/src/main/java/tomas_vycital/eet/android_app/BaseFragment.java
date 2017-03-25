package tomas_vycital.eet.android_app;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment implements RefreshableFragment {
    protected View layout;

    protected void info(String text) {
        Snackbar.make(this.layout, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
