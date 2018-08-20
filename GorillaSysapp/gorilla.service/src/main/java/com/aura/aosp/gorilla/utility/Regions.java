package com.aura.aosp.gorilla.utility;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Regions
{
    //
    // Temporary hardwired package
    // for cloud basics.
    //

    //
    // us = United States
    // ca = Canada
    // eu = Europe
    // ap = Asia / Pacific
    // sa = South America
    //
    // af = africa
    // ru = russia
    //

    private static String[] auraRegions = createAuraRegions();

    private static String[] createAuraRegions()
    {
        return new String[]{

                "aura-us-central-1", // Virtual USA maps to USA East

                "aura-us-east-1",
                "aura-us-east-2",
                "aura-us-west-1",
                "aura-us-west-2",

                "aura-ca-central-1",

                "aura-eu-central-1",
                "aura-eu-west-1",
                "aura-eu-west-2",
                "aura-eu-west-3",

                "aura-ap-northeast-1",
                "aura-ap-northeast-2",
                "aura-ap-northeast-3",
                "aura-ap-southeast-1",
                "aura-ap-southeast-2",
                "aura-ap-south-1",

                "aura-sa-east-1",

                "aura-af-central-1", // Virtual Africa maps to Europe
                "aura-ru-central-1", // Virtual Russia maps to Europe
        };
    }

    //
    // Amazon Web Services
    //

    private static Map<String, String> auraToAWSRegions = createAuraToAWSRegions();

    private static Map<String, String> createAuraToAWSRegions()
    {
        Map<String, String> initmap = new HashMap<>();

        initmap.put("aura-us-central-1", "us-east-1");

        initmap.put("aura-us-east-1", "us-east-1");
        initmap.put("aura-us-east-2", "us-east-2");
        initmap.put("aura-us-west-1", "us-west-1");
        initmap.put("aura-us-west-2", "us-west-2");

        initmap.put("aura-ca-central-1", "ca-central-1");

        initmap.put("aura-eu-central-1", "eu-central-1");
        initmap.put("aura-eu-west-1", "eu-west-1");
        initmap.put("aura-eu-west-2", "eu-west-2");
        initmap.put("aura-eu-west-3", "eu-west-3");

        initmap.put("aura-ap-northeast-1", "ap-northeast-1");
        initmap.put("aura-ap-northeast-2", "ap-northeast-2");
        initmap.put("aura-ap-northeast-3", "ap-northeast-3");
        initmap.put("aura-ap-southeast-1", "ap-southeast-1");
        initmap.put("aura-ap-southeast-2", "ap-southeast-2");
        initmap.put("aura-ap-south-1", "ap-south-1");

        initmap.put("aura-sa-east-1", "sa-east-1");

        initmap.put("aura-af-central-1", "eu-central-1");
        initmap.put("aura-ru-central-1", "eu-central-1");

        return initmap;
    }

    //
    // Rolled out countries.
    //

    private static Map<String, String> countryToAuraRegion = createCountryToAuraRegion();

    private static Map<String, String> createCountryToAuraRegion()
    {
        Map<String, String> initmap = new HashMap<>();

        initmap.put("US", "aura-us-central-1");
        initmap.put("DE", "aura-eu-central-1");
        initmap.put("VN", "aura-ap-northeast-2");

        return initmap;
    }

    @Nullable
    public static String CountryToRegion(String country)
    {
        return countryToAuraRegion.get(country);
    }

    @Nullable
    public static String MapToAWS(String auraregion)
    {
        return auraToAWSRegions.get(auraregion);
    }
}
