package br.com.rodrigop.pdv.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import java.io.File;
import java.io.IOException;

import br.com.rodrigop.pdv.R;
import br.com.rodrigop.pdv.domain.model.Produto;
import br.com.rodrigop.pdv.domain.util.Base64Util;
import br.com.rodrigop.pdv.domain.util.ImageInputHelper;
import butterknife.Bind;
import butterknife.OnClick;

public class CadastroNovoActivity extends BaseActivity implements ImageInputHelper.ImageActionListener {

    @Bind(R.id.editTextDescricao) EditText campoDescricao;
    @Bind(R.id.editTextUnidade) EditText campoUnidade;
    @Bind(R.id.editTextPreco) EditText campoPreco;
    @Bind(R.id.editTextCodigoDeBarras) EditText campoCodigoDeBarras;

    @Bind(R.id.imageButtonCamera) ImageButton imageButtonCamera;
    @Bind(R.id.imageButtonGaleria) ImageButton imageButtonGaleria;
    @Bind(R.id.imageViewFotoProduto) ImageView imageViewFotoProduto;


    private ImageInputHelper imageInputHelper;

    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_novo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageInputHelper = new ImageInputHelper(this);
        imageInputHelper.setImageActionListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar();
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

    private void salvar() {
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

        produto.save();

        finish();
    }

}
