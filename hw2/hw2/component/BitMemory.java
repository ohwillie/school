// $Id: BitMemory.java,v 1.3 2010/01/27 02:25:00 zahorjan Exp $

package component;

/** Generic implementation of a bit addressable memory.
    <p>
    Note:  it would be a mistake (in many ways) to try to read
    or write fields wider than 32 bits in a single operation.
    <p>
    All read operations return 0 bits if a selected region is outside
    the BitMemory.  Write operations are no-ops in those situations.
    (For both, this is on a bit-by-bit basis.)
*/

public class BitMemory {

    private int    memSizeInBits;  // measured in bits
    private int[]  bits;

    /** Construct a bit memory of size bits.
     */
    public BitMemory(int size) {
        memSizeInBits = size;
        bits = new int[(size+31)/32];
    }

    /* Write the len low order bits of val into this BitMemory
       starting at bit start.  The higest order bit of those
       selected from val is put at start, the next higest at start+1,
       etc.
       <p>
       len must be in the range 0..32 for correct operation.
    */
    public void write( int val, int start, int len ) {
        int valMask = 1 << (len-1);
        for ( int pos=start; pos<start+len; pos++ ) {
            if ( pos >= memSizeInBits ) break;
            int index = pos / 32;
            int offset = 31 - (pos - index*32);
            // turn off whatever bit is there now
            int bitMask = 1 << offset;
            bits[index] = bits[index] & ~bitMask;
            if ( (val & valMask) != 0 ) {
                bits[index] = bits[index] | bitMask;
            }
            valMask >>= 1; // seems to be algebraic, which doesn't work if len=32
            valMask &= 0x7fffffff;
        }
    }

    /** Copy all the bits of the data BitMemory into
        this one, starting at offset start.
    */
    public void write( BitMemory data, int start ) {
        for ( int pos=0; pos<data.getSize(); pos++ ) {
            int val = data.read(pos, 1);
            write( val, start+pos, 1 );
        }
    }

    /** The memory bits at positions start,...,start+len-1
        are put into the low order bits of the result.
        The bit at start becomes the MSB of those low-order
        len bits in the result.
        <p>
        If a bit position is out of range for this BitMemory,
        a 0 bit is substituted into the result.
        <p>
        len must be in the range 0..32 for correct operation.
    */
    public int read( int start, int len ) {
        int result = 0;
        for ( int pos=start; pos<start+len; pos++ ) {
            result <<= 1;
            if ( pos >= memSizeInBits ) continue;
            int index = pos / 32;
            int offset = 31 - (pos - index*32);
            int mask = 1 << offset;
            if ( (bits[index] & (1 << offset)) != 0 ) {
                result |= 1;
            }
        }
        return result;
    }

    /** Returns the size of this BitMemory, measured in bits.
     */
    public int getSize() {
        return memSizeInBits;
    }

    /** Verbose version of what an overridden toString() usually does.
     */
    public String dump() {
        String result;
        result = "Using " + bits.length + " ints to store " + getSize() + " bits.\n";
        for ( int i=0; i<bits.length; i++ ) {
            result += "\t" + Integer.toHexString(bits[i]) + "\n";
        }
        return result;
    }

    /** Needed for BitMemory's to be insertable into a HashSet.
     */
    public int hashCode() {
        int result = 0;
        for (int i=0; i<bits.length; i++ ) {
            result ^= bits[i];
        }
        return result;
    }

    /** Needed for BitMemory's to be insertable into a HashSet.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if ( o == null || getClass() != o.getClass()) {
            return false; 
        }

        BitMemory other = (BitMemory)o;
        if ( other.getSize() != getSize() ) {
            return false;
        }

        for (int pos=0; pos<getSize(); pos++ ) {
            if ( read(pos,1) != other.read(pos, 1) ) {
                return false;
            }
        }

        return true;
    }
        
    /** Test code/driver for this class; used for debugging BitMemory implementation only.
     */
    public static void main(String[] args) {

        int pos;
        int len;

        int size = 0;
        for (int i = 1; i<Integer.MAX_VALUE/2; i *= 2 ) {
            size += (int)Math.ceil(Math.log(i)/Math.log(2)) + 1;
            
        }

        BitMemory d = new BitMemory(size);
        System.out.println( "BitMemory size = " + size + " bits");

        pos = 0;
        for (int i = 1; i<Integer.MAX_VALUE/2; i *= 2 ) {
            len = (int)Math.ceil(Math.log(i)/Math.log(2)) + 1;
            System.out.println("write(" + i + ", " + pos + ", " + len + ")");
            d.write(i, pos, len);
            pos += len;
        }

        pos = 0;
        for (int i = 1; i<Integer.MAX_VALUE/2; i *= 2 ) {
            len = (int)Math.ceil(Math.log(i)/Math.log(2)) + 1;
            System.out.print("read(" + pos + ", " + len + "): ");
            int result = d.read(pos, len);
            System.out.println(result);
            pos += len;
        }

        int i = 0x70707070;
        d.write(i, 0, 32);
        int result = d.read(0, 32);
        System.out.println(Integer.toHexString(i) + " ==? " +
                           Integer.toHexString(result) );


        i = 0;
        System.out.println( (1 << i) );
        i = 31;
        System.out.println( Integer.toHexString(1 << i) );
    }
}
