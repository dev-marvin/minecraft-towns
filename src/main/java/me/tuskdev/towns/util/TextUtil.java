package me.tuskdev.towns.util;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    public static List<String> getPage(List<String> lines, int pageHumanBased, String title, int pageHeight) {
        // Create Ret
        List<String> ret = new ArrayList<>();
        int pageCount = (int)Math.ceil(((double)lines.size()) / pageHeight);

        // Add Title
        String titleLine = title.replace("{page}", pageHumanBased + "").replace("{total}", pageCount + "");
        ret.add(titleLine);

        // Check empty and invalid
        if (pageCount == 0) {
            ret.add("§cNo logs found.");
            return ret;
        }

        else if (pageHumanBased > pageCount) {
            ret.add("§cInvalid page.");
            return ret;
        }

        // Get Lines
        int from = (pageHumanBased - 1) * pageHeight;
        int to = from + pageHeight;
        if (to > lines.size()) to = lines.size();

        // Check object type and add lines
        ret.addAll(lines.subList(from, to));

        // Return Ret
        return ret;
    }

}
