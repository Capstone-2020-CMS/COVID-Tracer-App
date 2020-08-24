package com.covid;

import org.junit.Test;

import static org.junit.Assert.*;

public class IDCodeTest {
    @Test
    public void code_isSize(){
        //get a code of the correct size
        assertEquals(16, appCode.getLength());
    }

    @Test
    public void code_isSalted(){
        //get code
        //get salt
        //de-hash code using salt
    }
}
