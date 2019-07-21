package tk.dalpiazsolutions.fuelpricesurveillance;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import tk.dalpiazsolutions.fuelpricesurveillance.TabArticles;
import tk.dalpiazsolutions.fuelpricesurveillance.TabFuel;

public class PagerAdapter extends FragmentPagerAdapter {

    int numberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs)
    {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }
    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                TabFuel tabFuel = new TabFuel();
                return tabFuel;
            case 1:
                TabArticles tabArticles = new TabArticles();
                return tabArticles;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
