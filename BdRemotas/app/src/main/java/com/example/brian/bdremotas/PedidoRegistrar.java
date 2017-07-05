package com.example.brian.bdremotas;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brian.bdremotas.Util.Funciones;
import com.example.brian.bdremotas.logica.Producto;
import com.example.brian.bdremotas.logica.Sesion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class PedidoRegistrar extends AppCompatActivity implements View.OnClickListener{
    EditText txtDni;
    TextView txtNroPed;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_registrar);

        txtDni = (EditText) findViewById(R.id.txtDni);
        txtNroPed = (TextView) findViewById(R.id.txtNroPed);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        boolean r = Funciones.mensajeConfirmacion(this, "Confirme", "Desea registrar el pedido");
        if (r){
            String dni = txtDni.getText().toString();

            /*Obtener el json para enviar el detalle del pedido*/
            JSONArray jsonArrayDetalle = new JSONArray();
            for (int i = 0; i< PedidoAdapter.listaDatos.size();i++){
                Producto item = PedidoAdapter.listaDatos.get(i);
                if (item.getCanditad() > 0) {
                    jsonArrayDetalle.put(item.getJSONItemDetalle());
                }
            }
            String detallePedido = jsonArrayDetalle.toString();
            Log.e("Detalle Pedido", detallePedido);

            new RegistrarPedidoTask(dni, detallePedido).execute();
        }
    }

    public class RegistrarPedidoTask extends AsyncTask<Void, Void, String> {
        private String dni;
        private String detalle;

        public RegistrarPedidoTask(String dni, String detalle) {
            this.dni = dni;
            this.detalle = detalle;
        }

        @Override
        protected String doInBackground(Void... params) {
            // LLamar a la WS para registrar el pedido
            try {
                String ws = Funciones.URL_WS + "pedido.registrar.php";
                HashMap parametros = new HashMap<String, String>();
                // Igual como esta en el postman
                parametros.put("token", Sesion.token);
                parametros.put("dni_cli", this.dni);
                parametros.put("det_ped", this.detalle);

                String resultado = new Funciones().getHttpContent(ws, parametros);
                return resultado;

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "";
            }
        }


        protected void onPostExecute(final String resultado) {
            if ( ! resultado.isEmpty() ) {
                try {
                    JSONObject json = new JSONObject(resultado);

                    int estado = json.getInt("estado");
                    if (estado==200) {
                        JSONObject jsonDatos = json.getJSONObject("datos");
                        txtNroPed.setText(String.valueOf(jsonDatos.getInt("np")));
                        Funciones.mensajeInformacion(PedidoRegistrar.this, "Pedido", "Registrado correctamente");
                        PedidoRegistrar.this.finish();
                    }else{
                        Log.e("Error", json.getString("mensaje"));
                    }
                }catch (Exception e){
                    Toast.makeText(PedidoRegistrar.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
