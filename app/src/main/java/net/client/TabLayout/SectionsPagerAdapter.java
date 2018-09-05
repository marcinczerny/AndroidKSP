package net.client.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import net.client.TabLayout.TabView.ConfigurationFragment;
import net.client.TabLayout.TabView.GraphFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> fr_list;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

        ArrayList<Fragment> fr_list = new ArrayList<Fragment>();
        fr_list.add(new ConfigurationFragment());
        fr_list.add(new GraphFragment());

        this.fr_list = fr_list;
        mFragmentTags = new HashMap<Integer,String>();
    }
    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    @Override
    public Fragment getItem(int position) {
        return fr_list.get(position);
    }

    @Override
    public int getCount() {
        return fr_list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Konfiguracja";
            case 1:
                return "Wykres";
        }
        return null;
    }
}