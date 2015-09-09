package com.angcyo.y2androidapi.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.y2androidapi.R;
import com.angcyo.y2androidapi.control.DataPool;
import com.angcyo.y2androidapi.util.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class MenuFragment extends Fragment {

    public static int TYPE_CLASSIFY = 1;
    public static int TYPE_LINK = 0;

    @Bind(R.id.menu_tip_layout)
    LinearLayout mMenuTipLayout;

    @Bind(R.id.menu_tip_text)
    TextView mMenuTipText;

    @Bind(R.id.menu_tip_count)
    TextView mMenuTipCount;

    @Bind(R.id.recyclerViewLinks)
    RecyclerView mMenuLinksView;

    @Bind(R.id.recyclerViewClassify)
    RecyclerView mMenuClassifyView;
    private LinksAdapter linksAdapter;
    private ClassifyAdapter classifyAdapter;
    private Handler handler;

    private LinearLayoutManager linksLayoutManager;
    private OnMenuItemClickListener menuItemClickListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        handler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_layout, container, false);
        ButterKnife.bind(this, view);

        initLinksLayout();
        initClassifyLayout();
        initMenuTipLayout();
        return view;
    }

    private void initMenuTipLayout() {
        mMenuTipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuTipLayout.setVisibility(View.GONE);
                classifyAdapter.setData(linksAdapter.getData().getTypeList(), linksAdapter.getData().getTypeCountList());
                mMenuClassifyView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.tran_height_anim)));
                mMenuClassifyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initClassifyLayout() {
        classifyAdapter = new ClassifyAdapter();
        classifyAdapter.setOnItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onItemClick(View parentV, final int position, Object values) {
                Logger.e("位置:" + position + " 文本:" + values);
                mMenuClassifyView.setVisibility(View.GONE);
                linksLayoutManager.scrollToPosition((linksLayoutManager.getItemCount() - 1) < 0 ? 0 : (linksLayoutManager.getItemCount() - 1));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linksLayoutManager.scrollToPosition(linksAdapter.getData().getTypeIndex().get(position));
                    }
                }, 100);
            }
        });
        mMenuClassifyView.setAdapter(classifyAdapter);
    }

    private void initLinksLayout() {
        linksLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mMenuLinksView.setLayoutManager(linksLayoutManager);
        mMenuClassifyView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        linksAdapter = new LinksAdapter();
        linksAdapter.setOnItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onItemClick(View parentV, int position, Object values) {
//                Logger.e("位置:" + position + " 类型:" + linksAdapter.getItemViewType(position)
//                        + " 文本:" + ((AndroidClassLinkBean) values).getText()
//                        + " 链接:" + ((AndroidClassLinkBean) values).getLink());
                if (linksAdapter.getItemViewType(position) == TYPE_CLASSIFY) {
                    classifyAdapter.setData(linksAdapter.getData().getTypeList(), linksAdapter.getData().getTypeCountList());
                    mMenuClassifyView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.tran_height_anim)));
                    mMenuClassifyView.setVisibility(View.VISIBLE);
                } else {
                    if (menuItemClickListener != null) {
                        menuItemClickListener.onItemClick(parentV, position, values);
                    }
                }

            }
        });
        mMenuLinksView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = linksLayoutManager.findFirstVisibleItemPosition();
                if (linksAdapter.getItemViewType(position) != 1) {
                    mMenuTipLayout.setVisibility(View.VISIBLE);
                    mMenuTipText.setText(linksAdapter.getNearTypeName(position));
                    mMenuTipCount.setText(linksAdapter.getData().getTypeCountList().get(
                            DataPool.getClassTypeIndex(
                                    linksAdapter.getData(), linksAdapter.getNearTypeName(position))) + "");
                } else {
                    mMenuTipLayout.setVisibility(View.GONE);
                }
            }
        });
        mMenuLinksView.setAdapter(linksAdapter);
    }

    public void setData(DataPool pool) {
        if (linksAdapter != null)
            linksAdapter.setData(pool);
    }

    public void setOnMenuClick(OnMenuItemClickListener listener) {
        menuItemClickListener = listener;
    }

    public boolean isClassifyShow() {
        if (mMenuClassifyView != null) {
            return mMenuClassifyView.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    public void hideClassifyView() {
        if (mMenuClassifyView != null && mMenuClassifyView.getVisibility() == View.VISIBLE) {
            mMenuClassifyView.setVisibility(View.GONE);
        }
    }

    private class LinksAdapter extends RecyclerView.Adapter<SimplerViewHolder> {

        private DataPool dataPool;
        private OnMenuItemClickListener itemClick;

        @Override
        public SimplerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_link_item, parent, false);
            if (viewType == TYPE_CLASSIFY) {
                view.findViewById(R.id.menu_bg).setBackgroundResource(R.drawable.classify_bg_selector);
            }
            return new SimplerViewHolder(view);
        }

        public void setOnItemClickListener(OnMenuItemClickListener listener) {
            itemClick = listener;
        }

        @Override
        public void onBindViewHolder(final SimplerViewHolder holder, final int position) {
            holder.itemTextView.setText(dataPool.allLinks.get(position).getText());
            if (getItemViewType(position) == TYPE_CLASSIFY) {
                holder.itemCountTextView.setText(dataPool.getTypeCountList().get(getTypeCount(position)) + "");
            }

            //Item 事件响应
            holder.itemBgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClick != null) {
                        itemClick.onItemClick(holder.itemView, position, dataPool.allLinks.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return (dataPool == null || dataPool.allLinks == null) ? 0 : dataPool.allLinks.size();
        }

        @Override
        public int getItemViewType(int position) {
            return DataPool.isClassType(dataPool, position) ? TYPE_CLASSIFY : TYPE_LINK;
        }

        /**
         * 返回当前位置,上面最近的类型文本
         *
         * @param position the position
         * @return the string
         */
        public String getNearTypeName(int position) {
            int n = 0;
            for (int i = 0; i < dataPool.getTypeIndex().size(); i++) {
                n = dataPool.getTypeIndex().get(i);
                if (n < position) {
                    continue;
                }
                return dataPool.getTypeList().get((i - 1) < 0 ? 0 : (i - 1));
            }
            return dataPool.getTypeList().get(0);
        }

        public int getTypeCount(int position) {
            return DataPool.getClassTypeIndex(dataPool, dataPool.allLinks.get(position).getText());
        }

        public void setData(DataPool pool) {
            dataPool = pool;
            dataPool.parse();
            notifyDataSetChanged();
        }

        public DataPool getData() {
            return dataPool;
        }
    }

    private class ClassifyAdapter extends RecyclerView.Adapter<SimplerViewHolder> {
        private List<String> links;
        private List<Integer> dadaCount;
        private OnMenuItemClickListener itemClick;

        @Override
        public SimplerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_link_item, parent, false);
            view.findViewById(R.id.menu_bg).setBackgroundResource(R.drawable.classify_bg_selector);
            return new SimplerViewHolder(view);
        }

        public void setOnItemClickListener(OnMenuItemClickListener listener) {
            itemClick = listener;
        }

        @Override
        public void onBindViewHolder(final SimplerViewHolder holder, final int position) {
            holder.itemTextView.setText(links.get(position));
            holder.itemCountTextView.setText(dadaCount.get(position) + "");

            //Item 事件响应
            holder.itemBgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClick != null) {
                        itemClick.onItemClick(holder.itemView, position, links.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return links == null ? 0 : links.size();
        }

        public void setData(List<String> data, List<Integer> dadaCount) {
            links = data;
            this.dadaCount = dadaCount;
            notifyDataSetChanged();
        }
    }


    private class SimplerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView itemTextView, itemCountTextView;
        private LinearLayout itemBgView;

        public SimplerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemTextView = (TextView) itemView.findViewById(R.id.menu_text);
            itemBgView = (LinearLayout) itemView.findViewById(R.id.menu_bg);
            itemCountTextView = (TextView) itemView.findViewById(R.id.menu_count);
        }
    }

    public interface OnMenuItemClickListener<T> {
        void onItemClick(View parentV, int position, T values);
    }
}
