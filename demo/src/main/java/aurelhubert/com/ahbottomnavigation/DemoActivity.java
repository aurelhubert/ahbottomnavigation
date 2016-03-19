package aurelhubert.com.ahbottomnavigation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.HideOnScroll;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
	}

	/**
	 * Init UI
	 */
	private void initUI() {

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		final AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		final ArrayList<AHBottomNavigationItem> items = new ArrayList<>();

		AHBottomNavigationItem item1 = new AHBottomNavigationItem("Label One", R.drawable.ic_maps_place, Color.parseColor("#455C65"));
		AHBottomNavigationItem item2 = new AHBottomNavigationItem("Label Two", R.drawable.ic_maps_local_bar, Color.parseColor("#00886A"));
		AHBottomNavigationItem item3 = new AHBottomNavigationItem("Label Three", R.drawable.ic_maps_local_restaurant, Color.parseColor("#8B6B62"));
		final AHBottomNavigationItem item4 = new AHBottomNavigationItem("Label Four", R.drawable.ic_maps_local_bar, Color.parseColor("#6C4A42"));
		final AHBottomNavigationItem item5 = new AHBottomNavigationItem("Label Five", R.drawable.ic_maps_place, Color.parseColor("#F63D2B"));

		items.add(item1);
		items.add(item2);
		items.add(item3);

		bottomNavigation.addItems(items);
		bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
		bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
		// Use colored navigation with circle reveal effect
		bottomNavigation.setColored(true);


		List<ItemData> itemsData = new ArrayList<ItemData>();
		for(int i = 0; i < 50; i++)
		{
			itemsData.add(new ItemData("Text " + i));
		}

		recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		MyAdapter mAdapter = new MyAdapter(itemsData);
		recyclerView.setAdapter(mAdapter);
		recyclerView.addOnScrollListener(new HideOnScroll.ShowHideBottomBarOnScrollingListener(bottomNavigation, toolbar));
	}
}
