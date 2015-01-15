package ch.hesso.valueproposition.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.hesso.valueproposition.R;
import ch.hesso.valueproposition.db.DbObjects;
import ch.hesso.valueproposition.utils.Constants;

public class ElementsFragment extends Fragment {
    private Uri mCanvasUri;

    public static ElementsFragment newInstance() {
        return new ElementsFragment();
    }

    public ElementsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_elements, container, false);

        mCanvasUri = getActivity().getIntent().getData();
        if (mCanvasUri != null) {
            Cursor c = getActivity().getContentResolver().query(mCanvasUri, new String[]{DbObjects.Canvas._ID, DbObjects.Canvas.COL_TITLE, DbObjects.Canvas.COL_DESC}, null, null, null);
            if (c.moveToFirst())
                getActivity().setTitle(c.getString(c.getColumnIndex(DbObjects.Canvas.COL_TITLE)));
        }

        rootView.findViewById(R.id.elements_imagebutton_productsservices).setOnClickListener(onItemClickListener);
        rootView.findViewById(R.id.elements_imagebutton_gaincreators).setOnClickListener(onItemClickListener);
        rootView.findViewById(R.id.elements_imagebutton_painrelievers).setOnClickListener(onItemClickListener);
        rootView.findViewById(R.id.elements_imagebutton_customerjobs).setOnClickListener(onItemClickListener);
        rootView.findViewById(R.id.elements_imagebutton_gains).setOnClickListener(onItemClickListener);
        rootView.findViewById(R.id.elements_imagebutton_pains).setOnClickListener(onItemClickListener);

        Cursor cursorCounter = getActivity().getContentResolver().query(DbObjects.Ideas.CONTENT_URI, DbObjects.Ideas.PROJECTION_IDEAS, DbObjects.Ideas.COL_CANVAS + "=?", new String[]{mCanvasUri.getPathSegments().get(DbObjects.Ideas.IDEA_ID_PATH_POSITION)}, null);

        if (cursorCounter.moveToFirst()) {
            int productServices = 0;
            int gainCreators = 0;
            int painRelievers = 0;
            int customerJobs = 0;
            int customerGains = 0;
            int customerPains = 0;
            do {
                switch (Constants.Elements.values()[cursorCounter.getInt(cursorCounter.getColumnIndex(DbObjects.Ideas.COL_ELEMENT))]) {
                    case PRODUCTS_SERVICES:
                        productServices++;
                        break;
                    case GAIN_CREATORS:
                        gainCreators++;
                        break;
                    case PAIN_RELIEVERS:
                        painRelievers++;
                        break;
                    case CUSTOMERS_JOBS:
                        customerJobs++;
                        break;
                    case CUSTOMER_GAINS:
                        customerGains++;
                        break;
                    case CUSTOMER_PAINS:
                        customerPains++;
                        break;
                }
            } while (cursorCounter.moveToNext());
            TextView tvProductServices = (TextView)rootView.findViewById(R.id.products_services_count);
            tvProductServices.setText(productServices + "");
            TextView tvGainCreators = (TextView)rootView.findViewById(R.id.gain_creators_count);
            tvGainCreators.setText(gainCreators + "");
            TextView tvPainRelievers = (TextView)rootView.findViewById(R.id.pain_relievers_count);
            tvPainRelievers.setText(painRelievers + "");
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_elements, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                Intent intent = new Intent(getActivity(), ExportActivity.class);
                intent.setData(mCanvasUri);
                startActivity(intent);
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.elements_delete_confirmation)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().getContentResolver().delete(mCanvasUri, null, null);
                                getActivity().getContentResolver().delete(DbObjects.Ideas.CONTENT_URI, DbObjects.Ideas.COL_CANVAS + "=?", new String[]{mCanvasUri.getPathSegments().get(DbObjects.Ideas.IDEA_ID_PATH_POSITION)});
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        }).create().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    android.view.View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), IdeasListActivity.class);
            intent.setData(mCanvasUri);
            intent.putExtra(Constants.EXTRA_ELEMENT_TYPE_ID, Constants.Elements.values()[Integer.parseInt(v.getTag().toString())]);
            startActivity(intent);
        }
    };
}