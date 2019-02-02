package roma.promo.nailson.romapromo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RealizadasConcorrente extends AppCompatActivity {

    private RecyclerView listaimagens;
    private DatabaseReference mDatabase, mDatabaseimg;
    private TextView periodo, preco, descricao, loja;
    private String chaveAcao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizadas_concorrente);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Ação cadastrada Concorrente");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(RealizadasConcorrente.this);

        mDatabaseimg = FirebaseDatabase.getInstance().getReference().child("AcoesCadastradasCorrentes");
        chaveAcao = getIntent().getExtras().getString("acao_id");
        periodo = (TextView)findViewById(R.id.periodo);
        descricao = (TextView)findViewById(R.id.descricao);
        preco = (TextView)findViewById(R.id.preco);
        loja = (TextView)findViewById(R.id.loja);
        listaimagens = (RecyclerView) findViewById(R.id.listaImgs);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        listaimagens.setLayoutManager(layoutManager);
        listaimagens.setItemAnimator(new DefaultItemAnimator());

        mDatabaseimg.child(chaveAcao).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String periodoa = dataSnapshot.child("periodo").getValue().toString();
                String precoa = dataSnapshot.child("preco").getValue().toString();
                String descricaoa = dataSnapshot.child("descricao").getValue().toString();
                String lojaa = dataSnapshot.child("loja").getValue().toString();
                String chaves = dataSnapshot.child("chave").getValue().toString();

                periodo.setText(periodoa);
                preco.setText(precoa);
                descricao.setText(descricaoa);
                loja.setText(lojaa);
                carregar(chaves);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void carregar(String chaves) {
        Query query;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("FotosAcoesCorrentes");

        query = mDatabase.orderByChild("chave").startAt(chaves).endAt(chaves+ "\uf8ff");

        FirebaseRecyclerAdapter<Pessoa, RealizadasConcorrente.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, RealizadasConcorrente.AnuncioViewHolder>(
                Pessoa.class,
                R.layout.images_itens,
                RealizadasConcorrente.AnuncioViewHolder.class,
                query


        ) {
            @Override
            protected void populateViewHolder(RealizadasConcorrente.AnuncioViewHolder viewHolder, final Pessoa model, int position) {

                final String chave_acao = getRef(position).getKey();

                viewHolder.setImagem(getApplicationContext(),model.getImagem());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent img = new Intent(RealizadasConcorrente.this, ImagemAcao.class);
                        img.putExtra("httpImg", model.getImagem());
                        startActivity(img);
                    }
                });

            }
        };
        listaimagens.setAdapter(firebaseRecyclerAdapter);

    }
    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AnuncioViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setImagem(Context context, String imagem){

            ImageView nomeUser = (ImageView) mView.findViewById(R.id.imgRealizadas);
            Picasso.with(context).load(imagem).placeholder(R.mipmap.icon).into(nomeUser);


        }





    }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(RealizadasConcorrente.this, Concorrencia.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(RealizadasConcorrente.this, Concorrencia.class));
        finish();
        return true;
    }
}

