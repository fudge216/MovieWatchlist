package com.zybooks.movie_watchlist;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragSearch extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragSearch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragSearch newInstance(String param1, String param2) {
        FragSearch fragment = new FragSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_frag_search, container, false);

        //creates widget variables
        EditText editText = view.findViewById(R.id.inputMovieName);
        TextView moviesTv = view.findViewById(R.id.searchinfo);
        Button movieResults = view.findViewById(R.id.resultsbtn);
        Button addtoWatchlist= view.findViewById(R.id.btnAddtoWatchlist);
        ImageView imageView=  view.findViewById(R.id.movie_poster);
        String fileName = "WatchList.txt";
        String filepath = "myFileDir";




        movieResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sets edit text to string to add to url
                String movieName = editText.getText().toString();
                // edit text in url to create full api url
                String url = "http://www.omdbapi.com/?t=" +movieName+"&apikey=9cf6f88c";

                //volley api call using Json object
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplication().getApplicationContext());
                // creates json object to pull data from
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //fills variables with api data
                        try {
                            String title =response.getString("Title");
                            String release =response.getString("Released");
                            String runtime =response.getString("Runtime");
                            String poster = response.getString("Poster");
                            String plot = response.getString("Plot");
                            String rating = "";
                            // retrieves rating data from nested json
                            JSONArray jsonArray =response.getJSONArray("Ratings");
                            for(int i = 0; i< jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                rating = jsonObject.getString("Value");

                            }
                            //sets text to contain all movie data
                            moviesTv.setText("Movie Title: "+title+"\nRelease Date: "+release+"\nRuntime:" +
                                    runtime+"\nRaiting:"+ rating+"\n"+ plot);

                            //loads the poster image data into imageview
                            Glide.with(getActivity().getApplication().getApplicationContext())
                                    .load(poster)
                                    .into(imageView);


                        } catch (JSONException e) {
                            moviesTv.setText("Failed to pull data.");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        moviesTv.setText("Failed to pull data.");
                    }

                });
                //sent back to be displayed
                queue.add(jsonObjectRequest);
            }

        });

        addtoWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplication().getApplicationContext(),"Movie saved to watchlist",Toast.LENGTH_SHORT).show();

                // the intent here was to have the "add to watchlist" button create the url like in the prior method
                // but save it to a file to be read by the watchlist fragment to create cards with watchlist data



                //                String movieName = editText.getText().toString();
//                String url = (String) "http://www.omdbapi.com/?t="+movieName+"&apikey=9cf6f88c";
//                String fileName = "Watchlist.txt";
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),fileName);
//
//
//                try {
//
//                    FileOutputStream fos = new FileOutputStream(file);
//                    fos.write(url.getBytes());
//                    fos.close();
//
//                    Toast.makeText(getActivity().getApplication().getApplicationContext(),"textsaved",Toast.LENGTH_SHORT).show();
//                } catch (FileNotFoundException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e){
//                    e.printStackTrace();
//                }
//

            }

            public boolean checkPermission(String permission){
                int check = ContextCompat.checkSelfPermission(getActivity().getApplication().getApplicationContext(),permission);
                return (check==PackageManager.PERMISSION_GRANTED);
            }
        });

        return view;
    }
}