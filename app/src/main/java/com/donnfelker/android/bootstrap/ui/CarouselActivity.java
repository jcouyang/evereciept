

package com.donnfelker.android.bootstrap.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.donnfelker.android.bootstrap.BootstrapServiceProvider;
import com.donnfelker.android.bootstrap.R;
import com.donnfelker.android.bootstrap.core.BootstrapService;
import com.donnfelker.android.bootstrap.evernote.EvernoteSessionActivity;
import com.donnfelker.android.bootstrap.util.Ln;
import com.donnfelker.android.bootstrap.util.SafeAsyncTask;
import com.viewpagerindicator.TitlePageIndicator;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

import net.simonvt.menudrawer.MenuDrawer;
import com.evernote.client.android.EvernoteSession;

/**
 * Activity to view the carousel and view pager indicator with fragments.
 */
public class CarouselActivity extends EvernoteSessionActivity {

    @InjectView(R.id.tpi_header)
    TitlePageIndicator indicator;
    @InjectView(R.id.vp_pages)
    ViewPager pager;

    @Inject
    BootstrapServiceProvider serviceProvider;

    private MenuDrawer menuDrawer;

    private boolean userHasAuthenticated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        // Set up navigation drawer
        menuDrawer = MenuDrawer.attach(this);
        menuDrawer.setMenuView(R.layout.navigation_drawer);
        menuDrawer.setContentView(R.layout.carousel_view);
        menuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        menuDrawer.setDrawerIndicatorEnabled(true);

        Views.inject(this);
        checkAuth();

    }

    private void initScreen() {
        if (userHasAuthenticated) {
            Ln.d("evernote setup pager");
            pager.setAdapter(new BootstrapPagerAdapter(getResources(), getSupportFragmentManager()));
            Ln.d("evernote inject pager");
            indicator.setViewPager(pager);
            pager.setCurrentItem(1);
        }

        setNavListeners();
    }

    private void checkAuth() {
        if (mEvernoteSession.isLoggedIn()) {
            Ln.d("evernote is loged in");
            userHasAuthenticated = true;
            initScreen();

        } else {
            Ln.d("evernote is loging now");
            mEvernoteSession.authenticate(this);
        }
    }


    private void setNavListeners() {

        menuDrawer.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDrawer.toggleMenu();
            }
        });

        menuDrawer.findViewById(R.id.timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDrawer.toggleMenu();
                navigateToTimer();
            }
        });

    }

    /**
     * Called when the control returns from an activity that we launched.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Ln.d("maybe evernote auth done");
        switch (requestCode) {
            //Update UI when oauth activity returns result
            case EvernoteSession.REQUEST_CODE_OAUTH:
                if (resultCode == Activity.RESULT_OK) {
                    Ln.d("evernote auth done");
                    initScreen();
                }
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menuDrawer.toggleMenu();
                return true;
            case R.id.timer:
                navigateToTimer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToTimer() {
        final Intent i = new Intent(this, BootstrapTimerActivity.class);
        startActivity(i);
    }
}
