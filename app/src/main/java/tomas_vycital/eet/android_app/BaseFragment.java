package tomas_vycital.eet.android_app;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Contains methods used by many fragments
 */
public abstract class BaseFragment extends Fragment {
    protected View layout;

    /**
     * Displays a snackbar (a strip with text at the bottom of the screen) to the user
     *
     * @param text The text displayed to the user
     */
    protected void info(String text) {
        Snackbar.make(this.layout, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    /**
     * Refreshes the content of the fragment
     */
    public void refresh() {
    }

    /**
     * Indicates FAB visibility
     *
     * @return true for visible, false for hidden FAB
     */
    public boolean fab() {
        return false;
    }
}
