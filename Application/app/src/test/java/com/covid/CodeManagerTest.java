package com.covid;

import com.covid.utils.CodeManager;

import org.junit.Test;

import static org.junit.Assert.*;

public class CodeManagerTest {

    @Test
    public void code_isGenerated(){
        CodeManager cm = new CodeManager();
        String codeString = cm.generateCode();

        assertNotNull(codeString);
    }

    @Test
    public void code_isLength(){
        CodeManager cm = new CodeManager();
        String codeString = cm.generateCode();

        assertEquals(20, codeString.length());

    }

    @Test
    public void code_isDifferent(){
        CodeManager cm = new CodeManager();
        String code1 = cm.generateCode();
        String code2 = cm.generateCode();

        assertNotEquals(code1, code2);
    }

    @Test
    public void code_structureCorrect(){
        CodeManager cm = new CodeManager();
        String code1 = cm.generateCode();

        assertEquals("4", code1);
    }



}
