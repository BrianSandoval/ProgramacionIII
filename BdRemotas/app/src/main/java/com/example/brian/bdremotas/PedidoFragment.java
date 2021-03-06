package com.example.brian.bdremotas;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brian.bdremotas.R;
import com.example.brian.bdremotas.Util.Funciones;
import com.example.brian.bdremotas.logica.Producto;
import com.example.brian.bdremotas.logica.Sesion;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PedidoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    SwipeRefreshLayout swipeContenedor;
    ListView lvListado;
    ArrayList<Producto> listaDatos;
    PedidoAdapter adaptadorPedido;
    ProgressDialog pDialog;

    public PedidoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pedido, container, false);

        /*Configura los controles*/
        swipeContenedor = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeContenedor.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);

        swipeContenedor.setOnRefreshListener(this);
        lvListado = (ListView) rootView.findViewById(R.id.lvListado);
        /*Configura los controles*/


        //Inicializar con datos en blanco
        listaDatos = new ArrayList<Producto>();
        adaptadorPedido = new PedidoAdapter(getContext(), listaDatos);
        lvListado.setAdapter(adaptadorPedido);
        //Inicializar con datos en blanco

        //Iniciando la descarga de datos y mostrando el resultado

        pDialog = new ProgressDialog(getContext());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);
        pDialog.show();

        new CargarListaProducto().execute();
        //Iniciando la descarga de datos y mostrando el resultado

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Configurar la librería para guaradar en el cache*/
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this.getContext());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());
        /*Configurar la librería para guaradar en el cache*/


        /*Si la versión del SO es mayor a la versión de GINGERBARD, entonces habilita una politica especial para conectarse a internet*/
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        /*Si la versión del SO es mayor a la versión de GINGERBARD, entonces habilita una politica especial para conectarse a internet*/
    }

    @Override
    public void onRefresh() {
        if (! new Funciones().isOnline(this.getContext())){

            Snackbar snackbar = Snackbar
                    .make(swipeContenedor, "Sin conexión", Snackbar.LENGTH_LONG)
                    .setAction("VOLVER A INTENTAR", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onRefresh();
                        }
                    });

            snackbar.setActionTextColor(Color.RED);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();


            swipeContenedor.setRefreshing(false);
            return;
        }


        System.out.println("Refrescando");
        new CargarListaProducto().execute();
        swipeContenedor.setRefreshing(true);
    }

    public class CargarListaProducto extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            Integer retorno = 0;
            try {
                String urlListado = Funciones.URL_WS + "Producto.listar.php";

                HashMap parametros = new HashMap<String, String>();
                parametros.put("token", Sesion.token);

                String resultado = new Funciones().getHttpContent(urlListado, parametros);
                cargarListaDatos(resultado);
                retorno = 1; // Satisfactoriamente

            } catch (Exception e) {
                e.printStackTrace();
            }

            return retorno;
        }

        @Override
        protected void onPostExecute(Integer retorno) {
            if (retorno == 1) {
                adaptadorPedido.setListaDatos(listaDatos);
                swipeContenedor.setRefreshing(false);
                pDialog.dismiss();

            } else {
                Toast.makeText(PedidoFragment.this.getContext(), "Ha ocurrido un error al cargar los datos de la web service", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarListaDatos(String resultado) {
        try {

            JSONObject json = new JSONObject(resultado);
            JSONArray jsonArray = json.getJSONArray("datos");

            listaDatos.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);

                Producto item = new Producto();
                // Depende de como lo estes jalando en el Postman - Producto.listar
                item.setFoto(jsonData.getString("Foto"));
                item.setNombre(jsonData.getString("nombre"));
                item.setCodigoProducto(jsonData.getInt("codigo_producto"));
                item.setPrecioVenta(jsonData.getDouble("precio_venta"));
                listaDatos.add(item);
            }
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

}
