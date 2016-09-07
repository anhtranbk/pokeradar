package io.tjeubaoit.pokeradar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class BitmapUtils {

    private static LruCache<Integer, Bitmap> cache = new LruCache<Integer, Bitmap>(16 * 1024 * 1024) {
        @Override
        protected int sizeOf(Integer key, Bitmap value) {
            return value.getByteCount();
        }
    };

    public static Bitmap getScaledBitmap(Context context, int resId, float scale) {
        Bitmap dst = cache.get(resId);
        if (dst == null) {
            Bitmap src = BitmapFactory.decodeResource(context.getResources(), resId);
            int dstWidth = (int) (scale * src.getWidth());
            int dstHeight = (int) (scale * src.getHeight());
            dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
            cache.put(resId, dst);
        }
        return dst;
    }
}
