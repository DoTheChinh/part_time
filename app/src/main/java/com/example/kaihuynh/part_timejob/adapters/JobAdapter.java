package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.JobLikedFragment;
import com.example.kaihuynh.part_timejob.ListRecruitmentActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.UpdateJobActivity;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Job;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kai on 2018-02-12.
 */

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobItemViewHolder> {

    private ArrayList<Job> mJobList;
    private Context context;
    private int layout;

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickItemIndex);
    }


    public JobAdapter(Context context, int layout, ArrayList<Job> mJobList, ListItemClickListener listener) {
        this.mJobList = mJobList;
        this.context = context;
        this.mOnClickListener = listener;
        this.layout = layout;
    }

    @NonNull
    @Override
    public JobItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new JobItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final JobItemViewHolder holder, final int position) {
        final Job job = mJobList.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(job.getTimestamp()));
        holder.mJobPostedDate.setText(getTime(Calendar.getInstance(), calendar));
        holder.mJobLocation.setText(job.getLocation());
        holder.mJobTitle.setText(job.getName());
        holder.mJobSalary.setText(job.getSalary());

        if (ListRecruitmentActivity.getInstance() == context) {
            holder.mOption.setText(context.getResources().getString(R.string.option));
            holder.mOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupWindow(view, R.menu.list_recruitment_menu, position, job);
                }
            });
        } else {
            holder.mOption.setText("");
            holder.mOption.setBackground(ContextCompat.getDrawable(context, R.drawable.delete));
            holder.mOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserManager.getInstance().removeFavouriteJob(job.getId());
                    JobLikedFragment.getInstance().refreshData();
                }
            });
        }

    }

    private String getTime(Calendar current, Calendar postingDate) {
        String s = "";
        int minus = current.get(Calendar.DAY_OF_MONTH) - postingDate.get(Calendar.DAY_OF_MONTH);
        if (minus < 2) {
            if (minus == 1) {
                s = "Hôm qua lúc " + new SimpleDateFormat("hh:mm").format(postingDate.getTime());
            } else if (minus == 0) {
                int minus1 = current.get(Calendar.HOUR_OF_DAY) - postingDate.get(Calendar.HOUR_OF_DAY);
                if (minus1 > 0) {
                    s = minus1 + " giờ trước";
                } else {
                    if (current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) == 0) {
                        s = "1 phút trước";
                    } else {
                        s = current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) + " phút trước";
                    }
                }
            }
        } else {
            s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
        }

        return s;
    }

    private void showPopupWindow(View view, int menu, final int position, final Job job) {
        PopupMenu popup = new PopupMenu(this.context, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.getMenuInflater().inflate(menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recruitment_item_edit:
                        Intent intent = new Intent(context, UpdateJobActivity.class);
                        intent.putExtra("job", job);
                        context.startActivity(intent);
                }
                return true;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return mJobList.size();
    }

    class JobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mJobTitle, mJobLocation, mJobSalary, mJobPostedDate, mOption;

        public JobItemViewHolder(View itemView) {
            super(itemView);

            mJobTitle = itemView.findViewById(R.id.tv_job_item_title);
            mJobSalary = itemView.findViewById(R.id.tv_job_item_salary);
            mJobLocation = itemView.findViewById(R.id.tv_job_item_location);
            mJobPostedDate = itemView.findViewById(R.id.tv_job_item_date);
            mOption = itemView.findViewById(R.id.tv_job_item_menu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
