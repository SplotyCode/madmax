package de.hochtaunusschule.madmax;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;

/**
 * @author David (_Esel)
 */
@AllArgsConstructor
public class MadMax implements HexBinary {
    public static String solve(String input, int maxMoves) {
        int[] result = solve(HexBinary.extractHex(input), maxMoves);
        String asString = HexBinary.toHexString(result);
        System.out.println("Result for " + input + " is " + asString);
        return asString;
    }

    /* we can not for-fill overflow as the moves would not be enough  */
    private static boolean checkEnoughMovesForOverflow(int reserved, int moves) {
        return reserved < 0 && Math.abs(reserved) > moves;
    }

    /* can can't put more than we have space left */
    private static boolean checkSpaceForOverflow(int reserved, int bladesLeft, int length) {
        int maxBlades = length * 7;
        return reserved < 0 && Math.abs(reserved) > maxBlades - bladesLeft;
    }

    /* reserved more than we have blades left (on the right side) as we need at least 2 blades to construct a one */
    private static boolean checkReservedMoreThenOffer(int reserved, int bladesLeft, int length) {
        int minBlades = length * 2;
        return reserved < 0 && bladesLeft - Math.abs(reserved) < minBlades;
    }

    public static int[] solve(int[] input, int maxSteps) {
        if (maxSteps < 0) {
            throw new IllegalArgumentException("maxSteps might not be negative");
        }
        return solve(input, 0, input.length, 0, maxSteps, HexCache.totalLetterSize(input));
    }

    private static int[] solve(int[] input, int offset, int length, int reserved, int moves,
                               int bladesLeft) {
        if (moves < 0) { /* moves exhausted */
            return null;
        }
        if (input.length - offset == 0) { /* end of tree */
            /* check that we have no reserved blades left */
            return reserved == 0 ? input : null;
        }
        if (moves == 0) { /* we are not in the tail but have no moves left */
            /* if we have no reserved/left over blades we are fine */
            return reserved == 0 ? input : null;
        }
        if (checkEnoughMovesForOverflow(reserved, moves)
            || checkSpaceForOverflow(reserved, bladesLeft, length)
            || checkReservedMoreThenOffer(reserved, bladesLeft, length)) {
            return null;
        }
        int current = input[offset];
        int childRemaining = bladesLeft - HexCache.letterSize(current);
        for (int upgrade = DIGITS.length - 1; upgrade >= 0; upgrade--) { /* from f to 0 */
            HexMove.Move costs = HexCache.move(current, upgrade);
            int[] res = solve(
                input,
                offset + 1, length - 1, reserved + costs.fromOutside, moves - costs.moves,
                childRemaining
            );
            if (res != null) {
                res[offset] = upgrade;
                return res;
            }
        }
        return null;
    }

    private static final Logger LOGGER = Logger.getLogger(MadMax.class.getName());

    public static void main(String[] args) {
        if (args.length == 1) {
            File file = new File(args[0]);
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                args = new String[] {lines.get(0), lines.get(1)};
            } catch (IOException | IndexOutOfBoundsException e) {
                LOGGER.log(Level.SEVERE, "Es gab einen Fehler beim einlesen der input datei "
                    + file.getName() + ": " + e.getMessage(), e);
                return;
            }
        }
        if (args.length != 2) {
            System.out.println("Illegal usage: Use <hex number> <max moves>");
            return;
        }
        int maxMoves = Integer.parseInt(args[1]);
        int[] from = HexBinary.extractHex(args[0]);
        System.out.println("input: " + args[0] + " in <=" + maxMoves + " moves");
        long start = System.currentTimeMillis();
        int[] result = solve(from.clone(), maxMoves);
        String asString = HexBinary.toHexString(result);
        System.out.println("result is: " + asString);
        System.out.println("Took " + (System.currentTimeMillis() - start) + "ms");
        MoveTracer.displayPath(HexBinary.hexToBinary(from), HexBinary.hexToBinary(result.clone()));
    }
}
