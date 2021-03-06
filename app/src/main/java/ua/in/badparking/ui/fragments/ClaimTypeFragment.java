package ua.in.badparking.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ua.in.badparking.R;
import ua.in.badparking.services.ClaimService;
import ua.in.badparking.ui.MainActivity;

/**
 * Design https://www.dropbox.com/sh/vbffs09uqzaj2mt/AAABkTvQbP7q10o5YP83Mzdia?dl=0
 * Created by Dima Kovalenko on 7/3/16.
 */
public class ClaimTypeFragment extends BaseFragment {

    private String[] reportTypes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_claim_type, container, false);

        reportTypes = getResources().getStringArray(R.array.report_types);

        ListView listView = (ListView)rootView.findViewById(R.id.reportTypeList);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return reportTypes.length;
            }

            @Override
            public Object getItem(int i) {
                return reportTypes[i];
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View itemView = inflater.inflate(R.layout.listitem_report_type, parent, false);
                TextView textView = (TextView)itemView.findViewById(R.id.list_item);
                textView.setText((String)getItem(i));
                return itemView;
            }
        });

        rootView.findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).moveToNext();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ClaimService.INST.getClaim().setCaseTypeId(String.valueOf(reportTypes[position]));
            }
        });
        return rootView;
    }

    public static Fragment newInstance() {
        return new ClaimTypeFragment();
    }
}
