package de.hochtaunusschule.madmax;

/**
 * @author David (_Esel)
 */
public class MoveTracer {
    public static void displayPath(int[] from, int[] to) {
        int[] display = from.clone();
        System.out.println("Input: ");
        HexBinary.print(display, System.out);
        int move = 0;
        while (nextMove(from, to, display)) {
            System.out.println("Move #" + ++move);
            HexBinary.print(display, System.out);
        }
    }

    private static boolean nextMove(int[] from, int[] to, int[] display) {
        boolean remove = findRemove(from, to, display);
        boolean add = findAdd(from, to, display);
        if (remove != add) {
            throw new IllegalArgumentException("unmatch");
        }
        return remove;
    }

    private static boolean findRemove(int[] from, int[] to, int[] display) {
        for (int i = 0; i < from.length; i++) {
            for (int bit : HexBinary.BITS) {
                if (HexBinary.state(bit, from[i]) && !HexBinary.state(bit, to[i])) {
                    from[i] &= ~bit;
                    display[i] &= ~bit;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean findAdd(int[] from, int[] to, int[] display) {
        for (int i = 0; i < from.length; i++) {
            for (int bit : HexBinary.BITS) {
                if (!HexBinary.state(bit, from[i]) && HexBinary.state(bit, to[i])) {
                    to[i] &= ~bit;
                    display[i] |= bit;
                    return true;
                }
            }
        }
        return false;
    }
}
