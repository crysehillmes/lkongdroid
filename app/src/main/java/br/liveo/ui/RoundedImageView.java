/*
 * Copyright 2015 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.liveo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
    Path mCirclePath = new Path();
    int mPathWidth = 0;
    public RoundedImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        super.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        int w = getWidth();
        drawable.setBounds(0, 0, w, w);
        if(mPathWidth != w) {
            mCirclePath.reset();
            mCirclePath.addCircle(w / 2f, w / 2f, w / 2f, Path.Direction.CW);
            mPathWidth = w;
        }
        canvas.clipPath(mCirclePath, Region.Op.REPLACE);
        drawable.draw(canvas);
        canvas.save();
        canvas.restore();
    }

    @Override
    public void setLayerType(int layerType, Paint paint) {
        super.setLayerType(LAYER_TYPE_SOFTWARE, paint);
    }
}