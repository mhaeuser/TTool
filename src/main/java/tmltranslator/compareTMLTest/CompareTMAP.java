package tmltranslator.compareTMLTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class CompareTMAP {

    public CompareTMAP () {

    }

    public boolean CompareTMAPFiles(File expected, File clone) throws Exception {

        BufferedReader expectedReader = new BufferedReader(new FileReader(expected));
        BufferedReader cloneReader = new BufferedReader(new FileReader(clone));

        String s1;
        String s2;
        List<String> expectedStringArray = new ArrayList<String>();
        List<String> cloneStringArray = new ArrayList<String>();
        
        while ((s1 = expectedReader.readLine()) != null) {
            if (s1.indexOf("//") >= 0) {
                s1 = s1.substring(0, s1.indexOf("//"));
            }
            if (!s1.contains("#include") && s1.length() > 0) {
                s1 = s1.trim();
                expectedStringArray.add(s1);
            }
        }

        while ((s2 = cloneReader.readLine()) != null){
            if (s2.indexOf("//") >= 0) {
                s2 = s2.substring(0, s2.indexOf("//"));
            }
            if (!s2.contains("#include") && s2.length() > 0) {
                s2 = s2.trim();
                cloneStringArray.add(s2);
            }
        }
        expectedReader.close();
        cloneReader.close();
        
        return checkEquality(expectedStringArray, cloneStringArray);
    }

    public boolean checkEquality(List<String> s1, List<String> s2) {
        for (String s : s1) {
            if (s2.contains(s)) {
                s2.remove(s);
            }
        }

        if (s2.size() == 0) {
            return true;
        }

        return false;
    }
}
