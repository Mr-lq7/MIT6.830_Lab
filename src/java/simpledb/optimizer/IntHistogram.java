package simpledb.optimizer;

import simpledb.execution.Predicate;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */

    // add
    private int[] buckets;
    private int min;
    private int max;
    private double width;
    private int ntups;

    public IntHistogram(int buckets, int min, int max) {
    	// some code goes here

        this.buckets = new int[buckets];
        this.min = min;
        this.max = max;
        this.ntups = 0;
        this.width = Math.max(1, (1. + max - min) / this.buckets.length);

    }

    private int getIndex(int v) {
        return Math.min((int) ((v - min) / width), buckets.length - 1);
    }


    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	// some code goes here

        if (v < min || v > max) return;
        int index = getIndex(v);
        ntups ++;
        buckets[index] ++;
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {

    	// some code goes here

        int index = getIndex(v);
        if (op.equals(Predicate.Op.EQUALS)) {
            //(high / width) / ntups
            if (index < 0 || index >= buckets.length) return 0.0;
            double selectivity = (buckets[index] / width) / ntups;
            return selectivity;
        }
        if (op.equals(Predicate.Op.NOT_EQUALS)) {
            return 1 - estimateSelectivity(Predicate.Op.EQUALS, v);
        }
        if (op.equals(Predicate.Op.GREATER_THAN)) {
            if (v <= min) return 1.0;
            if (v >= max) return 0.0;

            int cnt = 0;
            for(int i = index + 1; i < buckets.length; i++) {
                cnt += buckets[i];
            }
            //b_f = h_b / ntups
            //b_part = (b_right - const) / w_b
            //b_selectivity = b_f x b_part
            double b_f = 1.0 * buckets[index] / ntups;
            double b_part = ((index + 1) * width - v) / width;
            double selectivity = b_f * b_part + 1.0 * cnt / ntups;
            return selectivity;
        }
        if (op.equals(Predicate.Op.GREATER_THAN_OR_EQ)) {
            if (v < min) return 1.0;
            if (v > max) return 0.0;
            return estimateSelectivity(Predicate.Op.GREATER_THAN, v - 1);
        }
        if (op.equals(Predicate.Op.LESS_THAN)) {
            if (v <= min) return 0.0;
            if (v >= max) return 1.0;
            return 1.0 - estimateSelectivity(Predicate.Op.EQUALS, v) - estimateSelectivity(Predicate.Op.GREATER_THAN, v);
        }
        if (op.equals(Predicate.Op.LESS_THAN_OR_EQ)) {
            if (v <= min) return 0.0;
            if (v >= max) return 1.0;
            return 1.0 - estimateSelectivity(Predicate.Op.GREATER_THAN, v);
        }

        return -1.0;
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
//        return 1.0;

        int sum = 0;
        for (int b : buckets) sum += b;
        return 1.0 * sum / ntups;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
        // some code goes here
        return null;
    }
}
