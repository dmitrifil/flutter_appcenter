package com.microsoft.flutterappcenter;

import android.app.Activity;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.distribute.Distribute;
import com.microsoft.appcenter.distribute.UpdateTrack;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class FlutterAppcenterPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {
    private Activity activity;
    private Result result;

    private String updateDialogTitle;
    private String updateDialogSubTitle;
    private String updateDialogDetail;
    private String updateDialogConfirm;
    private String updateDialogCancel;

    public FlutterAppcenterPlugin() {
        this.activity = null;
        this.updateDialogTitle = "Update";
        this.updateDialogSubTitle = "";
        this.updateDialogDetail = "";
        this.updateDialogConfirm = "Confirm";
        this.updateDialogCancel = "Cancel";
    }

    public FlutterAppcenterPlugin(Activity activity) {
        this.activity = activity;
        this.updateDialogTitle = "Update";
        this.updateDialogSubTitle = "";
        this.updateDialogDetail = "";
        this.updateDialogConfirm = "Confirm";
        this.updateDialogCancel = "Cancel";
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), "flutter_appcenter");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }
    @Override
    public void onReattachedToActivityForConfigChanges(
            ActivityPluginBinding binding
    ) {
        activity = binding.getActivity();
    }
    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

    @Override
    public void onMethodCall(final MethodCall call, final Result result) {
        this.result = result;
        if(call.method.equals("initAppCenter")) {
            if(call.hasArgument("usePrivateTrack")) {
                String usePrivateTrackStr = call.argument("usePrivateTrack").toString();
                boolean usePrivateTrack = Boolean.parseBoolean(usePrivateTrackStr);

                if(usePrivateTrack){
                    Distribute.setUpdateTrack(UpdateTrack.PRIVATE);
                }
            }

            if(call.hasArgument("automaticCheckForUpdate")) {
                String automaticCheckForUpdateStr = call.argument("automaticCheckForUpdate").toString();
                boolean automaticCheckForUpdate = Boolean.parseBoolean(automaticCheckForUpdateStr);

                if(!automaticCheckForUpdate){
                    Distribute.disableAutomaticCheckForUpdate();
                }
            }

            if(call.hasArgument("updateDialogTitle")){
                this.updateDialogTitle = call.argument("updateDialogTitle").toString();
            }

            if(call.hasArgument("updateDialogSubTitle")){
                this.updateDialogSubTitle = call.argument("updateDialogSubTitle").toString();
            }

            if(call.hasArgument("updateDialogDetail")){
                this.updateDialogDetail = call.argument("updateDialogDetail").toString();
            }

            if(call.hasArgument("updateDialogConfirm")){
                this.updateDialogConfirm = call.argument("updateDialogConfirm").toString();
            }

            if(call.hasArgument("updateDialogCancel")){
                this.updateDialogCancel = call.argument("updateDialogCancel").toString();
            }

            if(call.hasArgument("appSecret")) {
                if (activity != null) {
                    String appSecret = call.argument("appSecret").toString();

                    AppCenter.start(activity.getApplication(), appSecret, Analytics.class, Crashes.class, Distribute.class);
                    result.success("1");
                } else {
                    result.success('0');
                }
            }else {
                result.success("0");
            }
        }else if(call.method.equals("checkForUpdate")){
            Distribute.checkForUpdate();
        }else if(call.method.equals("isEnabledForDistribute")){
            boolean isEnabled = Distribute.isEnabled().get();

            String res = isEnabled ? "1" : "0";
            result.success(res);
        }else{
            result.error("UNAVAILABLE", "it can not implement initAppCenter method", null);
        }

    }

}