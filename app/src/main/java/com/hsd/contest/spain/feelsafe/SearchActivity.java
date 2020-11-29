package com.hsd.contest.spain.feelsafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        GlobalVariables.setSelectedPosition(position);
        Toast.makeText(getApplicationContext(), item + " " + position, Toast.LENGTH_LONG).show();

        startActivity(new Intent(SearchActivity.this, MapsActivity.class));
    }

    EditText search_query;
    TextView response_text;
    Button search_button;
    ListView listview;

    private SearchService searchService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_query = (EditText) findViewById(R.id.search_query);
        //response_text = (TextView) findViewById(R.id.response);
        listview = (ListView) findViewById(R.id.list);
        search_button = (Button) findViewById(R.id.button_search);

        try {
            searchService = SearchServiceFactory.create(this, URLEncoder.encode("CgB6e3x9MPbI8iITjvfTfjI82nRwFI0Y7vFnXcFUeM8TdYtCWhs6L6JP+417gXvM4kZaC2pEr5lUp5uKUU/SxZxo", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("Error:", "encode apikey error");
        }

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(v);
            }
        });
    }

    public void search(View view) {
        TextSearchRequest textSearchRequest = new TextSearchRequest();
        textSearchRequest.setQuery(search_query.getText().toString());
        searchService.textSearch(textSearchRequest, new SearchResultListener<TextSearchResponse>() {
            @Override
            public void onSearchResult(TextSearchResponse textSearchResponse) {
                List<Site> siteList;
                if (textSearchResponse == null || textSearchResponse.getTotalCount() <= 0 || (siteList = textSearchResponse.getSites()) == null
                        || ((List) siteList).size() <= 0) {

                    Toast.makeText(getApplicationContext(), "No hay resultados en la busqueda.", Toast.LENGTH_LONG).show();
                    return;
                }

                ArrayList <String> res = new ArrayList<String>(); //para crear la lista a mostrar en pantalla
                AddressDetail addressDetail;

                if (GlobalVariables.places.size() > 0) GlobalVariables.places.clear();
                for (Site site : textSearchResponse.getSites()) {
                    addressDetail = site.getAddress();
                    res.add(site.getName() + " " + site.getFormatAddress() + " " + addressDetail.getCountry());
                    GlobalVariables.places.add(new GlobalVariables.ListItem(site.getName(), new LatLng(site.getLocation().getLat(),site.getLocation().getLng())));
                }

                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, res);
                listview.setAdapter(listAdapter);

                listview.setOnItemClickListener(SearchActivity.this);
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
                Log.e("Error: ", "onSearchError is: " + searchStatus.getErrorCode());
            }
        });
    }
}