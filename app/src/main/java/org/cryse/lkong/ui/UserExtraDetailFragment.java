package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.TimeFormatUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserExtraDetailFragment extends AbstractFragment {
    public static final String LOG_TAG = UserExtraDetailFragment.class.getSimpleName();

    @InjectView(R.id.fragment_user_detail_cardview_introduction)
    CardView mIntroductionCardView;
    @InjectView(R.id.fragment_user_detail_cardview_wealth)
    CardView mWealthCardView;
    @InjectView(R.id.fragment_user_detail_cardview_punch)
    CardView mPunchCardView;
    @InjectView(R.id.fragment_user_detail_cardview_registration_time)
    CardView mRegistrationTimeCardView;


    @InjectView(R.id.fragment_user_detail_textview_introduction)
    TextView mIntroductionTextView;
    @InjectView(R.id.fragment_user_detail_textview_wealth1)
    TextView mActivePointsTextView;
    @InjectView(R.id.fragment_user_detail_textview_wealth2)
    TextView mDragonMoneyTextView;
    @InjectView(R.id.fragment_user_detail_textview_wealth3)
    TextView mDragonCrystalTextView;

    @InjectView(R.id.fragment_user_detail_textview_punch1)
    TextView mCurrentContinuousPunchDaysTextView;
    @InjectView(R.id.fragment_user_detail_textview_punch2)
    TextView mLongestContinuousPunchDaysTextView;
    @InjectView(R.id.fragment_user_detail_textview_punch3)
    TextView mLastPunchTimeTextView;
    @InjectView(R.id.fragment_user_detail_textview_punch4)
    TextView mTotalPunchDaysTextView;

    @InjectView(R.id.fragment_user_detail_textview_registration_time)
    TextView mRegistrationTextView;


    public UserInfoModel mUserInfo = null;

    public static UserExtraDetailFragment newInstance() {
        Bundle args = new Bundle();
        UserExtraDetailFragment fragment = new UserExtraDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_user_detail, container, false);
        ButterKnife.inject(this, contentView);
        displayUserInfo();
        return contentView;
    }

    public void setUserInfo(UserInfoModel userInfo) {
        this.mUserInfo = userInfo;
        displayUserInfo();
    }

    public void displayUserInfo() {
        if(mUserInfo == null) {
            mIntroductionCardView.setVisibility(View.GONE);
            mWealthCardView.setVisibility(View.GONE);
            mPunchCardView.setVisibility(View.GONE);
            mRegistrationTimeCardView.setVisibility(View.GONE);
            return;
        }

        // Display Introduction Info
        if(!TextUtils.isEmpty(mUserInfo.getSigHtml())) {
            mIntroductionCardView.setVisibility(View.VISIBLE);
            String introduction = mUserInfo.getSigHtml();
            mIntroductionTextView.setText(introduction);
        } else {
            mIntroductionCardView.setVisibility(View.GONE);
        }

        // Display Wealth Info
        mWealthCardView.setVisibility(View.VISIBLE);
        String activePoints = getString(R.string.format_active_points, mUserInfo.getActivePoints());
        mActivePointsTextView.setText(activePoints);
        String dragonCrystal = getString(R.string.format_dragon_crystal, mUserInfo.getDragonCrystal());
        mDragonCrystalTextView.setText(dragonCrystal);
        if(mUserInfo.getUid() == mUserInfo.getMe()) {
            String dragonMoney = getString(R.string.format_dragon_money, mUserInfo.getDragonMoney());
            mDragonMoneyTextView.setText(dragonMoney);
            mDragonMoneyTextView.setVisibility(View.VISIBLE);
        } else {
            mDragonMoneyTextView.setVisibility(View.GONE);
        }

        // Display Punch Info
        mPunchCardView.setVisibility(View.VISIBLE);
        String currentContinuousPunchDays = getString(R.string.format_current_continuous_punch_days, mUserInfo.getCurrentContinuousPunch());
        mCurrentContinuousPunchDaysTextView.setText(currentContinuousPunchDays);
        String longestContinuousPunchDays = getString(R.string.format_longest_continuous_punch_days, mUserInfo.getLongestContinuousPunch());
        mLongestContinuousPunchDaysTextView.setText(longestContinuousPunchDays);
        String totalPunchDays = getString(R.string.format_total_punch_days, mUserInfo.getTotalPunchCount());
        mTotalPunchDaysTextView.setText(totalPunchDays);
        String lastPunchTime = getString(
                R.string.format_last_punch_time,
                (mUserInfo.getTotalPunchCount() > 0 ? TimeFormatUtils.formatDate(getActivity(), mUserInfo.getLastPunchTime(), false) : ""));
        mLastPunchTimeTextView.setText(lastPunchTime);

        // Display Registration Time
        mRegistrationTimeCardView.setVisibility(View.VISIBLE);
        String regTime = getString(R.string.format_registration_time, TimeFormatUtils.formatDate(getActivity(), mUserInfo.getRegDate(), true));
        mRegistrationTextView.setText(regTime);
    }
}
