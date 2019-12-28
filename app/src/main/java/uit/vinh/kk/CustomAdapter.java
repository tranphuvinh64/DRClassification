package uit.vinh.kk;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView personalID;
        TextView Name;
        TextView dateOfBirth;
        TextView textViewResult;
        ImageView imageViewResult;

    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.base_row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.base_row_item, parent, false);
            viewHolder.Name = convertView.findViewById(R.id.row_item_name);
            viewHolder.dateOfBirth = convertView.findViewById(R.id.row_item_DoB);
            viewHolder.personalID = convertView.findViewById(R.id.row_item_personalID);
            viewHolder.imageViewResult = convertView.findViewById((R.id.imageview_icon_result));
            viewHolder.textViewResult = convertView.findViewById(R.id.icon_text_result);
//            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
//            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
//            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
//            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.Name.setText(dataModel.getName());
        viewHolder.personalID.setText(dataModel.getPersonalID());
        viewHolder.dateOfBirth.setText(dataModel.getDateOfBirth());
        if (dataModel.getResult().equals("No DR")){
            viewHolder.imageViewResult.setImageResource(R.drawable.bg_circle_nodr);
            viewHolder.textViewResult.setText("N");
        }
        else if (dataModel.getResult().equals("Mild")){
            viewHolder.imageViewResult.setImageResource(R.drawable.bg_circle_mild);
            viewHolder.textViewResult.setText("M");
        }
        else if (dataModel.getResult().equals("Moderate")){
            viewHolder.imageViewResult.setImageResource(R.drawable.bg_circle_moderate);
            viewHolder.textViewResult.setText("A");
        }
        else if (dataModel.getResult().equals("Severe")){
            viewHolder.imageViewResult.setImageResource(R.drawable.bg_circle_severe);
            viewHolder.textViewResult.setText("S");
        }
        else if (dataModel.getResult().equals("Proliferative")){
            viewHolder.imageViewResult.setImageResource(R.drawable.bg_circle_proliferative);
            viewHolder.textViewResult.setText("P");
        }
//        viewHolder.txtName.setText(dataModel.getName());
//        viewHolder.txtType.setText(dataModel.getType());
//        viewHolder.txtVersion.setText(dataModel.getVersion_number());
//        viewHolder.info.setOnClickListener(this);
//        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
