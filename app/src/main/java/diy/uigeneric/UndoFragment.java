package diy.uigeneric;

import android.animation.Animator;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * A simple undo fragment.
 */
public class UndoFragment extends Fragment {

    private Listener listener = null;
    private List<Long> list = null;
    private TextView textDeleted = null;
    private TextView textUndo = null;
    private ViewGroup container = null;
    private Runnable runnable = null;
    private Handler handlers = null;

    public UndoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_undo, container, false);
        textDeleted = (TextView) view.findViewById(R.id.text_deleted);
        textUndo = (TextView) view.findViewById(R.id.text_undo);
        textUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlers.removeCallbacks(runnable);
                if (list != null) {
                    GenericDataSource source = new GenericDataSource(getActivity());
                    source.open();
                    source.restoreList(list);
                    source.close();
                    if (listener != null)
                        listener.refresh();
                    list = null;
                    hideAnimate();
                }
            }
        });
        this.container = (ViewGroup) view;

        runnable = new Runnable() {
            @Override
            public void run() {
                handlers.removeCallbacks(runnable);
                list = null;
                hideAnimate();
            }
        };
        handlers = new Handler();

        return view;
    }

    public void setDeleted(List<Long> list) {
        handlers.removeCallbacks(runnable);
        if (list != null && list.size() > 0) {
            this.list = list;
            String text = String.format("deleted %d item(s)", list.size());
            textDeleted.setText(text);
            container.setVisibility(View.VISIBLE);
            container.animate()
                    .translationY(0)
                    .setDuration(250)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
            handlers.postDelayed(runnable, 5000);
        }
    }

    public void setListener(Listener value) {
        listener = value;
    }

    public void hide() {
        handlers.removeCallbacks(runnable);
        container.setVisibility(View.GONE);
    }

    public void hideAnimate() {
        handlers.removeCallbacks(runnable);
        if (container.getVisibility() != View.GONE) {
            container.animate()
                    .translationY(container.getHeight())
                    .setDuration(250)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            container.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
        }
    }

    public interface Listener {
        public void refresh();
    }

}
