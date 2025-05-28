package c.plus.plan.skits.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import c.plus.plan.jowosdk.entity.Banner;
import c.plus.plan.jowosdk.entity.Block;
import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.Feedback;
import c.plus.plan.jowosdk.entity.Label;
import c.plus.plan.jowosdk.entity.Language;
import c.plus.plan.jowosdk.entity.PageResult;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.skits.DataApiInfo;
import c.plus.plan.skits.databinding.ActivityDataApiBinding;
import c.plus.plan.skits.ui.adapter.DataApiAdapter;

public class DataApiActivity extends AppCompatActivity {
    private List<DataApiInfo> apiList = new ArrayList<>();
    private ActivityDataApiBinding mBinding;
    private DataApiAdapter mAdapter;
    private long mStartAt;
    private DataApiInfo mCurrentApi;
    private HashMap params = new HashMap();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDataApiBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        apiList.add(new DataApiInfo("fetchForU", "请求for you列表", null));
        apiList.add(new DataApiInfo("fetchBanner", "请求Banner列表", null));
        apiList.add(new DataApiInfo("lastBanner", "本地缓存Banner列表", null));
        apiList.add(new DataApiInfo("fetchBlocks", "请求块列表", null));
        apiList.add(new DataApiInfo("lastBlocks", "本地缓存块列表", null));
        apiList.add(new DataApiInfo("fetchLabels", "请求Label列表", null));
        apiList.add(new DataApiInfo("lastLabels", "本地缓存Label列表", null));
        apiList.add(new DataApiInfo("fetchLanguages", "请求支持的语言列表", null));

        apiList.add(new DataApiInfo("fetchDramasPerPage", "分页请求所有短剧", null));
        apiList.add(new DataApiInfo("lastDramas", "获取本地缓存短剧", null));
        apiList.add(new DataApiInfo("fetchDrama", "请求短剧详情", "短剧ID"));
        apiList.add(new DataApiInfo("fetchDramaList", "请求短剧列表", "短剧ID Int数组"));
        apiList.add(new DataApiInfo("fetchDramasByLabelKeyPerPage", "分页请求短剧列表label", "标签key,分页数据(首页传0)"));
        apiList.add(new DataApiInfo("lastDramasByLabelKey", "获取缓存短剧列表label", "标签key"));
        apiList.add(new DataApiInfo("fetchDramasByBlockKeyPerPage", "分页请求短剧列表block", "块key,分页数据(首页传0)"));
        apiList.add(new DataApiInfo("lastDramasByBlockKey", "获取缓存短剧列表block", "块key"));

        apiList.add(new DataApiInfo("fetchEpisodesGreaterNum", "请求短剧剧集列表", "短剧id,短剧开始的集数"));
        apiList.add(new DataApiInfo("lastEpisodesByDramaId", "获取本地缓存短剧剧集", "短剧id"));
        apiList.add(new DataApiInfo("fetchEpisodesByNum", "请求短剧指定剧集详情", "短剧id,短剧集数"));
        apiList.add(new DataApiInfo("fetchBlockDrama", "请求所有块以及对应短剧列表", null));
        apiList.add(new DataApiInfo("lastBlockDrama", "本地缓存的所有块以及对应的短剧列表", null));

        apiList.add(new DataApiInfo("postPlayEpisodes", "点播短剧", "短剧ID,点播集数,点播语言"));
        apiList.add(new DataApiInfo("postDramaHistory", "上报播放历史", "短剧ID,播放集数,播放进度毫秒"));
        apiList.add(new DataApiInfo("postDramaCollect", "收藏短剧", "短剧ID,收藏语言"));
        apiList.add(new DataApiInfo("fetchUserDramaCollectsPerPage", "分页请求短剧收藏列表", "分页数据(首页传0)"));
        apiList.add(new DataApiInfo("deleteDramaCollectCancel", "取消短剧收藏", "短剧ID"));
        apiList.add(new DataApiInfo("fetchUserDrama", "请求指定短剧当前用户解锁集数", "短剧ID"));
        apiList.add(new DataApiInfo("searchDramas", "搜索短剧", "关键字,分页数据(首页传0)"));

        apiList.add(new DataApiInfo("lastUserDrama", "获取本地指定短剧当前用户解锁集数", "短剧ID"));
        apiList.add(new DataApiInfo("fetchUserDramaHistoriesPerPage", "分页请求短剧播放历史", "分页数据(首页传0)"));
        apiList.add(new DataApiInfo("lastHistories", "本地缓存播放历史(最多20条)", null));
        apiList.add(new DataApiInfo("lastHistoryByDramaId", "本地缓存指定短剧播放历史", "短剧ID"));
        apiList.add(new DataApiInfo("fetchEpisodeShareUrl", "请求剧集的分享url", "短剧ID,短剧集数"));
        apiList.add(new DataApiInfo("postFeedback", "举报/反馈", "短剧ID,短剧集数"));
//        apiList.add(new DataApiInfo("searchDrama", "搜索短剧", "短剧ID,语言"));

        mAdapter = new DataApiAdapter();
        mAdapter.setDataApiInfos(apiList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rv.setAdapter(mAdapter);
        mBinding.rv.setLayoutManager(layoutManager);

        mAdapter.setOnItemClickListener(new DataApiAdapter.OnItemClickListener() {
            @Override
            public void click(int position, DataApiInfo item) {
                if (TextUtils.isEmpty(item.getParamsDesc())) {
                    callApi(item);
                } else {
                    mCurrentApi = item;
                    mBinding.et.setText(null);
                    mBinding.params.setVisibility(View.VISIBLE);
                    mBinding.tvParams.setText(item.getParamsDesc());
                }
            }
        });

        mBinding.btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mBinding.et.getText().toString())) {
                ToastUtils.showLong("请输入参数");
                return;
            }

            if ("fetchDrama".equals(mCurrentApi.getMethod())) {
                int dramaId = Integer.parseInt(mBinding.et.getText().toString());
                if (dramaId <= 0) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }
                params.put("dramaId", dramaId);

            } else if ("fetchDramaList".equals(mCurrentApi.getMethod())) {
                String[] dramaIds = mBinding.et.getText().toString().split(",");

                if (dramaIds.length == 0) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                int[] ids = new int[dramaIds.length];
                for (int i = 0; i < dramaIds.length; i++) {
                    ids[i] = Integer.parseInt(dramaIds[i].trim());
                }
                params.put("ids", ids);
            } else if ("fetchDramasByLabelKeyPerPage".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("labelKey", inParams[0]);
                params.put("cursorId", Integer.parseInt(inParams[1].trim()));
            } else if ("lastDramasByLabelKey".equals(mCurrentApi.getMethod())) {
                params.put("labelKey", mBinding.et.getText().toString());
            } else if ("fetchDramasByBlockKeyPerPage".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("blockKey", inParams[0]);
                params.put("cursorId", Integer.parseInt(inParams[1].trim()));
            } else if ("lastDramasByBlockKey".equals(mCurrentApi.getMethod())) {
                params.put("blockKey", mBinding.et.getText().toString());
            } else if ("fetchEpisodesGreaterNum".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("dramaId", String.valueOf(inParams[0].trim()));
                params.put("greaterEpisodesNum", Integer.parseInt(inParams[1].trim()));
            } else if ("lastDramasByBlockKey".equals(mCurrentApi.getMethod())) {
                params.put("dramaId", String.valueOf(mBinding.et.getText().toString()));
            } else if ("fetchEpisodesByNum".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("dramaId", String.valueOf(inParams[0].trim()));
                params.put("episodesNum", Integer.parseInt(inParams[1].trim()));
            } else if ("postPlayEpisodes".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 3) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("dramaId", String.valueOf(inParams[0]));
                params.put("episodesNum", Integer.parseInt(inParams[1].trim()));
                params.put("language", inParams[2]);
            } else if ("postDramaHistory".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 3) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("dramaId", String.valueOf(inParams[0].trim()));
                params.put("episodesNum", Integer.parseInt(inParams[1].trim()));
                params.put("progressTimeMillis", Long.parseLong(inParams[2].trim()));
            } else if ("postDramaCollect".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("dramaId", String.valueOf(inParams[0].trim()));
                params.put("language", inParams[1]);
            } else if ("fetchUserDramaCollectsPerPage".equals(mCurrentApi.getMethod())) {
                params.put("cursorId", Integer.parseInt(mBinding.et.getText().toString()));
            } else if ("deleteDramaCollectCancel".equals(mCurrentApi.getMethod())) {
                params.put("dramaId", String.valueOf(mBinding.et.getText().toString()));
            } else if ("fetchUserDrama".equals(mCurrentApi.getMethod())) {
                params.put("dramaId", String.valueOf(mBinding.et.getText().toString()));
            } else if ("lastUserDrama".equals(mCurrentApi.getMethod())) {
                params.put("dramaId", String.valueOf(mBinding.et.getText().toString()));
            } else if ("fetchUserDramaHistoriesPerPage".equals(mCurrentApi.getMethod())) {
                params.put("cursorId", Integer.parseInt(mBinding.et.getText().toString()));
            } else if ("lastHistoryByDramaId".equals(mCurrentApi.getMethod())) {
                params.put("dramaId", String.valueOf(mBinding.et.getText().toString()));
            } else if ("searchDramas".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }
                params.put("keywords", inParams[0]);
                params.put("cursorId", Integer.parseInt(inParams[1].trim()));
            } else if ("fetchEpisodeShareUrl".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("dramaId", String.valueOf(inParams[0].trim()));
                params.put("episodesNum", Integer.parseInt(inParams[1].trim()));
            } else if ("postFeedback".equals(mCurrentApi.getMethod())) {
                String[] inParams = mBinding.et.getText().toString().split(",");
                if (inParams.length != 2) {
                    ToastUtils.showLong("参数错误，请仔细检查输入的参数");
                    return;
                }

                params.put("dramaId", String.valueOf(inParams[0].trim()));
                params.put("episodesNum", Integer.parseInt(inParams[1].trim()));
            }

            mBinding.params.setVisibility(View.GONE);
            callApi(mCurrentApi);
        });

        mBinding.result.setOnClickListener(v -> {
            mBinding.result.setVisibility(View.GONE);
        });

        mBinding.params.setOnClickListener(v -> {
            mBinding.params.setVisibility(View.GONE);
        });

    }

    private void callApi(DataApiInfo item) {
        mStartAt = System.currentTimeMillis();
        mBinding.loading.setVisibility(View.VISIBLE);
        mBinding.result.setVisibility(View.VISIBLE);
        if ("fetchBanner".equals(item.getMethod())) {
            JOWOSdk.fetchBanner(new Callback<>() {
                @Override
                public void result(Result<List<Banner>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("fetchBlocks".equals(item.getMethod())) {
            JOWOSdk.fetchBlocks(new Callback<List<Block>>() {
                @Override
                public void result(Result<List<Block>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("fetchLabels".equals(item.getMethod())) {
            JOWOSdk.fetchLabels(new Callback<List<Label>>() {
                @Override
                public void result(Result<List<Label>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("fetchLanguages".equals(item.getMethod())) {
            JOWOSdk.fetchLanguages(new Callback<List<Language>>() {
                @Override
                public void result(Result<List<Language>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("lastBanner".equals(item.getMethod())) {
            JOWOSdk.lastBanner(new Callback<List<Banner>>() {
                @Override
                public void result(Result<List<Banner>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("lastBlocks".equals(item.getMethod())) {
            JOWOSdk.lastBlocks(new Callback<List<Block>>() {
                @Override
                public void result(Result<List<Block>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("lastLabels".equals(item.getMethod())) {
            JOWOSdk.lastLabels(new Callback<List<Label>>() {
                @Override
                public void result(Result<List<Label>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("fetchDramasPerPage".equals(item.getMethod())) {
            JOWOSdk.fetchDramasPerPage(0, new Callback<PageResult<List<Drama>>>() {
                @Override
                public void result(Result<PageResult<List<Drama>>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("fetchDrama".equals(item.getMethod())) {
            JOWOSdk.fetchDrama(String.valueOf( params.get("dramaId")), new Callback<Drama>() {
                @Override
                public void result(Result<Drama> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("fetchDramaList".equals(item.getMethod())) {
            JOWOSdk.fetchDramaList((String[]) params.get("ids"), new Callback<List<Drama>>() {
                @Override
                public void result(Result<List<Drama>> result) {
                    showResult(item, GsonUtils.toJson(result));
                }
            });
        } else if ("lastDramas".equals(item.getMethod())) {
            JOWOSdk.lastDramas(result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchDramasByLabelKeyPerPage".equals(item.getMethod())) {
            JOWOSdk.fetchDramasByLabelKeyPerPage((String) params.get("labelKey"), (Integer) params.get("cursorId"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("lastDramasByLabelKey".equals(item.getMethod())) {
            JOWOSdk.lastDramasByLabelKey((String) params.get("labelKey"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchDramasByBlockKeyPerPage".equals(item.getMethod())) {
            JOWOSdk.fetchDramasByBlockKeyPerPage((String) params.get("blockKey"), (Integer) params.get("cursorId"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("lastDramasByBlockKey".equals(item.getMethod())) {
            JOWOSdk.lastDramasByBlockKey((String) params.get("blockKey"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchEpisodesGreaterNum".equals(item.getMethod())) {
            JOWOSdk.fetchEpisodesGreaterNum(String.valueOf( params.get("dramaId")), (Integer) params.get("greaterEpisodesNum"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("lastEpisodesByDramaId".equals(item.getMethod())) {
            JOWOSdk.lastEpisodesByDramaId(String.valueOf( params.get("dramaId")), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchEpisodesByNum".equals(item.getMethod())) {
            JOWOSdk.fetchEpisodesByNum(String.valueOf( params.get("dramaId")), (Integer) params.get("episodesNum"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchBlockDrama".equals(item.getMethod())) {
            JOWOSdk.fetchBlockDrama(result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("lastBlockDrama".equals(item.getMethod())) {
            JOWOSdk.lastBlockDrama(result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("postPlayEpisodes".equals(item.getMethod())) {
//            JOWOSdk.postPlayEpisodes(String.valueOf( params.get("dramaId")), (Integer) params.get("episodesNum"), (String) params.get("language"), result -> {
//                showResult(item, GsonUtils.toJson(result));
//            });
        } else if ("postDramaHistory".equals(item.getMethod())) {
            JOWOSdk.postDramaHistory(String.valueOf( params.get("dramaId")), (Integer) params.get("episodesNum"), (Long) params.get("progressTimeMillis"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("postDramaCollect".equals(item.getMethod())) {
            JOWOSdk.postDramaCollect(String.valueOf( params.get("dramaId")), (String) params.get("language"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchUserDramaCollectsPerPage".equals(item.getMethod())) {
            JOWOSdk.fetchUserDramaCollectsPerPage((Integer) params.get("cursorId"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("deleteDramaCollectCancel".equals(item.getMethod())) {
            JOWOSdk.deleteDramaCollectCancel(String.valueOf(params.get("dramaId")), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchUserDrama".equals(item.getMethod())) {
            JOWOSdk.fetchUserDrama(String.valueOf( params.get("dramaId")), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("lastUserDrama".equals(item.getMethod())) {
            JOWOSdk.lastUserDrama(String.valueOf( params.get("dramaId")), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchUserDramaHistoriesPerPage".equals(item.getMethod())) {
            JOWOSdk.fetchUserDramaHistoriesPerPage((Integer) params.get("cursorId"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("lastHistories".equals(item.getMethod())) {
            JOWOSdk.lastHistories(result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("lastHistoryByDramaId".equals(item.getMethod())) {
            JOWOSdk.lastHistoryByDramaId(String.valueOf( params.get("dramaId")), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchForU".equals(item.getMethod())) {
            JOWOSdk.fetchDramasForU(result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("searchDramas".equals(item.getMethod())) {
            JOWOSdk.searchDramas(new String[]{params.get("keywords").toString()}, (Integer) params.get("cursorId"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("fetchEpisodeShareUrl".equals(item.getMethod())) {
            JOWOSdk.fetchEpisodeShareUrl(String.valueOf( params.get("dramaId")), (Integer) params.get("episodesNum"), result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        } else if ("postFeedback".equals(item.getMethod())) {
            Feedback feedback = new Feedback();
            feedback.setTitle("测试标题");
            feedback.setContent("测试内容");
            feedback.setDramaId(String.valueOf( params.get("dramaId")));
            feedback.setEpisodesNum((Integer) params.get("episodesNum"));
            feedback.setFeedType(Feedback.TYPE_REPORT);
            feedback.setEmail("test@example.com");
            JOWOSdk.postFeedback(feedback, result -> {
                showResult(item, GsonUtils.toJson(result));
            });
        }
    }

    private void showResult(DataApiInfo item, String json) {
        KeyboardUtils.hideSoftInput(mBinding.et);
        String result = item.getMethodDesc() + "\n";
        result += "响应时间：" + (System.currentTimeMillis() - mStartAt) + "ms \n\n";
        result += json;

        mBinding.loading.setVisibility(View.GONE);
        mBinding.tv.setText(result);
    }
}
