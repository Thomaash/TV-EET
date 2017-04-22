package tomas_vycital.eet.android_app;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.TreeSet;

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

    /**
     * Populates radio button group with files from a folder
     *
     * @param group           RadioGroup view to populate
     * @param dirStr          The path to the dir
     * @param filter          Filename filter
     * @param checkedFilename Filename to be checked
     * @param comparator      Comparator for nonstandard ordering
     * @return The number of created radio buttons
     */
    protected int generateRadioButtons(RadioGroup group, String dirStr, FilenameFilter filter, String checkedFilename, Comparator<Object> comparator) {
        group.removeAllViews();
        File dir = new File(dirStr);
        dir.mkdirs();
        File[] files = dir.listFiles(filter);
        TreeSet<String> backups = new TreeSet<>(comparator); // Automatically sorts the newest at the top
        if (files != null) {
            for (File file : files) {
                backups.add(file.getName());
            }
            for (String backup : backups) {
                RadioButton radioButton = new RadioButton(this.getContext());
                radioButton.setText(backup);
                group.addView(radioButton);
                if (backup.equals(checkedFilename)) {
                    radioButton.setChecked(true);
                }
            }
        }

        return backups.size();
    }
}
