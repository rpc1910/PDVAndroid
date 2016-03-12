package br.com.rodrigop.pdv.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.rodrigop.pdv.R;
import br.com.rodrigop.pdv.domain.model.Produto;
import br.com.rodrigop.pdv.domain.util.Base64Util;
import br.com.rodrigop.pdv.domain.util.ImageInputHelper;
import br.com.rodrigop.pdv.domain.util.SpinnerOption;
import butterknife.Bind;
import butterknife.OnClick;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

public class EditarProdutoActivity extends BaseActivity implements ImageInputHelper.ImageActionListener {

    @Bind(R.id.spinner) Spinner spinner;

    @Bind(R.id.editTextDescricao) EditText campoDescricao;
    @Bind(R.id.editTextUnidade) EditText campoUnidade;
    @Bind(R.id.editTextPreco) EditText campoPreco;
    @Bind(R.id.editTextCodigoDeBarras) EditText campoCodigoDeBarras;

    @Bind(R.id.imageButtonCamera) ImageButton imageButtonCamera;
    @Bind(R.id.imageButtonGaleria) ImageButton imageButtonGaleria;
    @Bind(R.id.imageViewFotoProduto) ImageView imageViewFotoProduto;

    private Produto produto;

    private ImageInputHelper imageInputHelper;

    private double latitude = 0.0d;
    private double longitude = 0.0d;

    private String teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Conecta com a API de geolocalização
        LostApiClient lostApiClient = new LostApiClient.Builder(this).build();
        lostApiClient.connect();

        Location location = LocationServices.FusedLocationApi.getLastLocation();
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        // Define tempo de atualização da localização
        LocationRequest request = LocationRequest.create()
                .setInterval(5000)
                .setSmallestDisplacement(10)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };

        LocationServices.FusedLocationApi.requestLocationUpdates(request, listener);

        imageInputHelper = new ImageInputHelper(this);
        imageInputHelper.setImageActionListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar(view);
            }
        });

        List<Produto> produtos = Query.many(Produto.class, "SELECT * FROM produto ORDER BY codigo_barra ASC").get().asList();

        // Copiado do professor
        produto = new Produto();

        List<String> barcodeList = new ArrayList<>();

        // Alterado por Rodrigo
        List<SpinnerOption> barcodeListOption = new ArrayList<>();


        for(Produto produto: produtos){
            barcodeList.add(produto.getCodigoBarras());

            barcodeListOption.add(new SpinnerOption(produto.getCodigoBarras(), produto.getDescricao()));
        }

//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
//                (this, android.R.layout.simple_spinner_item,barcodeList);

        ArrayAdapter<SpinnerOption> dataAdapter = new ArrayAdapter<SpinnerOption>
                (this, android.R.layout.simple_spinner_item,barcodeListOption);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption option = (SpinnerOption) parent.getItemAtPosition(position);
                //String barCode = parent.getItemAtPosition(position).toString();
                String barCode = option.getCodigoDeBarras();

                Log.d("BARCODE", barCode);

                // Seleciona o produto no banco de dados
                produto = Query.one(Produto.class, "SELECT * FROM produto WHERE codigo_barra = ?", barCode).get();

                if(produto != null) {
                    campoDescricao.setText( produto.getDescricao() );
                    campoUnidade.setText( produto.getUnidade() );
                    campoPreco.setText( String.valueOf(produto.getPreco()) );
                    campoCodigoDeBarras.setText( produto.getCodigoBarras() );
                    campoDescricao.setText( produto.getDescricao() );

                    imageViewFotoProduto.setImageBitmap( Base64Util.decodeBase64(produto.getFoto()) );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @OnClick(R.id.imageButtonGaleria)
    public void onClickGaleria() {
        imageInputHelper.selectImageFromGallery();
    }

    @OnClick(R.id.imageButtonCamera)
    public void onClickCamera() {
        imageInputHelper.takePhotoWithCamera();
    }

    //    Métodos utilizados para o retorno dos dados da foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageInputHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onImageSelectedFromGallery(Uri uri, File imageFile) {
        // cropping the selected image. crop intent will have aspect ratio 16/9 and result image
        // will have size 800x450
        imageInputHelper.requestCropImage(uri, 100, 100, 0, 0);
    }

    @Override
    public void onImageTakenFromCamera(Uri uri, File imageFile) {
        // cropping the taken photo. crop intent will have aspect ratio 16/9 and result image
        // will have size 800x450
        imageInputHelper.requestCropImage(uri, 100, 100, 1, 1);
    }

    @Override
    public void onImageCropped(Uri uri, File imageFile) {
        try {
            // getting bitmap from uri
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            imageViewFotoProduto.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvar(View view) {
        produto.setDescricao(campoDescricao.getText().toString());
        produto.setUnidade(campoUnidade.getText().toString());
        produto.setCodigoBarras(campoCodigoDeBarras.getText().toString());

        String conteudoPreco = campoPreco.getText().toString();

        if(!conteudoPreco.equals("")) {
            produto.setPreco(Double.parseDouble(conteudoPreco));
        }
        else {
            produto.setPreco(0.0);
        }

        Bitmap imagem = ((BitmapDrawable)imageViewFotoProduto.getDrawable()).getBitmap();
        produto.setFoto(Base64Util.encodeTobase64(imagem));

        produto.setLatitude(latitude);
        produto.setLongitude(longitude);

        produto.save();

        Snackbar.make(view, "Produto alterado com sucesso!", Snackbar.LENGTH_SHORT).show();

        //finish();
    }

}
