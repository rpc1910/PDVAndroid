package br.com.rodrigop.pdv.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;


import java.util.List;

import br.com.rodrigop.pdv.R;
import br.com.rodrigop.pdv.domain.model.Produto;
import br.com.rodrigop.pdv.domain.util.Util;
import br.com.rodrigop.pdv.ui.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

public class MapaActivity extends BaseActivity {
    @Bind(R.id.mapview)
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.setCenterCoordinate(new LatLng(-23.5586729,-46.6612236));
        mapView.setZoomLevel(12);

        CursorList cursorList = Query.all(Produto.class).get();

        List<Produto> listaProdutos = cursorList.asList();



        for(Produto produto : listaProdutos) {
            if(produto.getLatitude()+produto.getLongitude() != 0d) {
                Log.d("PRODUTO", produto.toString());
                mapView.addMarker(new MarkerOptions()
                        .position(new LatLng(produto.getLatitude(), produto.getLongitude()))
                        .title(produto.getDescricao())
                        .snippet(Util.getCurrencyValue(produto.getPreco()) + " " + produto.getUnidade()));
            }
        }

        mapView.onCreate(savedInstanceState);
    }

    @OnClick(R.id.btnRuas)
    public void onclickRuas() {
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
    }

    @OnClick(R.id.btnSatelite)
    public void onclickSatelite() {
        mapView.setStyleUrl(Style.SATELLITE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}