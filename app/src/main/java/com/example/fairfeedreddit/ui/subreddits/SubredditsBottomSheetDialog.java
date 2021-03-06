package com.example.fairfeedreddit.ui.subreddits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fairfeedreddit.R;
import com.example.fairfeedreddit.model.SubredditEntity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

import static com.example.fairfeedreddit.utils.AppConstants.SHOW_LESS_OFTEN_POSTS_KEY;
import static com.example.fairfeedreddit.utils.AppConstants.SUBREDDIT_KEY;

public class SubredditsBottomSheetDialog extends BottomSheetDialogFragment {

    private Unbinder unbinder;
    private SubredditEntity subreddit;
    private OnActionItemClickListener mListener;

    @BindView(R.id.go_to_subreddit_action)
    TextView goToSubredditTV;

    @BindView(R.id.update_show_less_often_action)
    TextView showLessOftenTV;

    @BindView(R.id.leave_subreddit_action)
    TextView leaveSubredditTV;

    private boolean shouldShowLessOften;

    public SubredditsBottomSheetDialog() {}

    private SubredditsBottomSheetDialog(OnActionItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subreddits_bottom_sheet, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            subreddit = (SubredditEntity) getArguments().getSerializable(SUBREDDIT_KEY);
            shouldShowLessOften = getArguments().getBoolean(SHOW_LESS_OFTEN_POSTS_KEY);
        }

        if (subreddit != null) {
            String subredditName = subreddit.getName();
            goToSubredditTV.setText(String.format(getString(R.string.go_to_subreddit_action), subredditName));
            showLessOftenTV.setText(getString(shouldShowLessOften ? R.string.show_less_often_action : R.string.show_more_often_action));
            leaveSubredditTV.setText(String.format(getString(R.string.leave_subreddit_action), subredditName));
            subreddit.setShouldShowLessOften(shouldShowLessOften);
        }

        // Credit for fully expanding bottom sheet dialog in landscape mode: https://medium.com/@OguzhanAlpayli/bottom-sheet-dialog-fragment-expanded-full-height-65b725c8309
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View bottomSheet = ((BottomSheetDialog) Objects.requireNonNull(getDialog())).findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(Objects.requireNonNull(bottomSheet));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setPeekHeight(0);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    static SubredditsBottomSheetDialog newInstance(OnActionItemClickListener mListener) {
        return new SubredditsBottomSheetDialog(mListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SUBREDDIT_KEY, subreddit);
    }

    @Optional
    @OnClick({R.id.go_to_subreddit_action, R.id.update_show_less_often_action, R.id.leave_subreddit_action})
    void onClick(View view) {
        mListener.onActionItemClick(view.getId(), subreddit);
    }

    void clearListener() {
        this.mListener = null;
    }

    public interface OnActionItemClickListener {
        void onActionItemClick(int id, SubredditEntity subreddit);
    }
}