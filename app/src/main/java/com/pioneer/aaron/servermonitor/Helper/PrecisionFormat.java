package com.pioneer.aaron.servermonitor.Helper;

import java.text.DecimalFormat;

/**
 * Created by Aaron on 6/15/15.
 */
public class PrecisionFormat extends DecimalFormat{
    DecimalFormat decimalFormat;

    public static PrecisionFormat newInstance() {
        return new PrecisionFormat();
    }

    public String shrink(float num, int length) {
        switch (length) {
            case 2:
                decimalFormat = new DecimalFormat("##0.00");
                return decimalFormat.format(num);
        }

        return "";
    }
}
