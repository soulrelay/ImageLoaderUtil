package com.baofeng.soulrelay.utils.imageloader.glideprogress;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

/**
 * modified by soulrelay
 */
public class ProgressModelLoader implements StreamModelLoader<String> {

    private final ModelCache<String, String> modelCache;
    private ProgressUIListener proListener;

    public ProgressModelLoader(ProgressUIListener listener) {
        this(null, listener);
    }

    public ProgressModelLoader(ModelCache<String, String> modelCache) {
        this(modelCache, null);
    }


    public ProgressModelLoader(ModelCache<String, String> modelCache, ProgressUIListener listener) {
        this.modelCache = modelCache;
        this.proListener = listener;
    }


    @Override
    public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
        String result = null;
        if (modelCache != null) {
            result = modelCache.get(model, width, height);
        }
        if (result == null) {
            result = model;
            if (modelCache != null) {
                modelCache.put(model, width, height, result);
            }
        }
        return new ProgressDataFetcher(result, proListener);
    }

    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        private final ModelCache<String, String> mModelCache = new ModelCache<>(500);

        @Override
        public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new ProgressModelLoader(mModelCache);
        }

        @Override
        public void teardown() {

        }
    }

}
