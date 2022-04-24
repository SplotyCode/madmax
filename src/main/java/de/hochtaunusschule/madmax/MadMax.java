package de.hochtaunusschule.madmax;

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
        int maxBlades = length * 7;
        if (reserved < 0 && Math.abs(reserved) > maxBlades - bladesLeft) { /* put more than place */
            return null;
        }
        if (reserved < 0 && Math.abs(reserved) > moves) { /* we can not for-fill reserve as the moves would not be enough  */
            return null;
        }
        int minBlades = length * 2;
        if (bladesLeft < reserved || bladesLeft - minBlades < reserved) { /* reserve more than we have left */
            return null;
        }
        if (reserved < 0 && bladesLeft - Math.abs(reserved) < minBlades) { /* when serving reserves we could not  */
            return null;
        }
        int current = input[offset];
        int childRemaining = bladesLeft - HexCache.letterSize(current);
        for (int upgrade = DIGITS.length - 1; upgrade >= 0; upgrade--) {
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

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Illegal usage: Use <hex number> <max moves>");
            return;
        }
        long start = System.currentTimeMillis();
        solve(args[0], Integer.parseInt(args[1]));
        System.out.println((System.currentTimeMillis() - start));

    }
}
