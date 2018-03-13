package com.example.dell.loot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends Fragment implements View.OnClickListener{

    private FirebaseAuth mAuth;
//    private FirebaseDatabase database;
//    DatabaseReference users;
    View view;
    EditText name, email, contact, zeal, username, password;
    ImageView avatars[]=new ImageView[5];
    ImageView tick[]=new ImageView[5];
    int avatarIds[]=new int[5];
    ProgressDialog dialog;
    FirebaseFirestore db;
    int selectedAvatar;
    public Register() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
//        database = FirebaseDatabase.getInstance();
//        users = database.getReference("Users");
        Button register = getView().findViewById(R.id.submit);

        dialog.setTitle("Please Wait");
        dialog.setCancelable(false);
        dialog.setMessage("Signing in...");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validation())
                {
                    Toast.makeText(getActivity(),"Please fill in all the field",Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        updateFirebase(firebaseUser);
                                        Toast.makeText(getContext(), "You're registered successfully!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getContext(), WelcomeSlider.class);
                                        startActivity(i);
//                                    StringRequest register = new StringRequest(Request.Method.POST, Endpoints.register,
//                                            new Response.Listener<String>() {
//                                                @Override
//                                                public void onResponse(String response) {
//                                                    Toast.makeText(getContext(),"You're registered successfully!",Toast.LENGTH_SHORT).show();
//                                                    Intent i=new Intent(getContext(),WelcomeSlider.class);
//                                                    startActivity(i);
//                                                }
//                                            },
//                                            new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError error) {
//
//                                                }
//                                            }){
//                                        @Override
//                                        protected Map<String, String> getParams() throws AuthFailureError {
//                                            Map<String, String> map = new HashMap();
//                                            map.put("", firebaseUser.getUid());
//                                            map.put("", email.getText().toString());
//                                            map.put("", name.getText().toString());
//                                            map.put("", username.getText().toString());
//                                            map.put("", zeal.getText().toString());
//                                            map.put("", contact.getText().toString());
//                                            return map;
//                                        }
//                                    };
//                                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//                                    requestQueue.add(
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Registration failed." + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initializeViews() {
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        contact = view.findViewById(R.id.contactNumber);
        zeal = view.findViewById(R.id.zealId);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);


        avatars[0]=view.findViewById(R.id.avatar_1);
        avatars[1]=view.findViewById(R.id.avatar_2);
        avatars[2]=view.findViewById(R.id.avatar_3);
        avatars[3]=view.findViewById(R.id.avatar_4);
        avatars[4]=view.findViewById(R.id.avatar_5);

        for (int x=0;x<5;x++)
        {
            avatars[x].setOnClickListener(this);
        }

        tick[0]=view.findViewById(R.id.tick_1);
        tick[1]=view.findViewById(R.id.tick_2);
        tick[2]=view.findViewById(R.id.tick_3);
        tick[3]=view.findViewById(R.id.tick_4);
        tick[4]=view.findViewById(R.id.tick_5);


        avatarIds[0]=R.drawable.avatar_1;
        avatarIds[1]=R.drawable.avatar_2;
        avatarIds[2]=R.drawable.avatar_3;
        avatarIds[3]=R.drawable.avatar_4;
        avatarIds[4]=R.drawable.avatar_5;

        dialog=new ProgressDialog(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

//    private User getUser() {
//        View view = getView();
//        EditText username = view.findViewById(R.id.username);
//        EditText zealId = view.findViewById(R.id.zealId);
//        EditText name = view.findViewById(R.id.name);
//        EditText contact = view.findViewById(R.id.contact);
//        EditText email = view.findViewById(R.id.email);
//        EditText password = view.findViewById(R.id.password);
//        ImageView avtar = view.findViewById(R.id.avatar);
//        User user = new User();
////        user.setAvatarID((int)avtar.getTag());
//        user.setContactNumber(Long.parseLong(contact.getText()+""));
//        user.setEmail(email.getText()+"");
//        user.setName(name.getText()+"");
//        user.setScore(0);
//        user.setPassword(password.getText()+"");
//        user.setZealID(zealId.getText()+"");
//        user.setUsername(username.getText()+"");
//        return  user;
//    }
//
//    private void updateDB(User user) {
//        users.child(user.getUserID()).setValue(user);
//        Intent i=new Intent(getContext(),Main2Activity.class);
//        i.putExtra("UID",user.getUserID());
//        startActivity(i);
//    }

    public void login() {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Login fragment = new Login();
        fragmentTransaction.replace(R.id.login_frame, fragment);
        fragmentTransaction.commit();
    }
    public void updateFirebase(FirebaseUser firebaseUser)
    {
        Map<String, Object> user = new HashMap<>();
        user.put("userID", firebaseUser.getUid());
        user.put("online", false);

        db.collection("users").document(firebaseUser.getUid())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Log.i("Added Succesfully","");
                }
                else
                {
                    Log.i("Error",task.getException().getMessage());
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        int id=view.getId();
        for(int i=0;i<5;i++)
        {
            if(avatars[i].getId()==id)
            {
                tick[selectedAvatar].setVisibility(View.GONE);
                tick[i].setVisibility(View.VISIBLE);
                //TODO:Update avatarId
                selectedAvatar=i;
            }
        }

    }

    private boolean validation()
    {
        boolean validate=true;
        if(name.getText()==null||name.getText().toString().trim().length()==0)
        {
            validate=false;
        }
        else if(email.getText()==null||email.getText().toString().trim().length()==0)
        {
            validate=false;
        }
        else if(zeal.getText()==null||zeal.getText().toString().trim().length()==0)
        {
            validate=false;
        }
        else if(contact.getText()==null||contact.getText().toString().trim().length()==0)
        {
            validate=false;
        }
        else if(password.getText()==null||password.getText().toString().trim().length()==0)
        {
            validate=false;
        }
        else if(username.getText()==null||username.getText().toString().trim().length()==0)
        {
            validate=false;
        }
        return validate;

    }
}
