package it.lorenzo.pokemon.utils;

import java.util.Comparator;

public class NaturalOrderComparator implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {
        if (s1 == null) return s2 == null ? 0 : -1;
        if (s2 == null) return 1;

        String[] part1 = splitParts(s1);
        String[] part2 = splitParts(s2);

        int length = Math.min(part1.length, part2.length);
        for (int i = 0; i < length; i++) {
            String p1 = part1[i];
            String p2 = part2[i];

            if (p1.matches("\\d+") && p2.matches("\\d+")) {
                int n1 = Integer.parseInt(p1);
                int n2 = Integer.parseInt(p2);
                if (n1 != n2) return Integer.compare(n1, n2);
            } else {
                int res = p1.compareTo(p2);
                if (res != 0) return res;
            }
        }
        return Integer.compare(part1.length, part2.length);
    }

    private String[] splitParts(String s) {
        return s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    }
}