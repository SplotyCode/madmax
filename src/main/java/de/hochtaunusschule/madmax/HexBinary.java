package de.hochtaunusschule.madmax;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author David (_Esel)
 */
public interface HexBinary extends HexDisplay{
    int[] BITS = new int[] {UP_LEFT, UP_MID, UP_RIGHT, CENTER, DOWN_LEFT, DOWN_MID, DOWN_RIGHT};

    static boolean state(int bit, int number) {
        return (number & bit) != 0;
    }

    static String stateChar(int bit, String ch, int number) {
        return state(bit, number) ? ch : ch.replaceAll(".", " ");
    }

    static int difference(int left, int right) {
        int difference = 0;
        for (int bit : BITS) {
            if (state(bit, left) == state(bit, right)) {
                difference++;
            }
        }
        return difference;
    }

    static int letterSize(int number) {
        int size = 0;
        for (int bit : BITS) {
            if (state(bit, number)) {
                size++;
            }
        }
        return size;
    }

    static int[] hexToBinary(int[] hex) {
        int[] binary = hex.clone();
        for (int i = 0; i < hex.length; i++) {
            binary[i] = HexDisplay.DIGITS[binary[i]];
        }
        return binary;
    }

    static int[] extractHex(String hex) {
        return hex
            .chars()
            .map(operand -> Integer.parseInt(String.valueOf((char) operand), 16))
            .toArray();
    }

    static String toHexString(int[] array) {
        return Arrays.stream(array)
            .mapToObj(Integer::toHexString)
            .collect(Collectors.joining());
    }

    static void print(int[] number, PrintStream stream) {
        String[] lines = new String[] {"", "", "", "", ""};
        for (int digit : number) {
            print(digit, lines);
        }
        for (String line : lines) {
            stream.println(line);
        }
    }

    static String anyStateChar(String ch, int number, int... bits) {
        String last = null;
        for (int bit : bits) {
            last = stateChar(bit, ch, number);
            if (!last.trim().isEmpty()) {
                return last;
            }
        }
        return last;
    }

    static void print(int number, String[] lines) {
        lines[0] += stateChar(UP_LEFT, "#", number) + stateChar(UP_MID, "###", number) + stateChar(UP_RIGHT, "#", number) + "  ";
        lines[1] += stateChar(UP_LEFT, "#", number) + "   " + stateChar(UP_RIGHT, "#", number) + "  ";
        lines[2] += anyStateChar("#", number, UP_LEFT, DOWN_LEFT) + stateChar(CENTER, "###", number) + anyStateChar("#", number, UP_RIGHT, DOWN_RIGHT) + "  ";
        lines[3] += stateChar(DOWN_LEFT, "#", number) + "   " + stateChar(DOWN_RIGHT, "#", number) + "  ";
        lines[4] += stateChar(DOWN_LEFT, "#", number) + stateChar(DOWN_MID, "###", number) + stateChar(DOWN_RIGHT, "#", number) + "  ";
    }
}
