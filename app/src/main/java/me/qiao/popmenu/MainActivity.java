package me.qiao.popmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

import me.qiao.pop.PopMenu;
import me.qiao.popmenu.bean.SampleDataSource;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, PopMenu.IClickHandler {

    private PopMenu mMenu;
    private View mOptionView;
    private ListView mListView;
    private List mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mListView = createListView());
    }

    private ListView createListView() {
        ListView listView = new ListView(this);
        listView.setDivider(null);
        listView.setScrollbarFadingEnabled(false);
        listView.setAdapter(new CommentArrayAdapter(this, mDatas = SampleDataSource.prepareCommentsArray()));
        listView.setOnItemClickListener(this);
        return listView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = view.getTag();
        if (obj instanceof CommentArrayAdapter.ViewHolder) {

            mMenu = PopMenu.Builder
                    .create(this)
                    .bindItemView(view)
                    .withMenu(generateMenu())
                    .deprecatedLine(((CommentArrayAdapter.ViewHolder) obj).line2)
                    .onDismiss(this)
                    .build();
            mMenu.show();
        }
    }

    private View generateMenu() {
        if (mOptionView == null) {
            mOptionView = getLayoutInflater().inflate(R.layout.popmenu, null);
            mOptionView.setOnClickListener(this);
        }
        if (mOptionView.getParent() instanceof ViewGroup) {
            ((ViewGroup) mOptionView.getParent()).removeView(mOptionView);
        }
        return mOptionView;
    }

    @Override
    public void onClick(View v) {
        if (mMenu != null) {
            mMenu.dismiss(true);
        }
    }

    @Override
    public void onMenuDismiss(boolean isClicked, final View itemView) {
        if (isClicked) {
            final ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            final int originalHeight = itemView.getHeight();
            ValueAnimator heightAnimator = ValueAnimator.ofInt(originalHeight, 0);
            heightAnimator.setDuration(350);
            heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    lp.height = (Integer) valueAnimator.getAnimatedValue();
                    itemView.setLayoutParams(lp);
                }
            });
            heightAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (itemView.getTag() instanceof CommentArrayAdapter.ViewHolder) {
                        CommentArrayAdapter.ViewHolder holder = (CommentArrayAdapter.ViewHolder) itemView.getTag();
                        mDatas.remove(holder.position);
                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                    }
                }
            });
            heightAnimator.start();
        }
    }
}
