package we.nstu.registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import we.nstu.registration.databinding.ActivityMainBinding;

import we.nstu.registration.Event.EventsFragment;
import we.nstu.registration.Lesson.ScheduleFragment;
import we.nstu.registration.Message.MessageFragment;
import we.nstu.registration.News.NewsFragment;
import we.nstu.registration.Profile.ProfileFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            if (currentFragment.equals("NewsFragment")) {
                replaceFragment(new NewsFragment());
            } else if (currentFragment.equals("EventsFragment")) {
                replaceFragment(new EventsFragment());
            } else if (currentFragment.equals("MessageFragment")) {
                replaceFragment(new MessageFragment());
            } else if (currentFragment.equals("ScheduleFragment")) {
                replaceFragment(new ScheduleFragment());
            } else if (currentFragment.equals("ProfileFragment")) {
                replaceFragment(new ProfileFragment());
            }
            else {
                replaceFragment(new NewsFragment());
            }
        }
        else
        {
            replaceFragment(new NewsFragment());
        }

        binding.navigationBar.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.news) {
                replaceFragment(new NewsFragment());
            } else if (itemId == R.id.events) {
                replaceFragment(new EventsFragment());
            } else if (itemId == R.id.messages) {
                replaceFragment(new MessageFragment());
            } else if (itemId == R.id.schedule) {
                replaceFragment(new ScheduleFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            saveCurrentFragment(Objects.requireNonNull(item.getTitle()).toString());
            return true;
        });
    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public static void saveEmail(Context context, String email)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.apply();
    }
    public static String getEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", null);
    }

    public static void clearEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void saveCurrentFragment(String fragmentName) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentFragment", fragmentName);
        editor.apply();
    }
    private String getCurrentFragment() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("currentFragment", null);
    }
}







