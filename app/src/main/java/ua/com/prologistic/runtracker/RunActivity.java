package ua.com.prologistic.runtracker;

import android.support.v4.app.Fragment;

public class RunActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new RunFragment();
    }
}
