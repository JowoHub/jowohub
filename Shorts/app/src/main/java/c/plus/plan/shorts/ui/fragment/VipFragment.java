package c.plus.plan.shorts.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.google.common.collect.ImmutableList;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c.plus.plan.common.ui.viewmodel.UserViewModel;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ShortsApplication;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.databinding.FragmentVipBinding;
import c.plus.plan.shorts.entity.PurchaseProduct;
import c.plus.plan.shorts.entity.request.RequestPurchase;
import c.plus.plan.shorts.manager.ShortsDataBase;
import c.plus.plan.shorts.manager.VipManager;
import c.plus.plan.shorts.ui.adapter.PurchaseProductAdapter;
import c.plus.plan.shorts.ui.adapter.VipItemAdapter;
import c.plus.plan.shorts.ui.view.LoadingDialog;
import c.plus.plan.shorts.ui.viewmodel.VipViewModel;

public class VipFragment extends Fragment {
    private FragmentVipBinding mBinding;
    private VipItemAdapter mVipAdapter;
    private BillingClient billingClient;
    private final List<ProductDetails.SubscriptionOfferDetails> mVipList = new ArrayList<>();
    private List<ProductDetails> mProductDetailsList;
    private List<ProductDetails> mProductCoinList = new ArrayList<>();
    private OnClickListener onClickListener;
    private VipViewModel mViewModel;
    private PurchaseProductAdapter mPurchaseProductAdapter;
    private UserViewModel mUserViewModel;
    private List<Purchase> mConsumePurchases = new ArrayList<>();

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentVipBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VipViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initViews();
    }

    private void initViews() {
        mBinding.srl.autoRefresh();
        mBinding.srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryInappProduct();
            }
        });
        mVipAdapter = new VipItemAdapter(mVipList);
        mBinding.banner.addBannerLifecycleObserver(getViewLifecycleOwner())
                .addOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {


                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                })
                .setAdapter(mVipAdapter);
        mBinding.banner.setBannerGalleryEffect(8, 7, 1f);
        mVipAdapter.setOnItemClickListener(new VipItemAdapter.OnItemClickListener() {
            @Override
            public void click(int position, ProductDetails.SubscriptionOfferDetails item) {
                if (position < mProductDetailsList.size()) {
                    launchBillingFlow(item.getOfferToken(), mProductDetailsList.get(position));
                }
            }
        });

        billingClient = BillingClient.newBuilder(getContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
//                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
//                    queryVipInfo();
//                    queryVipProduct();
//                    queryInappProduct();
//                } else {
//                    // TODO
//                    showNoData();
//                }
                queryInappProduct();

            }

            @Override
            public void onBillingServiceDisconnected() {
                // TODO
                LogUtils.dTag("FWFW", "onBillingServiceDisconnected");
                showNoData();
            }
        });

        mPurchaseProductAdapter = new PurchaseProductAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvCoin.setLayoutManager(layoutManager);
        mBinding.rvCoin.setAdapter(mPurchaseProductAdapter);
        mPurchaseProductAdapter.setOnItemClickListener(new PurchaseProductAdapter.OnItemClickListener() {
            @Override
            public void click(int position, PurchaseProduct item) {
                if (position >= mProductCoinList.size()) {
                    ToastUtils.showLong("Service is not available, please wait.");
                } else {
                    ProductDetails productDetails = mProductCoinList.get(position);
                    launchBillingFlow(null, productDetails);
                }
            }
        });

        updateVip();
    }

    public void updateVip() {
        if (VipManager.isVip()) {
            mBinding.subHead.setVisibility(View.GONE);
            mBinding.banner.setVisibility(View.GONE);
        } else {
            mBinding.subHead.setVisibility(View.VISIBLE);
            mBinding.banner.setVisibility(View.VISIBLE);
        }

        // fake版本没有订阅
        mBinding.subHead.setVisibility(View.GONE);
        mBinding.banner.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mConsumePurchases.isEmpty()) {
            for (Purchase p : mConsumePurchases) {
                consumeInapp(p);
            }
            mConsumePurchases.clear();
        }
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {

        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (CollectionUtils.isNotEmpty(purchases)) {
                    mConsumePurchases.clear();
                    boolean isVip = VipManager.isVip();
                    boolean buyVip = false;
                    for (Purchase purchase : purchases) {
                        if (TextUtils.equals(purchase.getProducts().get(0), Constants.VIP_SUB_PRODUCT_ID)) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                VipManager.setVipTime(purchase.getPurchaseTime());
                            }

                            buyVip = true;
                            if (onClickListener != null && !isVip) {
                                onClickListener.vip(true);
                            }
                            LoadingDialog.dismissLoading();
                        } else {
                            mConsumePurchases.add(purchase);
                        }


                        if (!purchase.isAcknowledged()) {
                            AcknowledgePurchaseParams acknowledgePurchaseParams =
                                    AcknowledgePurchaseParams.newBuilder()
                                            .setPurchaseToken(purchase.getPurchaseToken())
                                            .build();
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams, null);

                        }
                    }

                    VipManager.setVip(buyVip);
                } else {
                    VipManager.setVip(false);
                    LoadingDialog.dismissLoading();
                }
            }
        }
    };

    private void queryVipInfo() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (CollectionUtils.isNotEmpty(purchases)) {
                                boolean isVip = VipManager.isVip();
                                VipManager.setVip(true);
                                for (Purchase purchase : purchases) {
                                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                        VipManager.setVipTime(purchase.getPurchaseTime());
                                    }
                                    if (!purchase.isAcknowledged()) {
                                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                                AcknowledgePurchaseParams.newBuilder()
                                                        .setPurchaseToken(purchase.getPurchaseToken())
                                                        .build();
                                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, null);
                                    }
                                }

                                if (onClickListener != null && !isVip) {
                                    onClickListener.vip(true);
                                }
                            } else {
                                VipManager.setVip(false);
                            }
                        }
                    }
                });

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (CollectionUtils.isNotEmpty(purchases)) {
                                for (Purchase purchase : purchases) {
                                    consumeInapp(purchase);
                                }
                            } else {

                            }
                        }
                    }
                });
    }

    private void consumeInapp(Purchase purchase) {
        RequestPurchase requestPurchase = new RequestPurchase();
        requestPurchase.setToken(purchase.getPurchaseToken());
        requestPurchase.setProductId(purchase.getProducts().get(0));
        requestPurchase.setPackageName(AppUtils.getAppPackageName());

        mViewModel.postPurchaseCheck(requestPurchase).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess() && result.getData() != null && result.getData().isSuccess()) {
                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.consumeAsync(consumeParams, null);
                mUserViewModel.requestMyUserInfo().observe(getViewLifecycleOwner(), result1 -> {
                    LoadingDialog.dismissLoading();
                    if (onClickListener != null) {
                        onClickListener.topUp();
                    }
                    if (isAdded()) {
                        showToast(R.string.buy_coin_suc);
                    }

                });
            } else {
                LoadingDialog.dismissLoading();
                ViewUtils.runOnUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            showToast(R.string.buy_coin_fail);
                        }
                    }
                }, 400);
            }
        });
    }

    private void queryVipProduct() {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(Constants.VIP_SUB_PRODUCT_ID)
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();
        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            mProductDetailsList = productDetailsList;
                            Map<String, ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetailsMap = new HashMap<>();
                            for (ProductDetails details : productDetailsList) {
//                                LogUtils.dTag("FWFW", "ProductDetails: ", details.getProductId(), details.getName(), details.getDescription());
                                for (ProductDetails.SubscriptionOfferDetails sub : details.getSubscriptionOfferDetails()) {
//                                    LogUtils.dTag("FWFW", "SubDetails: ", sub.getOfferId(), sub.getBasePlanId());
//                                    for (ProductDetails.PricingPhase pp: sub.getPricingPhases().getPricingPhaseList()) {
//                                        LogUtils.dTag("FWFW", "PricingPhase", pp.getFormattedPrice(), pp.getBillingPeriod(), pp.getRecurrenceMode(), pp.getBillingCycleCount(), pp.getPriceAmountMicros(), pp.getPriceCurrencyCode());
//                                    }
                                    if (!subscriptionOfferDetailsMap.containsKey(sub.getBasePlanId())) {
                                        subscriptionOfferDetailsMap.put(sub.getBasePlanId(), sub);
                                    }
                                }
                            }

                            ViewUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mVipList.clear();
                                    List<ProductDetails.SubscriptionOfferDetails> list = new ArrayList<>(subscriptionOfferDetailsMap.values());
                                    mVipList.addAll(list);
                                    if (mVipList.isEmpty()) {
                                        mBinding.subHead.setVisibility(View.GONE);
                                        mBinding.banner.setVisibility(View.GONE);
                                    } else {
                                        mBinding.subHead.setVisibility(View.VISIBLE);
                                        mBinding.banner.setVisibility(View.VISIBLE);
                                        mBinding.banner.getAdapter().setDatas(mVipList);
                                        mBinding.banner.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
        );
    }

    private void showNoData() {
        mBinding.noData.getRoot().setVisibility(View.VISIBLE);
        mBinding.ll.setVisibility(View.GONE);
        mBinding.srl.finishRefresh();
    }

    private void queryInappProduct() {
        if (!isAdded()) return;
        ViewUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewModel.getPurchaseProductList().observe(getViewLifecycleOwner(), result -> {
                    LogUtils.dTag("FWFW", "1111111111111");
                    if (CollectionUtils.isNotEmpty(result)) {
                        LogUtils.dTag("FWFW", "22222222222222");
                        queryInappProductPrice(result);
                    } else {
                        LogUtils.dTag("FWFW", "33333333333333333");
                        mViewModel.requestPurchaseProductList().observe(getViewLifecycleOwner(), result2 -> {
                            if (result2.isSuccess()) {
                                queryInappProductPrice(result2.getData());
                            } else {
                                showNoData();
                            }
                        });
                    }
                });
            }
        });

    }

    private void queryInappProductPrice(List<PurchaseProduct> purchaseProducts) {
        if (CollectionUtils.isEmpty(purchaseProducts)) return;
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        for (PurchaseProduct pp : purchaseProducts) {
            productList.add(
                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(pp.getProductId())
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
            );
        }

        vipProductDetails(purchaseProducts, new ArrayList<>());

// 这里是以前的逻辑
//        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
//                .setProductList(productList)
//                .build();
//        billingClient.queryProductDetailsAsync(
//                params,
//                new ProductDetailsResponseListener() {
//                    public void onProductDetailsResponse(BillingResult billingResult,
//                                                         List<ProductDetails> productDetailsList) {
//                        if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
//                            vipProductDetails(purchaseProducts, productDetailsList);
//                        }else{
//                            ViewUtils.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showNoData();
//                                }
//                            });
//                        }
//                    }
//                }
//        );
    }

    private void vipProductDetails(List<PurchaseProduct> purchaseProducts, List<ProductDetails> productDetailsList) {
        mBinding.srl.finishRefresh();
        mProductCoinList.clear();
        // 显示或使用这些商品信息
        for (PurchaseProduct pp : purchaseProducts) {
            for (ProductDetails productDetails : productDetailsList) {
                LogUtils.dTag("FWFW", pp.getProductId(), productDetails.getProductId());
                String productId = productDetails.getProductId();
//                                  String title = productDetails.getTitle();
//                                  String description = productDetails.getDescription();
                String price = productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
                if (TextUtils.equals(pp.getProductId(), productId)) {
                    mProductCoinList.add(productDetails);
                    pp.setPrice(price);
                    break;
                }
            }
        }

        LogUtils.dTag("FWFW", "445555555555555555555");


        ShortsDataBase.dbWriteExecutor.execute(() -> {
            ShortsDataBase.get().purchaseProductDao().update(purchaseProducts);
        });

        ViewUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPurchaseProductAdapter.setPurchaseProducts(purchaseProducts);
                mPurchaseProductAdapter.notifyDataSetChanged();
                mBinding.ll.setVisibility(View.VISIBLE);
            }
        });

    }

    private void launchBillingFlow(String token, ProductDetails productDetails) {
        LoadingDialog.showLoading(getContext(), true);
        BillingFlowParams.ProductDetailsParams params;
        if (token == null) {
            params = BillingFlowParams.ProductDetailsParams.newBuilder()
                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    .setProductDetails(productDetails)
                    .build();
        } else {
            params = BillingFlowParams.ProductDetailsParams.newBuilder()
                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    .setProductDetails(productDetails)
                    // For one-time products, "setOfferToken" method shouldn't be called.
                    // For subscriptions, to get an offer token, call
                    // ProductDetails.subscriptionOfferDetails() for a list of offers
                    // that are available to the user.
                    .setOfferToken(token)
                    .build();
        }
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        params
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(getActivity(), billingFlowParams);
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

        } else {
            showToast(c.plus.plan.common.R.string.network_not_good);
        }
    }

    public interface OnClickListener {
        void vip(boolean suc);

        void topUp();
    }

    public void showToast(int res) {
        showToast(getResources().getString(res));
    }

    public void showToast(String toast) {
        ToastUtils.make()
                .setBgResource(R.drawable.bg_toast)
                .setTextColor(getResources().getColor(R.color.white))
                .setTextSize(16)
                .setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2px(100))
                .show(toast);
    }
}
