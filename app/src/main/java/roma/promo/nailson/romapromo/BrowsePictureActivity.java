package roma.promo.nailson.romapromo;


import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BrowsePictureActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mDatabaseUser, mDatabasepush;
    int SELECT_PICTURES = 1;
    private TextView imgSelecionada;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private Uri imageUri, imageUriOne;
    private EditText precoAcao, descricaoAcao, periodoAcao, lojaAcao;
    int up = 0;
    int k = 0;
    Button selct;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_picture);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Cadastro de ações");


        mAuth = FirebaseAuth.getInstance();
        String iduserAnuncio = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("AcoesCadastradas");
        mDatabasepush = FirebaseDatabase.getInstance().getReference().child("FotosAcoes");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(iduserAnuncio);
        selct = (Button) findViewById(R.id.select);
        img = (ImageView) findViewById(R.id.selected_picture_imageview);
        imgSelecionada = (TextView) findViewById(R.id.txtSelecionar);
        precoAcao = (EditText)findViewById(R.id.precoAcao);
        periodoAcao = (EditText)findViewById(R.id.periodoAcao);
        descricaoAcao = (EditText)findViewById(R.id.descricaoAcao);
        lojaAcao = (EditText)findViewById(R.id.lojaAcao);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES);
            }
        });

        selct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    salvar();

                } else {
                    salvarOne();
                }

            }
        });

    }

    private void salvarOne() {
        final String preco = precoAcao.getText().toString();
        final String periodo = periodoAcao.getText().toString();
        final String descricao = descricaoAcao.getText().toString();
        final String loja = lojaAcao.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        final String dataFormatada = sdf.format(hora);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
        final String dateString = sdfd.format(date);


        if (imageUriOne != null && !TextUtils.isEmpty(preco) && !TextUtils.isEmpty(periodo) && !TextUtils.isEmpty(descricao) && !TextUtils.isEmpty(loja) ) {
            final DatabaseReference novaFoto = mDatabase.push();
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    novaFoto.child("preco").setValue(preco);
                    novaFoto.child("loja").setValue(loja);
                    novaFoto.child("periodo").setValue(periodo);
                    novaFoto.child("descricao").setValue(descricao);
                    novaFoto.child("data").setValue(dateString);
                    novaFoto.child("hora").setValue(dataFormatada);
                    novaFoto.child("chave").setValue(mAuth.getCurrentUser().getUid() + dataFormatada);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        final DatabaseReference novaa = mDatabasepush.push();
        StorageReference filepathOne = FirebaseStorage.getInstance().getReference().child("gpic");
        filepathOne.child(imageUriOne.getLastPathSegment()).putFile(imageUriOne).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();

                novaa.child("imagem").setValue(downloadURL.toString());
                novaa.child("id").setValue(mAuth.getCurrentUser().getUid());
                novaa.child("chave").setValue(mAuth.getCurrentUser().getUid() + dataFormatada);


            }
        });

     }else {
            Toast.makeText(this, "Nenhuma foto", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(BrowsePictureActivity.this, MainActivity.class));
        finish();
 }

    private void salvar() {

        final String preco = precoAcao.getText().toString();
        final String periodo = periodoAcao.getText().toString();
        final String descricao = descricaoAcao.getText().toString();
        final String loja = lojaAcao.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        final String dataFormatada = sdf.format(hora);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
        final String dateString = sdfd.format(date);


        if (imageUri != null && !TextUtils.isEmpty(preco) && !TextUtils.isEmpty(periodo) && !TextUtils.isEmpty(descricao) && !TextUtils.isEmpty(loja)) {
        final DatabaseReference novaFoto = mDatabase.push();
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                novaFoto.child("preco").setValue(preco);
                novaFoto.child("loja").setValue(loja);
                novaFoto.child("periodo").setValue(periodo);
                novaFoto.child("descricao").setValue(descricao);
                novaFoto.child("data").setValue(dateString);
                novaFoto.child("hora").setValue(dataFormatada);
                novaFoto.child("chave").setValue(mAuth.getCurrentUser().getUid() + dataFormatada);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("gpic");
        int i = 0;
        while (up < mArrayUri.size()){
            final DatabaseReference nova = mDatabasepush.push();
            final int finalI = i;
            filepath.child(mArrayUri.get(k).getLastPathSegment()).putFile(mArrayUri.get(k)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadURL = taskSnapshot.getDownloadUrl();

                    nova.child("imagem").setValue(downloadURL.toString());
                    nova.child("id").setValue(mAuth.getCurrentUser().getUid());
                    nova.child("chave").setValue(mAuth.getCurrentUser().getUid()+dataFormatada);

                }
            });
            up++;
            k++;
            i++;

        }

        }else {
            Toast.makeText(this, "Nenhuma foto", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(BrowsePictureActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURES) {
            if (resultCode == MainActivity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    Log.i("count", String.valueOf(count));
                    int currentItem = 0;
                    while (currentItem < count) {
                        imageUri = data.getClipData().getItemAt(currentItem).getUri();


                        Log.i("uri", imageUri.toString());
                        mArrayUri.add(imageUri);
                        currentItem = currentItem + 1;
                        imgSelecionada.setText("Fotos selecionadas");
                        img.setImageURI(imageUri);

                    }

                    Log.i("listsize", String.valueOf(mArrayUri.size()));
                } else if (data.getData() != null) {

                    imageUriOne = data.getData();
                    imgSelecionada.setText("Você selecionou uma imagem");

                }
            }
         }
        }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(BrowsePictureActivity.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(BrowsePictureActivity.this, MainActivity.class));
        finish();
        return true;
    }

}

