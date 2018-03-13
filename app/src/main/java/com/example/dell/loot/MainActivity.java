//package com.example.dell.loot;
//
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.PopupMenu;
//import android.widget.Toast;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//public class MainActivity extends AppCompatActivity {
//
//    FloatingActionButton fab;
//    BottomNavigationView bottomNavigationView;
//    FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        fab = findViewById(R.id.fab);
//        bottomNavigationView = findViewById(R.id.bottom);
//        mAuth = FirebaseAuth.getInstance();
//        bottomNavigationView.getMenu().getItem(1).setChecked(true);
//        loadFragment(new CurrentMission());
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_leader_board:
//                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
//                        loadFragment(new LeaderBoard());
//                        return true;
//                    case R.id.nav_home:
//                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
//                        loadFragment(new CurrentMission());
//                        return true;
//                    case R.id.nav_duel:
//                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
//                        Toast.makeText(MainActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
//                        // TODO Duel functionality
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popup = new PopupMenu(MainActivity.this, view);
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        switch (menuItem.getItemId()) {
//                            case R.id.item_stats:
//                                loadFragment(new Stats());
//                                return true;
//                            case R.id.item_howTo:
//                                loadFragment(new HowTo());
//                                return true;
//                            case R.id.item_help:
//                                loadFragment(new Help());
//                                return true;
//                            case R.id.pop_logout:
//                                mAuth.signOut();
//                                Intent intent=new Intent(getApplicationContext(), Main3Activity.class);
//                                startActivity(intent);
//                                return true;
//                            default:
//                                return false;
//                        }
//                    }
//                });
//                popup.inflate(R.menu.popup_menu);
//                popup.show();
//            }
//        });
//    }
//
//    private void loadFragment(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CurrentMission.LOCATION_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "GPS enabled!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "GPS disabled!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }
//}
