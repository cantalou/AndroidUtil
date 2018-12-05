package com.cantalou.android.manager.devicematch;

import android.os.Build;

import com.cantalou.android.util.StringUtils;

/**
 * Project Name: m4399_Forums<p>
 * File Name:    BuildPattern.java<p>
 * ClassName:    BuildPattern<p>
 *
 * Description: 手机Build匹配模式.
 *
 * @author LinZhiWei
 * @date 2016年02月24日 14:09
 *
 * Copyright (c) 2016年,  Network CO.ltd. All Rights Reserved.
 */
public class BuildPattern
{

    /**
     * 制造商
     */
    public String manufacturer;


    /**
     * 手机型号
     */
    public String model;


    /**
     * 系统版本号
     */
    public String release;

    /**
     * @param model 品牌
     */
    public BuildPattern(String model)
    {
        this(null, model, null);
    }

    /**
     * @param manufacturer 制造商
     * @param model        品牌
     */
    public BuildPattern(String manufacturer, String model)
    {
        this(manufacturer, model, null);
    }

    /**
     * @param manufacturer 制造商
     * @param model        品牌
     * @param release      android版本如: 4.4.4
     */
    public BuildPattern(String manufacturer, String model, String release)
    {
        if (StringUtils.isNotBlank(manufacturer))
        {
            this.manufacturer = manufacturer;
        }

        if (StringUtils.isNotBlank(model))
        {
            this.model = model;
        }

        if (StringUtils.isNotBlank(release))
        {
            this.release = release;
        }
    }

    public boolean match()
    {

        if (manufacturer != null && !(manufacturer.contains(Build.MANUFACTURER) || Build.MANUFACTURER.contains(manufacturer)))
        {
            return false;
        }

        if (model != null && !(model.contains(Build.MODEL) || Build.MODEL.contains(model)))
        {
            return false;
        }

        if (release != null && !(release.contains(Build.VERSION.RELEASE) || Build.VERSION.RELEASE.contains(release)))
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "BuildPattern{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", release='" + release + '\'' +
                '}';
    }
}
