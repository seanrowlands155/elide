package com.yahoo.elide.core.exceptions.handlers;

import com.yahoo.elide.core.exceptions.InvalidAttributeException;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ErrorMapperTest {

    @Spy
    private ErrorMapper testErrorMapper = new ErrorMapper();

    @Mock
    private InvalidAttributeException invalidAttributeException;

    @Test
    public void testAddErrorMapperEntry(){

        testErrorMapper.addErrorMapperEntry(InvalidAttributeException.class,(b)->{
            System.out.println("called");
            System.out.println(b.getStatus());
            return String.valueOf(b.getStatus());
        });

        String result = testErrorMapper.getHandleErrorFunctionForError(InvalidAttributeException.class).apply(invalidAttributeException);
        System.out.println(result);
        //assertTrue(testErrorMapper.getHandleErrorFunctionForError(InvalidAttributeException.class) != null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateAddErrorMapperEntry(){

        testErrorMapper.addErrorMapperEntry(InvalidAttributeException.class,(b)->{
            System.out.println("called");
            System.out.println(b.getStatus());
            return b.getVerboseMessage();
        });

        testErrorMapper.addErrorMapperEntry(InvalidAttributeException.class,(b)->{
            System.out.println("called");
            System.out.println(b.getStatus());
            return b.getVerboseMessage();
        });
    }

    @Test
    public void testGetHandleErrorFunctionForError(){
    }

}