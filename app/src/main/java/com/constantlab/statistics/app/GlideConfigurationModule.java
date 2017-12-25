package com.constantlab.statistics.app;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

@GlideModule
public class GlideConfigurationModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        RequestOptions requestOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888);
        //Log
        builder.setLogLevel(Log.VERBOSE);
        builder.setDefaultRequestOptions(requestOptions);


    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}