package com.victor.utilities.utils;

import com.victor.utilities.visual.VisualAssist;
import org.junit.Test;

import java.io.IOException;

/**
 * unit test for DateHelper
 */
public class OsHelperTest {

    @Test
    public void osTest() throws IOException {
        VisualAssist.print("os: ", OsHelper.isLinux());
    }


}
