package com.example.android.popularmovies2.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

public class LayoutUtils {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }
}
