package com.yahoo.elide;

import static org.mockito.Mockito.spy;

import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.core.exceptions.handlers.ErrorMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ElideSettingsBuilderTest {

    @Mock
    private DataStore mockDataStore;

    @Mock
    private ErrorMapper errorMapper;

    @Spy
    private ElideSettingsBuilder builder = new ElideSettingsBuilder(mockDataStore);

    @Test
    public void testBuild() {
        builder.build();
        spy(builder).build();
    }

    @Test
    public void testWithErrorMapper()
    {

//        builder.withErrorMapper();
    }


}