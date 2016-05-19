package com.zhy.http.okhttp.callback;

import okhttp3.Response;

public abstract class BinaryCallback extends Callback<byte[]> {
    @Override
    public byte[] parseNetworkResponse(Response response) throws Exception {
        return response.body().bytes();
    }
}
