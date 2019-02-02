package roma.promo.nailson.romapromo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;;
import java.util.List;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class CheckIn extends AppCompatActivity {
    private Button registrar;
    private TextView horaatual, localizacao, editarLoc, nomePromotor, nomeLoja, ultimoRegistro;
    private ImageView sincronizar;
    private EditText edtEditarLocal;
    private View view;
    private FirebaseAuth mAuth;
    Geocoder geocoder;
    List<Address> addresses;
    GoogleApiClient mapGoogleApiClient;
    LinearLayout linearLayoutLocal, linearLayoutLocaleditado;
    private DatabaseReference Usuario, RegistrarPonto, UserRegistrarPonto, UltimoRegistro;
    private ProgressDialog dialog;
    private CircleImageView imgperfil;

    private String mLoja;
    ObtainGPS  gps;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Fazer check in");

        mAuth = FirebaseAuth.getInstance();

        Usuario = FirebaseDatabase.getInstance().getReference().child("usuario");
        RegistrarPonto = FirebaseDatabase.getInstance().getReference().child("RegistroPonto");
        mLoja = getIntent().getExtras().getString("nomemotorista");
        edtEditarLocal = (EditText)findViewById(R.id.edtEditarLocal);
        linearLayoutLocal = (LinearLayout)findViewById(R.id.lay3);
        linearLayoutLocaleditado = (LinearLayout)findViewById(R.id.layouteditar);
        editarLoc = (TextView)findViewById(R.id.editarLoc);
        sincronizar = (ImageView) findViewById(R.id.sincLocal);
        horaatual = (TextView) findViewById(R.id.horaAtual);
        nomeLoja = (TextView) findViewById(R.id.nomeLoja);
        localizacao = (TextView) findViewById(R.id.localizacao);
        nomePromotor = (TextView) findViewById(R.id.nomePromotor);
        ultimoRegistro = (TextView) findViewById(R.id.ultimoRegistro);
        imgperfil = (CircleImageView) findViewById(R.id.imgPerfil);
        registrar = (Button) findViewById(R.id.registrar);
        view = (View)findViewById(R.id.view3);
        String iduserResgistro = mAuth.getCurrentUser().getUid();
        UserRegistrarPonto = FirebaseDatabase.getInstance().getReference().child(iduserResgistro);
        UltimoRegistro = FirebaseDatabase.getInstance().getReference().child("UltimoRegistro");
        ultimoRegistro.setText("Você ainda não resgistrou ponto");
        dialog = new ProgressDialog(this);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        final String dataFormatada = sdf.format(hora);
        horaatual.setText(dataFormatada);
        nomeLoja.setText(mLoja);
        sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalization();
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registarPonto();
            }

        });
        editarLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutLocal.setVisibility(View.GONE);
                linearLayoutLocaleditado.setVisibility(View.VISIBLE);
                editarLoc.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
        });

        Usuario.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String promo = dataSnapshot.child("nome").getValue().toString();
                String imagemPerfil = dataSnapshot.child("imagem").getValue().toString();
                nomePromotor.setText(promo);
                Picasso.with(CheckIn.this).load(imagemPerfil).placeholder(R.drawable.default_avatar).into(imgperfil);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        UltimoRegistro.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ultimo = dataSnapshot.child("ultimoRegistro").getValue().toString();

                    ultimoRegistro.setText(ultimo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void registarPonto() {
        final String editLocal;
        dialog.setMessage("Enviando registro...");
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String dateString = sdf.format(date);

        final String nomeLojas = nomeLoja.getText().toString();
       final String nomeFuncionario = nomePromotor.getText().toString();
       final String hora = horaatual.getText().toString();
       if (edtEditarLocal.getText().toString().equals("")){
        editLocal = "local não editado";
       }else {
           editLocal = edtEditarLocal.getText().toString();
       }
       final String localLoja = localizacao.getText().toString();

       if (!TextUtils.isEmpty(nomeLojas) && !TextUtils.isEmpty(nomeFuncionario) && !TextUtils.isEmpty(hora)) {
           dialog.show();
           final DatabaseReference novoRegistro = RegistrarPonto.push();
           UserRegistrarPonto.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   String key = dataSnapshot.getKey();

                   novoRegistro.child("nomeLoja").setValue(nomeLojas);
                   novoRegistro.child("funcionario").setValue(nomeFuncionario);
                   novoRegistro.child("hora").setValue(hora);
                   novoRegistro.child("data").setValue(dateString);
                   novoRegistro.child("localEditado").setValue(editLocal);
                   novoRegistro.child("localOriginal").setValue(localLoja);
                   novoRegistro.child("idUsuario").setValue(mAuth.getCurrentUser().getUid());
                   UltimoRegistro.child(mAuth.getCurrentUser().getUid()).child("ultimoRegistro").setValue(dateString+" - "+hora).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                       }
                   });

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
           dialog.dismiss();
           startActivity(new Intent(CheckIn.this, MainActivity.class));
           finish();
           Toast.makeText(CheckIn.this, "Registro efetuado com sucesso!", Toast.LENGTH_SHORT).show();
       }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getLocalization() {
        gps = new ObtainGPS(CheckIn.this);

        if (GetLocalization(CheckIn.this)) {
            // check if GPS enabled
            if (gps.canGetLocation()) {
                // aqui você captura lat e lgn caso o localização seja diferente de nul
                geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);

                    String address = addresses.get(0).getAddressLine(0);
                    String area = addresses.get(0).getLocality();
                    String city = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    String fullAddress = address;

                    localizacao.setText(fullAddress);
                    Toast.makeText(CheckIn.this, fullAddress, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //AlertDialog erroLocation = new AlertDialog.Builder(this).create();
               // erroLocation.setTitle("Localização");
               // erroLocation.setMessage("Lat:" + gps.getLatitude() + " Lng:" + gps.getLongitude());
               // erroLocation.show();

            } else {


                showSettingsAlert();
                //gps.showSettingsAlert();

            }

        }
    }
    public boolean GetLocalization(Context context) {
        int REQUEST_PERMISSION_LOCALIZATION = 221;
        boolean res = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                res = false;
                ActivityCompat.requestPermissions((Activity) context, new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCALIZATION);

            }
        }
        return res;
    }

    @Override
    protected void onStart() {
        getLocalization();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(CheckIn.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(CheckIn.this, MainActivity.class));
        finish();
        return true;
    }

    public void showSettingsAlert(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CheckIn.this);

        // Titulo do dialogo
        alertDialog.setTitle("GPS");

        // Mensagem do dialogo
        alertDialog.setMessage("GPS não está habilitado. Deseja configurar?");

        // botao ajustar configuracao
        alertDialog.setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        // botao cancelar
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }

}