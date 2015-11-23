package com.thenealboys.kenny.whatsreckless;

import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.thenealboys.kenny.whatsreckless.InformationAsyncTask;
import com.thenealboys.kenny.whatsreckless.location.LocationLookup;
import com.thenealboys.kenny.whatsreckless.location.StateChangeListener;

import java.util.Arrays;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    /**
     * A tag used for logging purposes
     */
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private  Spinner spinner;
    private String[] states;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        Resources res = getResources();
        states = res.getStringArray(R.array.states);

        // Spinner element
        spinner = (Spinner) view.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderStateInfo(states[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                final WebView webview = (WebView) getView().findViewById(R.id.webView);
                webview.loadData("<html></html>", "text/html","UTF-8");
            }
        });

        // Spinner Drop down elements
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, states);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        return view;
    }

    public void select(String state){
        int index = Arrays.asList(states).indexOf(state);
        if (index != -1){
            spinner.setSelection(index);
        }
    }

    public void renderStateInfo(String state){

        final WebView webview = (WebView) getView().findViewById(R.id.webView);
        try {
            // Get the reckless driving information from Wikipedia
            AsyncTask asyncTask = new InformationAsyncTask( getActivity(), state ){
                @Override
                protected void onPostExecute(Map<String, String> result) {

                    if (result == null){
                        webview.loadData("<html>Failed</html>", MimeTypeMap.getSingleton().getMimeTypeFromExtension(".html"), "UTF-8");
                        return;
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append("<html><table>");

                    // Iteratively add each row to the table header to the row
                    String[] columnNames = result.get( InformationAsyncTask.COLUMN_NAMES ).split( "," );
                    for ( String columnName : columnNames ) {
                        sb
                                .append("<tr><td>")
                                .append(columnName)
                                .append("</td><td>")
                                .append(result.get(columnName) == null ? "" : result.get(columnName))
                                .append("</td></tr>");
                    }
                    sb.append("</table></html>");
                    webview.loadData(sb.toString(), MimeTypeMap.getSingleton().getMimeTypeFromExtension(".html"), "UTF-8");
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see android.os.AsyncTask#onPreExecute()
                 */
                @Override
                protected void onPreExecute() {

                    webview.loadData("<html>Loading state info...</html>", "text/html","UTF-8");
                }
            }.execute();
        } catch ( Exception e ) {
            Log.e( LOG_TAG, e.getMessage() );
        }
    }
}
