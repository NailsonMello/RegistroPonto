package roma.promo.nailson.romapromo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PontosRegistrados extends AppCompatActivity {

    private String chaveFuncionario, nomeFunc;
    private DatabaseReference mDatabase, dref;
    private DatabaseReference mFiltro;
    private RecyclerView listaPontos;
    String SearchdataPonto, SearchlojaPonto;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pontos_registrados);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Ponto Funcionario");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(PontosRegistrados.this);

        chaveFuncionario = getIntent().getExtras().getString("user_id");
        nomeFunc = getIntent().getExtras().getString("nome");

        listaPontos = (RecyclerView) findViewById(R.id.listaPontos);
        final LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setReverseLayout(true);
        ln.setStackFromEnd(true);
        listaPontos.setLayoutManager(ln);
        //listaPontos.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = (String.format("%02d/%02d/%d", day,+month,year));

                queryPonto(date);

            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();

        Query query;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("RegistroPonto");

        query = mDatabase.orderByChild("idUsuario").startAt(chaveFuncionario).endAt(chaveFuncionario + "\uf8ff");

        FirebaseRecyclerAdapter<Pessoa, PontosRegistrados.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, PontosRegistrados.AnuncioViewHolder>(
                Pessoa.class,
                R.layout.item_ponto,
                PontosRegistrados.AnuncioViewHolder.class,
                query


        ) {
            @Override
            protected void populateViewHolder(PontosRegistrados.AnuncioViewHolder viewHolder, Pessoa model, int position) {

                final String chave_acao = getRef(position).getKey();
                viewHolder.setNome(model.getFuncionario());
                viewHolder.setRegistro(model.getData(), model.getHora());
                viewHolder.setLoja(model.getNomeLoja());
                viewHolder.setlocalOriginal(model.getLocalOriginal());
                viewHolder.setLocaleditado(model.getLocalEditado());

            }
        };
        listaPontos.setAdapter(firebaseRecyclerAdapter);

    }
    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AnuncioViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setNome(String nome){

            TextView nomeUser = (TextView)mView.findViewById(R.id.nomeFunc);
            nomeUser.setText(nome);

        }

        public void setRegistro(String data, String hora){
            TextView texto = (TextView)mView.findViewById(R.id.marcacaoPonto);
            texto.setText("Ponto registrado em: "+data+ " as "+hora);
        }

        public void setLoja(String nomeLoja){
            TextView nomeUser = (TextView)mView.findViewById(R.id.lojaPonto);
            nomeUser.setText("Loja do registro: "+nomeLoja);

        }

        public void setlocalOriginal(String localOriginal){
            TextView nomeUser = (TextView)mView.findViewById(R.id.localOriginal);
            nomeUser.setText("Local original do registro: "+localOriginal);

        }

        public void setLocaleditado(String localEditado){
            TextView nomeUser = (TextView)mView.findViewById(R.id.localEditado);
            nomeUser.setText("Local editado do registro: "+localEditado);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ponto, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_data) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(PontosRegistrados.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                    year,month,day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();


        }else if (id == R.id.action_loja){
            DatabaseReference mFiltro = FirebaseDatabase.getInstance().getReference().child("usuario");
            mFiltro.child(chaveFuncionario).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String funcao = dataSnapshot.child("funcao").getValue(String.class);
                    if (funcao.equals("admin")){
                        Toast.makeText(PontosRegistrados.this, "Somente para promotores", Toast.LENGTH_SHORT).show();
                    }else{
                        mostrarLojas();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarLojas() {
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter<String> adapter;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Escolha uma loja");
        alert.setIcon(R.drawable.motoristas);
        View row = getLayoutInflater().inflate(R.layout.lista_motoristas, null);
        final ListView li = (ListView)row.findViewById(R.id.testandooo);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        li.setAdapter(adapter);

        alert.setView(row);
        final AlertDialog dialog = alert.create();
        dialog.show();

        Query query;
        dref = FirebaseDatabase.getInstance().getReference().child("Promotores");
        query = dref.orderByChild("promotor").startAt(nomeFunc).endAt(nomeFunc + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String lojas = dataSnapshot.child("loja").getValue(String.class);

                list.add(lojas);
                adapter.notifyDataSetChanged();
                li.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String noome = String.valueOf(parent.getItemAtPosition(position));
                        queryFuncionario(noome);
                        dialog.dismiss();
                    }
                });



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void queryPonto(String date) {

        Query query;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("RegistroPonto");

        query = mDatabase.orderByChild("data").startAt(date).endAt(date + "\uf8ff");

        FirebaseRecyclerAdapter<Pessoa, PontosRegistrados.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, PontosRegistrados.AnuncioViewHolder>(
                Pessoa.class,
                R.layout.item_ponto,
                PontosRegistrados.AnuncioViewHolder.class,
                query


        ) {
            @Override
            protected void populateViewHolder(PontosRegistrados.AnuncioViewHolder viewHolder, Pessoa model, int position) {

                final String chave_acao = getRef(position).getKey();
                viewHolder.setNome(model.getFuncionario());
                viewHolder.setRegistro(model.getData(), model.getHora());
                viewHolder.setLoja(model.getNomeLoja());
                viewHolder.setlocalOriginal(model.getLocalOriginal());
                viewHolder.setLocaleditado(model.getLocalEditado());

            }
        };
        listaPontos.setAdapter(firebaseRecyclerAdapter);

    }


    private void queryFuncionario(String noome) {
        Query query;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("RegistroPonto");

        query = mDatabase.orderByChild("nomeLoja").startAt(noome).endAt(noome + "\uf8ff");

        FirebaseRecyclerAdapter<Pessoa, PontosRegistrados.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, PontosRegistrados.AnuncioViewHolder>(
                Pessoa.class,
                R.layout.item_ponto,
                PontosRegistrados.AnuncioViewHolder.class,
                query


        ) {
            @Override
            protected void populateViewHolder(PontosRegistrados.AnuncioViewHolder viewHolder, Pessoa model, int position) {

                final String chave_acao = getRef(position).getKey();
                viewHolder.setNome(model.getFuncionario());
                viewHolder.setRegistro(model.getData(), model.getHora());
                viewHolder.setLoja(model.getNomeLoja());
                viewHolder.setlocalOriginal(model.getLocalOriginal());
                viewHolder.setLocaleditado(model.getLocalEditado());

            }
        };
        listaPontos.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(PontosRegistrados.this, PontosFuncionarios.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(PontosRegistrados.this, PontosFuncionarios.class));
        finish();
        return true;
    }
}

