package com.chads.vanroomies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.chads.vanroomies.databinding.ActivityHomeBinding;

// ChatGPT usage: No
public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ProfileFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(choice -> {
            if (choice.getItemId() == R.id.menu_listings){
                replaceFragment(new ListingsFragment());
            }
            else if (choice.getItemId() == R.id.menu_profile){
                replaceFragment(new ProfileFragment());
            }
            else if (choice.getItemId() == R.id.menu_matches){
                replaceFragment(new MatchesFragment());
            }
            else if (choice.getItemId() == R.id.menu_chat){
                replaceFragment(new ChatFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
         FragmentManager fragmentManager = getSupportFragmentManager();
         FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         fragmentTransaction.replace(R.id.frame_layout, fragment);
         fragmentTransaction.commit();
    }
}