package roma.promo.nailson.romapromo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;
import static roma.promo.nailson.romapromo.R.id.nav_sair;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private MenuItem login, sair, cadastro, vendas, ponto;
    private CardView checkin, realizadas, concorrencia, acoes;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase, dref;
    private DatabaseReference mDataUser;
    private FirebaseAuth mAuth;
    private FirebaseUser user, mUser;
    private TextView nomeMenu, emailMenu, con;
    private CircleImageView imgmenu;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(MainActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        user = mAuth.getCurrentUser();
        item();
        preencherMenu();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuario");
        mProgress = new ProgressDialog(this);
        checkin  = (CardView) findViewById(R.id.checkIn);
        realizadas  = (CardView) findViewById(R.id.realizadas);
        concorrencia  = (CardView) findViewById(R.id.concorrencia);
        acoes  = (CardView) findViewById(R.id.acoes);

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser == null){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("ATENÇÃO");
                    alerta.setIcon(android.R.drawable.ic_menu_info_details);
                    alerta.setMessage("Desculpa Você não tem permissão para entrar, contate o administrador");
                    alerta.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alerta.show();

                }else {
                    mProgress.setTitle("Carregando Localização");
                    mProgress.setMessage("Aguarde, sua localização esta sendo carregada.");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();

                    final int MILISEGUNDOS = 4000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseReference userLoja = FirebaseDatabase.getInstance().getReference().child("usuario");
                            userLoja.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String func = dataSnapshot.child("funcao").getValue().toString();

                                    if (func.toString().equals("admin")){
                                        Intent checkin = new Intent(MainActivity.this, CheckIn.class);
                                        checkin.putExtra("nomemotorista", "****************************************************************");
                                        startActivity(checkin);
                                        finish();
                                        mProgress.dismiss();
                                    }else {

                                        mostrarLojas();
                                        mProgress.dismiss();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }, MILISEGUNDOS);
                }
            }
        });

        realizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent realizadas = new Intent(MainActivity.this, Acoes.class);
                startActivity(realizadas);
                finish();
            }
        });

        concorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{"Cadastrar ações", "Ações cadastradas"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Escolha Uma Opção");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Click Event for each item.
                        if(i == 0){

                            Intent concorrencia = new Intent(MainActivity.this, CadAcoesConcorrentes.class);
                            startActivity(concorrencia);
                            finish();

                        }

                        if(i == 1){

                            Intent concorrencia = new Intent(MainActivity.this, Concorrencia.class);
                            startActivity(concorrencia);
                            finish();

                        }

                    }
                });

                builder.show();



            }
        });

        acoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent acoes = new Intent(MainActivity.this, BrowsePictureActivity.class);
                startActivity(acoes);
                finish();
            }
        });
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
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        String dianome = "";
        int dia = c.get(c.DAY_OF_WEEK);
        switch(dia){
            case Calendar.SUNDAY: dianome = "Domingo";break;
            case Calendar.MONDAY: dianome = "Segunda";break;
            case Calendar.TUESDAY: dianome = "Terça";break;
            case Calendar.WEDNESDAY: dianome = "Quarta";break;
            case Calendar.THURSDAY: dianome = "Quinta";break;
            case Calendar.FRIDAY: dianome = "Sexta";break;
            case Calendar.SATURDAY: dianome = "Sábado";break;
        }
       // String entregador = "Quarta";
        //Toast.makeText(this, dianome, Toast.LENGTH_SHORT).show();
        Query query;
        dref = FirebaseDatabase.getInstance().getReference().child("Promotores");
        query = dref.orderByChild("dia").startAt(dianome).endAt(dianome + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String lojas = dataSnapshot.child("loja").getValue(String.class);
                final String nomees = dataSnapshot.child("promotor").getValue(String.class);

                DatabaseReference userName = FirebaseDatabase.getInstance().getReference().child("usuario");
                userName.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String usuario = dataSnapshot.child("nome").getValue(String.class);

                        if (nomees.toString().equals(usuario)){
                            list.add(lojas);
                            adapter.notifyDataSetChanged();
                            li.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                    String noome = String.valueOf(parent.getItemAtPosition(position));
                                    Intent checkin = new Intent(MainActivity.this, CheckIn.class);
                                    checkin.putExtra("nomemotorista", noome);
                                    startActivity(checkin);
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

        alert.setView(row);
        AlertDialog dialog = alert.create();
        dialog.show();


    }

    @Override
    protected void onStart() {
        super.onStart();


            if (mAuth.getCurrentUser() == null){
                startActivity(new Intent(MainActivity.this, InicioLogin.class));
            }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finishAffinity();

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent login = new Intent(MainActivity.this, LoginUsuario.class);
            startActivity(login);
            //finish();
        }  else if (id == R.id.nav_minha_conta) {
            if (mAuth.getCurrentUser() == null) {
                Intent ite = new Intent(MainActivity.this, LoginUsuario.class);
                startActivity(ite);
                // finish();
            }else{
                Intent ite = new Intent(MainActivity.this, PerfilUsuario.class);
                ite.putExtra("idUser", mAuth.getCurrentUser().getUid());
                startActivity(ite);
                //finish();
            }
        } else if (id == R.id.nav_cadastrar_user) {

            Intent ite = new Intent(MainActivity.this, CadastroUsuario.class);
            startActivity(ite);
            //finish();

        } else if (id == R.id.nav_registros_ponto) {

            Intent ite = new Intent(MainActivity.this, PontosFuncionarios.class);
            startActivity(ite);
            //finish();

        }else if (id == nav_sair) {
            mAuth.signOut();
            if (mAuth.getCurrentUser() == null) {
                Intent it = new Intent(MainActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void item(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View menu = navigationView.getHeaderView(0);
        emailMenu = (TextView)menu.findViewById(R.id.emailViewmenu);
        nomeMenu = (TextView)menu.findViewById(R.id.nomeViewmenu);

        imgmenu = (CircleImageView)menu.findViewById(R.id.imageViewmenu);
        Menu menuu = navigationView.getMenu();
        login = menuu.findItem(R.id.nav_login);
        sair = menuu.findItem(R.id.nav_sair);
        cadastro = menuu.findItem(R.id.nav_cadastrar_user);
        ponto = menuu.findItem(R.id.nav_registros_ponto);

    }

    public void preencherMenu(){

        mDataUser = FirebaseDatabase.getInstance().getReference().child("usuario");

        if (mAuth.getCurrentUser() != null) {

            mDataUser.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String codi = dataSnapshot.getKey();
                    String NomeMenu = dataSnapshot.child("nome").getValue().toString();
                    String EmailMenu = dataSnapshot.child("email").getValue().toString();
                    String imgMenu = dataSnapshot.child("imagem").getValue().toString();
                    String func = dataSnapshot.child("funcao").getValue().toString();

                    emailMenu.setText(EmailMenu);
                    nomeMenu.setText(NomeMenu);

                    Picasso.with(MainActivity.this).load(imgMenu).placeholder(R.drawable.default_avatar).into(imgmenu);


                    if (func.toString().equals("admin")){
                        cadastro.setVisible(true);
                        ponto.setVisible(true);
                    }else {
                        cadastro.setVisible(false);
                        ponto.setVisible(false);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            login.setVisible(false);
            sair.setVisible(true);
        }else {
            emailMenu.setText("noslianmmello@gmail.com");
            nomeMenu.setText("Roma Promo");
            imgmenu.setImageResource(R.drawable.iconeroma);
            login.setVisible(true);
            sair.setVisible(false);
        }
    }

}
