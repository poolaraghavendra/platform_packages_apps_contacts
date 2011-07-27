/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.contacts.quickcontact;

import com.android.contacts.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/** A fragment that shows the list of resolve items below a tab */
public class QuickContactListFragment extends Fragment {
    private ListView mListView;
    private List<Action> mActions;
    private LinearLayout mFragmentContainer;
    private Listener mListener;

    public QuickContactListFragment() {
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        mFragmentContainer = (LinearLayout) inflater.inflate(R.layout.quickcontact_list_fragment,
                container, false);
        mListView = (ListView) mFragmentContainer.findViewById(R.id.list);
        mListView.setOnItemClickListener(mItemClickListener);
        mFragmentContainer.setOnClickListener(mOutsideClickListener);
        configureAdapter();
        return mFragmentContainer;
    }

    public void setActions(List<Action> actions) {
        mActions = actions;
        configureAdapter();
    }

    public void setListener(Listener value) {
        mListener = value;
    }

    private void configureAdapter() {
        if (mActions == null || mListView == null) return;

        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mActions.size();
            }

            @Override
            public Object getItem(int position) {
                return mActions.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final View resultView = convertView != null ? convertView
                        : getActivity().getLayoutInflater()
                        .inflate(R.layout.quickcontact_list_item, parent, false);

                // Set action title based on summary value
                final Action action = mActions.get(position);

                // TODO: Put those findViewByIds in a container
                final TextView text1 = (TextView) resultView.findViewById(
                        android.R.id.text1);
                final TextView text2 = (TextView) resultView.findViewById(
                        android.R.id.text2);
                final ImageView alternateActionButton = (ImageView) resultView.findViewById(
                        R.id.secondary_action_button);
                final View alternateActionDivider = resultView.findViewById(R.id.vertical_divider);

                alternateActionButton.setOnClickListener(mSecondaryActionClickListener);
                alternateActionButton.setTag(action);

                final boolean hasAlternateAction = action.getAlternateIntent() != null;
                alternateActionDivider.setVisibility(hasAlternateAction ? View.VISIBLE : View.GONE);
                alternateActionButton.setImageDrawable(action.getAlternateIcon());

                text1.setText(action.getBody());
                text2.setText(action.getSubtitle().toString().toUpperCase());

                resultView.setTag(action);
                return resultView;
            }
        });
    }

    /** A secondary action (SMS) was clicked */
    protected final OnClickListener mSecondaryActionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Action action = (Action) v.getTag();
            if (mListener != null) mListener.onItemClicked(action, true);
        }
    };

    /** A data item (e.g. phone number) was clicked */
    private final AbsListView.OnItemClickListener mItemClickListener =
            new AbsListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Action action = (Action) view.getTag();
            if (mListener != null) mListener.onItemClicked(action, false);
        }
    };

    private final OnClickListener mOutsideClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onOutsideClick();
        }
    };

    public interface Listener {
        void onOutsideClick();
        void onItemClicked(Action action, boolean alternate);
    }
}
