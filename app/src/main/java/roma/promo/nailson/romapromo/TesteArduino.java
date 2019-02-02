package roma.promo.nailson.romapromo;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.List;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class TesteArduino extends AppCompatActivity {

    private DatabaseReference ledVerde, ledAmarelo, mDatabase;
    private Button verde, amarelo, testePDF;
    PDFView pdfView;
    Pessoa p;
    String arquivoPdf = "relatorio.pdf";
    File file;
    private List nomes, datas,horas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_arduino);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("RegistroPonto");
        ledVerde = FirebaseDatabase.getInstance().getReference().child("led");
        ledAmarelo = FirebaseDatabase.getInstance().getReference().child("ledY");

        verde = (Button)findViewById(R.id.ledVerde);
        amarelo = (Button)findViewById(R.id.ledAmarelo);
        testePDF = (Button)findViewById(R.id.testePDF);
        pdfView = (PDFView)findViewById(R.id.pdfView);

        nomes = new List();
        datas = new List();
        horas = new List();



        verde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confVerde();
            }
        });
        testePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarPDF();
            }
        });


        amarelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confAmarelo();
            }
        });
    }

    private void gerarPDF() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        Document doc = new Document(PageSize.A4);
                        // String arquivoPdf = "relatorio.pdf";
                        file = new File(Environment.getExternalStorageDirectory()+"/pdf/"+arquivoPdf);

                        PdfWriter.getInstance(doc, new FileOutputStream(file));
                        doc.open();

                        Paragraph p = new Paragraph("Relatório PDF");
                        p.setAlignment(1);
                        doc.add(p);

                        p = new Paragraph(" ");
                        doc.add(p);

                        PdfPTable table = new PdfPTable(3);

                        PdfPCell cel1 = new PdfPCell(new Paragraph("Nome Loja"));
                        PdfPCell cel2 = new PdfPCell(new Paragraph("Data ponto"));
                        PdfPCell cel3 = new PdfPCell(new Paragraph("Hora ponto"));

                        table.addCell(cel1);
                        table.addCell(cel2);
                        table.addCell(cel3);

                        for (DataSnapshot s : dataSnapshot.getChildren()) {

                            String os = s.getKey();
                            String nomeLoja = s.child("nomeLoja").getValue().toString();
                            String dataP = s.child("data").getValue().toString();
                            String horaP = s.child("hora").getValue().toString();
                            for(int i=0; i <os.length(); i++ ) {

                                cel1 = new PdfPCell(new Paragraph(nomeLoja));
                                cel2 = new PdfPCell(new Paragraph(dataP));
                                cel3 = new PdfPCell(new Paragraph(horaP));

                                table.addCell(cel1);
                                table.addCell(cel2);
                                table.addCell(cel3);

                            }
                                doc.add(table);
                                doc.add(Chunk.TABBING);
                                doc.close();

                    }

                    }catch (Exception e){

                    }

                    pdfView.fromFile(file)
                            .load();

                } else {
                    Log.i("MeuLOG", "erro na captura");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void confAmarelo() {

        if (amarelo.getText().toString().equals("Ligado")){

            ledAmarelo.child("status").setValue("Desligado").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        amarelo.setText("Desligado");
                        amarelo.setBackgroundResource(R.color.red);
                    }
                }
            });
        }else {

            ledAmarelo.child("status").setValue("ligado").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        amarelo.setText("Ligado");
                        amarelo.setBackgroundResource(R.color.amarelo);
                    }
                }
            });
        }

    }

    private void confVerde() {
        if (verde.getText().toString().equals("Ligado")){

            ledVerde.child("status").setValue("Desligado").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        verde.setText("Desligado");
                        verde.setBackgroundResource(R.color.red);
                    }
                }
            });
        }else {
            ledVerde.child("status").setValue("ligado").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        verde.setText("Ligado");
                        verde.setBackgroundResource(R.color.verde);
                    }
                }
            });
        }

    }
}
