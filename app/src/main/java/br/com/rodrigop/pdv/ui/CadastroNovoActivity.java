package br.com.rodrigop.pdv.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import br.com.rodrigop.pdv.R;
import butterknife.Bind;

public class CadastroNovoActivity extends BaseActivity {

    @Bind(R.id.editTextDescricao) EditText campoDescricao;
    @Bind(R.id.editTextUnidade) EditText campoUnidade;
    @Bind(R.id.editTextPreco) EditText campoPreco;
    @Bind(R.id.editTextCodigoDeBarras) EditText campoCodigoDeBarras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_novo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Cadastro", campoDescricao.getText().toString());
            }
        });
    }






}
