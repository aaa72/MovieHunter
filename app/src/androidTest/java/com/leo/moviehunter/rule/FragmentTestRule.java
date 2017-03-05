package com.leo.moviehunter.rule;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.test.rule.ActivityTestRule;

import com.leo.moviehunter.R;
import com.leo.moviehunter.activity.TestFragmentActivity;

import org.junit.Assert;

public class FragmentTestRule<F extends Fragment> extends ActivityTestRule<TestFragmentActivity> {

    private final Class<F> mFragmentClass;
    private F mFragment;

    public FragmentTestRule(final Class<F> fragmentClass) {
        super(TestFragmentActivity.class, true, false);
        mFragmentClass = fragmentClass;
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();

        try {
            //Instantiate and insert the fragment into the container layout
            FragmentManager manager = getActivity().getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            mFragment = mFragmentClass.newInstance();
            transaction.replace(R.id.container, mFragment);
            transaction.commit();
        } catch (InstantiationException | IllegalAccessException e) {
            Assert.fail(String.format("%s: Could not insert %s into TestFragmentActivity: %s",
                    getClass().getSimpleName(),
                    mFragmentClass.getSimpleName(),
                    e.getMessage()));
        }
    }
    public F getFragment(){
        return mFragment;
    }
}