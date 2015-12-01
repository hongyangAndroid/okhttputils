package com.zhy.http.okhttp;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * create InputStream RequestBody
 * Created by YUHONG on 2015/12/1.
 *
 * Email: hongyuahu@gmail.com
 */
public class RequestBodyUtils {

    public static RequestBody create(final MediaType mediaType, final InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {

                }
            }

            @Override
            public long contentLength() throws IOException {
                try {
                    return inputStream.available();
                } catch ( IOException e) {
                    return 0;
                }
            }
        };
    }
}
