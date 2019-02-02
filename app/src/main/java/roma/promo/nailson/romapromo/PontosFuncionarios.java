package roma.promo.nailson.romapromo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.PipedOutputStream;

public class PontosFuncionarios extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView listaFuncionarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pontos_funcionarios);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Escolha um funcionario");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(PontosFuncionarios.this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuario");
        listaFuncionarios = (RecyclerView) findViewById(R.id.listafuncionarios);
        listaFuncionarios.setLayoutManager(new LinearLayoutManager(this));
        listaFuncionarios.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Pessoa, PontosFuncionarios.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, PontosFuncionarios.AnuncioViewHolder>(
                Pessoa.class,
                R.layout.item_funcionario,
                PontosFuncionarios.AnuncioViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(PontosFuncionarios.AnuncioViewHolder viewHolder, final Pessoa model, int position) {

                final String chave_acao = getRef(position).getKey();
                viewHolder.setNome(model.getNome());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (FirebaseAuth.getInstance().getCurrentUser() == null){

                            Intent telaEntrega = new Intent(PontosFuncionarios.this, LoginUsuario.class);
                            startActivity(telaEntrega);
                        }else {
                            Intent telaEntrega = new Intent(PontosFuncionarios.this, PontosRegistrados.class);
                            telaEntrega.putExtra("user_id", chave_acao);
                            telaEntrega.putExtra("nome", model.getNome());
                            startActivity(telaEntrega);
                            //finish();
                        }
                    }
                });

            }
        };
        listaFuncionarios.setAdapter(firebaseRecyclerAdapter);

    }
    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AnuncioViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setNome(String nome){

            TextView nomeUser = (TextView)mView.findViewById(R.id.nomeFuncionario);
            nomeUser.setText(nome);

        }





    }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(PontosFuncionarios.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(PontosFuncionarios.this, MainActivity.class));
        finish();
        return true;
    }
}
