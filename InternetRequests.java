
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.laivon.street.application.MyApplication;

import java.util.HashMap;
import java.util.Map;

public class InternetRequests {

    /*
    *
    * Essa classe serve para facilitar requisições a intenet usando a lib Volley.
    *
    * Para usá-la, add isso no build.gradle
    *
    * compile 'com.mcxiaoke.volley:library:1.0.17'
    *
    *
    * */

    Map<String, String> params;
    String tag;

    public InternetRequests() {
        params = new HashMap<>();
        tag = "InternetRequests";
    }

    // verifica se o aparelho possui conexão com a internet no momento.
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // cria um Listener vazio para erro, caso a chamada da função não passe um (obrigatório)
    public static Response.ErrorListener emptyErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    // cria um Listener vazio para respostas com sucesso, caso a chamada da função não passe um (obrigatório)
    public static Response.Listener emptyResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

        }
    };

    public void setTag(String tag) {
        this.tag = tag;
    }

    // parâmetros para requisição
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    // mesmo da função acima, mas invez de mandar um HashMap pode se mandar Strings e ele prepara o map internamente.
    // obviamente precisa de um numero par de parametros (mas n tratei isso ainda)
    // Exemplo:
    // makeParams("nome", "Jonathan", "idade", "22")
    public Map<String, String> makeParams(String... vars) {
        params = new HashMap<>();

        for (int i = 0; i < vars.length; i += 2) {
            params.put(vars[i], vars[i + 1]);
        }

        return params;
    }

    // adiciona paramestro a lista já criada, mesma lógica do item acima.
    public void addParam(String... vars) {
        for (int i = 0; i < vars.length; i += 2) {
            params.put(vars[i], vars[i + 1]);
        }
    }

    // modos de executar um post.
    public void executePost(String url) {
        executePost(url, null, null);
    }

    public void executePost(String url, Response.Listener responseListener) {
        executePost(url, responseListener, null);
    }

    public void executePost(String url, Response.ErrorListener errorListener) {
        executePost(url, null, errorListener);
    }

    public void executePost(String url, Response.Listener responseListener, Response.ErrorListener errorListener) {
        executePost(url, responseListener, errorListener, params);
    }


    // mais completo, onde pode mandar tudo.
    public void executePost(String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params) {
        try {

            if (responseListener == null) {
                responseListener = emptyResponseListener;
            }

            if (errorListener == null) {
                errorListener = emptyErrorListener;
            }

            RequestQueue rq = MyApplication.getInstance().getRequestQueue();

            StringRequest request = new StringRequest(Request.Method.POST,
                    url,
                    responseListener,
                    errorListener
            ) {
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }

                @Override
                public Priority getPriority() {
                    return (Priority.HIGH);
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.setTag(tag);

            rq.add(request);
        } catch (Exception e) {
            Log.e("executePost", "Erro ao executar URL: " + url);

            e.printStackTrace();
        }
    }
}
