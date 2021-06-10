package com.HBuilder.UniPlugin;

public class CompassView{

    private String msg  = "正北 0°";
    private float dec = 0.0f;

    public CompassView() {

    }


    // 更新指南针角度
    public void setDegree(float degree)
    {
        // 设置灵敏度
        if(Math.abs(dec - degree) >= 2 )
        {
            dec = degree;
            int range = 22;
            String degreeStr = String.valueOf(dec);

            // 指向正北
            if(dec > 360 - range && dec < 360 + range)
            {
                msg = "正北 " + degreeStr + "°";
            }

            // 指向正东
            if(dec > 90 - range && dec < 90 + range)
            {
                msg = "正东 " + degreeStr + "°";
            }

            // 指向正南
            if(dec > 180 - range && dec < 180 + range)
            {
                msg = "正南 " + degreeStr + "°";
            }

            // 指向正西
            if(dec > 270 - range && dec < 270 + range)
            {
                msg = "正西 " + degreeStr + "°";
            }

            // 指向东北
            if(dec > 45 - range && dec < 45 + range)
            {
                msg = "东北 " + degreeStr + "°";
            }

            // 指向东南
            if(dec > 135 - range && dec < 135 + range)
            {
                msg = "东南 " + degreeStr + "°";
            }

            // 指向西南
            if(dec > 225 - range && dec < 225 + range)
            {
                msg = "西南 " + degreeStr + "°";
            }

            // 指向西北
            if(dec > 315 - range && dec < 315 + range)
            {
                msg = "西北 " + degreeStr + "°";
            }
        }
    }
    public String getMsg(){
        return msg;
    }
}