/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.iflytek.aiui.demo.chat.ui.chat.adapter;

import android.annotation.SuppressLint;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The of the ViewDataBinding
 */
public abstract class DataBoundListAdapter<T, V extends ViewDataBinding>
        extends RecyclerView.Adapter<DataBoundViewHolder<V>> {

    private static ExecutorService FULL_TASK_EXECUTOR=(ExecutorService) Executors.newCachedThreadPool();

    @Nullable
    private List<T> items;
    // each time data is set, we update this variable so that if DiffUtil calculation returns
    // after repetitive updates, we can ignore the old calculation
    private int dataVersion = 0;
    @Override
    public final DataBoundViewHolder<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        V binding = createBinding(parent, viewType);
        return new DataBoundViewHolder<>(binding);
    }

    protected abstract V createBinding(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(DataBoundViewHolder<V> holder, int position) {
        //noinspection ConstantConditions
        bind(holder.binding, items.get(position));
        holder.binding.executePendingBindings();
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    public void replace(final List<T> update) {
        dataVersion ++;
        if (items == null) {
            if (update == null) {
                return;
            }
            items = update;
            notifyDataSetChanged();
        } else if (update == null) {
            int oldSize = items.size();
            items = null;//
            notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = dataVersion;
            final List<T> oldItems = items;
            MyAsync myAsync=new MyAsync(startVersion,oldItems,update);
            myAsync.executeOnExecutor(FULL_TASK_EXECUTOR);
        }
    }

    class MyAsync extends AsyncTask<Void, Void, DiffUtil.DiffResult>{
        int startVersion;
        List<T> oldItems;
        List<T> update;
        MyAsync(final int startVersion,final List<T> oldItems,final List<T> update ){
            Log.d("xb","MyAsync"+startVersion);
            this.startVersion=startVersion;
            this.oldItems=oldItems;
            this.update=update;
        }

        @Override
        protected DiffUtil.DiffResult  doInBackground(Void... voids) {
            Log.d("xb","doInBack"+startVersion);
            return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldItems.size();
                }

                @Override
                public int getNewListSize() {
                    return update.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    T oldItem = oldItems.get(oldItemPosition);
                    T newItem = update.get(newItemPosition);
                    return DataBoundListAdapter.this.areItemsTheSame(oldItem, newItem);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    T oldItem = oldItems.get(oldItemPosition);
                    T newItem = update.get(newItemPosition);
                    return DataBoundListAdapter.this.areContentsTheSame(oldItem, newItem);
                }
            });
        }

        @Override
        protected void onPostExecute(DiffUtil.DiffResult diffResult) {
            if (startVersion != dataVersion) {
                // ignore update
                return;
            }
            items = update;
            diffResult.dispatchUpdatesTo(DataBoundListAdapter.this);

        }
    }

    protected abstract void bind(V binding, T item);

    protected abstract boolean areItemsTheSame(T oldItem, T newItem);

    protected abstract boolean areContentsTheSame(T oldItem, T newItem);

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
