package com.notes.test.ui.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.notes.test.R;
import com.notes.test.ui.downloadLinks.DownloadLink;
import com.notes.test.urlConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    LayoutInflater inflater;
    //private MyListData[] listdata;
    List<MyListData> myListMainData ;

    // RecyclerView recyclerView;
    public MyListAdapter(Context ctx, List<MyListData> listdata) {
        this.inflater = LayoutInflater.from(ctx);
        this.myListMainData = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


       // LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.list_item, parent, false);
      //  ViewHolder viewHolder = new ViewHolder(listItem);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       // final MyListData myListData = listdata[position];
        holder.textView.setText(myListMainData.get(position).getDescription());
        holder.textViewHeader.setText(myListMainData.get(position).getHeader());

        Picasso.get().load(getFinalDownloadableLink(myListMainData.get(position).getPreviewImg())).into(holder.image_notes);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO :Get download code from http://www.codeplayon.com/2019/02/how-to-download-pdf-from-url-in-android-code-example/
                String url1 = "http://www.pdf995.com/samples/pdf.pdf";
                String url = "https://drive.google.com/file/d/1G29nJ5Bvf0UPVJ18tpPRJ7FGFIltLZLx/view";

                String title = "pdfName";

               // DownloadLink.downloadfile(url1,title,view.getContext());
                DownloadLink.downloadfile(getFinalDownloadableLink(myListMainData.get(position).getDownloadableLink()),title,view.getContext());
                Toast.makeText(view.getContext(),"click on item: "+myListMainData.get(position).getHeader(),Toast.LENGTH_LONG).show();
            }

            /*private void downloadfile(String url, String title, Context context) {
                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
                String tempTitle=title.replace("","");
                request.setTitle(tempTitle);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,tempTitle+".pdf");
                DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                request.setMimeType("application/pdf");
                request.allowScanningByMediaScanner();
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                downloadManager.enqueue(request);
            }*/
        });
    }

    private String getFinalDownloadableLink(String url){
        String downloadableUrl = urlConstants.URL_BASE + url ;
        return downloadableUrl;
    }

    @Override
    public int getItemCount() {
        return myListMainData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewHeader;
        public TextView textView;
        public ImageView image_notes;
        public CardView relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewHeader = (TextView) itemView.findViewById(R.id.textViewHeader);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            this.image_notes = itemView.findViewById(R.id.image_notes);
            relativeLayout = (CardView) itemView.findViewById(R.id.relativeLayout);
        }
    }
}