package android.cloud.microsoft.com.serviceapplication;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by mohit on 14/7/17.
 */


// Adapter is a bridge between an AdapterView`i.e. Linear/Grid view typs. and the data to which it has to be filled.
public class CustomAdapter extends RecyclerView.Adapter<android.cloud.microsoft.com.serviceapplication.CustomAdapter.MyViewHolder> {

    private ArrayList<FilePOJO> list_members = new ArrayList<>();
    private final LayoutInflater inflater;
    View view;
    MyViewHolder holder;
    MainActivity activity;
    Stack<String> folderHistory;
    FolderHistory fh;
    Context context;


    public CustomAdapter(Context context, MainActivity activ) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = activ;
        fh = new FolderHistory();
        folderHistory = fh.getHistory();
    }

    @Override
    public android.cloud.microsoft.com.serviceapplication.CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = inflater.inflate(R.layout.custom_row, parent, false);
        holder = new MyViewHolder(view);
        return holder;
    }

    //display the data at the specified position. Here single FilePOJO object is returned and set in the layout.
    //This way it's done for all the layout.
    @Override
    public void onBindViewHolder(android.cloud.microsoft.com.serviceapplication.CustomAdapter.MyViewHolder holder, int position) {
        FilePOJO list_items = list_members.get(position);
        holder.user_name.setText(list_items.getFileName());
        holder.content.setText(list_items.getDetail());
        holder.image.setImageResource(getImageId(list_items.getFileImage()));

    }

    @Override
    public int getItemCount() {
        return list_members.size();
    }


    public int getImageId(String s)
    {

        return context.getResources().getIdentifier("drawable/" + s, null, context.getPackageName());
    }



    // this method recieves all the FilePOJO object for each layout within recycler view.
    public void setListContent(ArrayList<FilePOJO> list_members) {
        this.list_members = list_members;
        notifyItemRangeChanged(0, list_members.size());

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user_name, content, time;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
            image = (ImageView) itemView.findViewById(R.id.picture);
        }

        //what has to be done when file is clicked is performed here.
        //File is clicked - start a new activity which will open the application in that file.
        //Folder is clicked - Add that folder in the folderHistory stack and then reload the recycler view with that folder
        // as the root.
        // TO open the new activity it has to be run in the main activity, using startActivity(), for that a method is called
        // in MainAcvitiy using it's instance.
        @Override
        public void onClick(View v) {


            TextView a = (TextView) v.findViewById(R.id.user_name);
            String clicked = a.getText().toString();
            Log.i("CustomError2", clicked);

            if (folderHistory.isEmpty())
                folderHistory = fh.getHistory();
            String history = folderHistory.peek();
            File clickedFile = new File(history, clicked);
            Log.i("CustomError2", clickedFile.toString());

            if (clickedFile.isDirectory()) {
                folderHistory.push(clickedFile.toString());
                activity.populateRecyclerViewValues(clickedFile.toString());
                Log.i("CustomError2", "|Directory");
            } else {
                String ext = getExtension(clickedFile.toString());
                activity.playFile(clickedFile.toString(),ext);
            }
        }

        public String getExtension(String fileName)
        {
            int l = fileName.length();

            return fileName.substring(l-3,l);
        }



    }

    //When hardware back button is clicked,
    // Recycler view shows the content of previous folder and removes the current folder from folderHistory stack.
    // If no element in the folderHistory stack then close the app.
    public boolean goBack() {

        if (folderHistory.isEmpty())
            return true;
        folderHistory.pop();
        if (!folderHistory.isEmpty())
            activity.populateRecyclerViewValues(folderHistory.peek());
        return false;
    }

}


