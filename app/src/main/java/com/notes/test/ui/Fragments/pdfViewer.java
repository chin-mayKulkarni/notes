package com.notes.test.ui.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.notes.test.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pdfViewer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pdfViewer extends Fragment {
    String url;
    PDFView pdfView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public pdfViewer() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static pdfViewer newInstance(String param1, String param2) {
        pdfViewer fragment = new pdfViewer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);

        url = this.getArguments().getString("url");

        pdfView = root.findViewById(R.id.pdfView);

        pdfView.fromUri(Uri.parse(url));

        return root;
    }
}