package br.com.rodrigop.pdv.ui;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

import br.com.rodrigop.pdv.R;
import br.com.rodrigop.pdv.domain.model.Produto;
import br.com.rodrigop.pdv.domain.network.APIClient;
import br.com.rodrigop.pdv.domain.util.Base64Util;
import br.com.rodrigop.pdv.domain.util.ImageInputHelper;
import butterknife.Bind;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.sprinkles.Query;

public class CadastroNovoActivity extends BaseActivity implements ImageInputHelper.ImageActionListener {

    @Bind(R.id.editTextDescricao) EditText campoDescricao;
    @Bind(R.id.editTextUnidade) EditText campoUnidade;
    @Bind(R.id.editTextPreco) EditText campoPreco;
    @Bind(R.id.editTextCodigoDeBarras) EditText campoCodigoDeBarras;

    @Bind(R.id.imageButtonCamera) ImageButton imageButtonCamera;
    @Bind(R.id.imageButtonGaleria) ImageButton imageButtonGaleria;
    @Bind(R.id.imageViewFotoProduto) ImageView imageViewFotoProduto;

    private ZXingLibConfig zxingLibConfig;

    private ImageInputHelper imageInputHelper;

    private Produto produto;

    private double latitude = 0.0d;
    private double longitude = 0.0d;

    Callback<String> callbackNovoProduto;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_novo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Instancia configuração do leitor de códigos de barra
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;

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

        Log.d("LATITUDE", "" + latitude);
        Log.d("LONGITUDE", "" + longitude);

        imageInputHelper = new ImageInputHelper(this);
        imageInputHelper.setImageActionListener(this);

        configureNovoProdutoCallback();

        dialog = new SpotsDialog(this, "Gravando no servidor");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar();
            }
        });

        /*
        campoCodigoDeBarras.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                IntentIntegrator.initiateScan(CadastroNovoActivity.this, zxingLibConfig);
            }
        });
        */

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

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (scanResult == null) { return;  }

                String result = scanResult.getContents();

                if(result != null) {
                    Log.d("SCANBARCODE","BarCode: "+result);
                    campoCodigoDeBarras.setText(result);
                }
                break;

            default:
        }
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

    private void salvar() {
        dialog.show();
        produto = new Produto();

        produto.setId(0L);
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
        produto.setStatus(0);

        produto.save();

        // Envia produto para o servidor

        new APIClient().getRestService().createProduto(
                produto.getCodigoBarras(),
                produto.getDescricao(),
                produto.getUnidade(),
                produto.getPreco(),
                produto.getFoto(),
                produto.getStatus(),
                produto.getLatitude(),
                produto.getLongitude(),
                callbackNovoProduto
        );
    }


    private void configureNovoProdutoCallback() {

        callbackNovoProduto = new Callback<String>() {

            @Override public void success(String resultado, Response response) {
                dialog.dismiss();
                finish();
            }

            @Override public void failure(RetrofitError error) {
                Log.e("RETROFIT", "Error:"+error.getMessage());
                dialog.dismiss();

                Toast.makeText(getApplicationContext(), "Não foi possível adicionar o produto no servidor", Toast.LENGTH_SHORT).show();

                // finish();
            }
        };
    }
}
