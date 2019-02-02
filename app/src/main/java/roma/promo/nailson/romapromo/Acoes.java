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

public class Acoes extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView listaAcoes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acoes);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Escolha uma loja");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(Acoes.this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("AcoesCadastradas");
        listaAcoes = (RecyclerView) findViewById(R.id.listaAcoes);
        listaAcoes.setLayoutManager(new LinearLayoutManager(this));
        listaAcoes.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Pessoa, Acoes.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, AnuncioViewHolder>(
                Pessoa.class,
                R.layout.itens_acoes,
                Acoes.AnuncioViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(AnuncioViewHolder viewHolder, Pessoa model, int position) {

                final String chave_acao = getRef(position).getKey();
                viewHolder.setLoja(model.getLoja());
                viewHolder.setData(model.getData());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (FirebaseAuth.getInstance().getCurrentUser() == null){

                            Intent telaEntrega = new Intent(Acoes.this, LoginUsuario.class);
                            startActivity(telaEntrega);
                        }else {
                            Intent telaEntrega = new Intent(Acoes.this, Realizadas.class);
                            telaEntrega.putExtra("acao_id", chave_acao);
                            startActivity(telaEntrega);
                            //finish();
                        }
                    }
                });

            }
        };
   listaAcoes.setAdapter(firebaseRecyclerAdapter);

    }
    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AnuncioViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setLoja(String nome){

            TextView nomeUser = (TextView)mView.findViewById(R.id.lojasAcoes);
            nomeUser.setText(nome);

        }

        public void setData(String data){
            TextView nomeUser = (TextView)mView.findViewById(R.id.dataAcao);
            nomeUser.setText(data);
        }



    }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(Acoes.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(Acoes.this, MainActivity.class));
        finish();
        return true;
    }
}
