package com.leo.moviehunter.tmdb;

import com.leo.moviehunter.tmdb.service.TMDBConfiguration;

import junit.framework.Assert;

import org.junit.Test;

public class TMDBConfigurationUnitTest {

    @Test
    public void getImageBaseUrl() throws Exception {
        System.out.println("getImageBaseUrl");

        String imageBaseUrl = TMDBConfiguration.getImageBaseUrl();

        System.out.println("imageBaseUrl = " + imageBaseUrl);
        // http://image.tmdb.org/t/p/w300/

        Assert.assertNotNull(imageBaseUrl);
    }
}


