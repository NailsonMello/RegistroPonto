package roma.promo.nailson.romapromo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class InicioLogin extends AppCompatActivity {
    
    private Spinner spnUser;
    private Button seguir;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_login);
        ActionBar act = getSupportActionBar();
        act.setTitle("Escolha o usuario");


        spnUser = (Spinner)findViewById(R.id.spnUser);
        seguir = (Button)findViewById(R.id.seguir);
        
        seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguirLogin();
            }
        });
        String[] list = getResources().getStringArray(R.array.usuarios);
        ArrayAdapter<String> adapterestado = new ArrayAdapter<String>(this, R.layout.spinnerlayout, R.id.txt, list);
        spnUser.setAdapter(adapterestado);


    }

    private void seguirLogin() {

        if (spnUser.getSelectedItem().toString().equals("Nailson")) {
            email = "noslianmmello@gmail.com";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Claudio")) {
            email = "claudio@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Claudia")) {
            email = "vendasba.rota11@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Gilcelia")) {
            email = "gilcelia@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Valdeir")) {
            email = "valdeir@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Rosimeire")) {
            email = "rosimeire@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Maria")) {
            email = "maria@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Mauro")) {
            email = "mauro@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Cassiara")) {
            email = "cassiara@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Magali")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Sheila")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Rosangela")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Patricia")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Joise")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Cristiane")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Luziana")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }else if (spnUser.getSelectedItem().toString().equals("Diana")) {
            email = "@romadistribuidora.com.br";
            userEmail();

        }
    }

    private void userEmail() {
        Intent enviarEmail = new Intent(InicioLogin.this, LoginUsuario.class);
        enviarEmail.putExtra("email", email);
        startActivity(enviarEmail);
        finish();
    }

    @Override
    public void onBackPressed() {
           finishAffinity();
    }
}
