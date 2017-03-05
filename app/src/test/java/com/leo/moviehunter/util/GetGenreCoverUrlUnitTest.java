package com.leo.moviehunter.util;

import android.content.Context;
import android.content.SharedPreferences;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class GetGenreCoverUrlUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Map map;

    @Mock
    SharedPreferences.Editor editor;

    @Mock
    SharedPreferences sharedPreferences;

    @Mock
    Context context;

    @Test
    public void getGenreCoverUrl() throws Exception {
        System.out.println("getGenreCoverUrl");

        map = Mockito.mock(Map.class);
        when(map.get(anyString())).thenReturn(null);

        editor = Mockito.mock(SharedPreferences.Editor.class);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        sharedPreferences = Mockito.mock(SharedPreferences.class);
        when(sharedPreferences.getAll()).thenReturn(map);
        when(sharedPreferences.edit()).thenReturn(editor);

        context = Mockito.mock(Context.class);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);

        new GetGenreCoverUrlTask(context, 53) {
            @Override
            public void onGetUrl(int genreId, String url) {
                System.out.println("genreId = " + genreId + ", url = " + url);
                Assert.assertNotNull(url);
            }
        }.execute();
    }
}


