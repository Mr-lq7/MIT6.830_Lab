package simpledb.execution;

import simpledb.common.DbException;
import simpledb.common.Type;
import simpledb.storage.*;
import simpledb.execution.Aggregator;
import simpledb.transaction.TransactionAbortedException;

import java.util.*;


/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    private final int gbfield;
    private final Type gbfieldtype;
    private final int afield;
    private final Op what;
    private Object aggr;

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here

        if (what != Op.COUNT) {
            throw new IllegalArgumentException("StringAggregator only support COUNT");
        }
        this.what = what;
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;

        if (this.gbfield == Aggregator.NO_GROUPING) {
            aggr = (Object) new Integer(0);
        } else {
            assert gbfieldtype != null;
            if (this.gbfieldtype == Type.INT_TYPE) {
                aggr = (Object) new TreeMap<Integer, ArrayList<Integer>>();
            } else {
                aggr = (Object) new TreeMap<String, ArrayList<String>>();
            }
        }
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here

        if (this.gbfield == Aggregator.NO_GROUPING) {
            aggr = (((Integer) aggr) + 1);
        } else {
            if (this.gbfieldtype == Type.INT_TYPE) {
                TreeMap<Integer, Integer> groupAggr = (TreeMap<Integer, Integer>) aggr;
                Integer gbKey = ((IntField) tup.getField(gbfield)).getValue();
                if (!groupAggr.containsKey(gbKey)) {
                    groupAggr.put(gbKey, 1);
                } else {
                    groupAggr.replace(gbKey, groupAggr.get(gbKey) + 1);
                }
            } else {
                TreeMap<String, Integer> groupAggr = (TreeMap<String, Integer>) aggr;
                String gbKey = ((StringField) tup.getField(gbfield)).getValue();
                if (!groupAggr.containsKey(gbKey)) {
                    groupAggr.put(gbKey, 1);
                } else {
                    groupAggr.replace(gbKey, groupAggr.get(gbKey) + 1);
                }
            }
        }

    }

    /**
     * Create a OpIterator over group aggregate results.
     *
     * @return a OpIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public OpIterator iterator() {
        // some code goes here
//        throw new UnsupportedOperationException("please implement me for lab2");
        return new StringAggrOpIterator();
    }



    private class StringAggrOpIterator implements OpIterator {
        private ArrayList<Tuple> res;
        private Iterator<Tuple> it;


        public StringAggrOpIterator() {
            assert what == Op.COUNT;
            this.it = null;
            res = new ArrayList<Tuple>();

            if (gbfield == Aggregator.NO_GROUPING) {
                Tuple t = new Tuple(getTupleDesc());
                Field aggregateVal = new IntField((Integer) aggr);
                t.setField(0, aggregateVal);
                res.add(t);
            } else {
                for (Map.Entry e : ((TreeMap<Integer, ArrayList<Integer>>) aggr).entrySet()) {
                    Tuple t = new Tuple(getTupleDesc());
                    Field groupVal = null;
                    if (gbfieldtype == Type.INT_TYPE) {
                        groupVal = new IntField((int) e.getKey());
                    } else {
                        String str = (String) e.getKey();
                        groupVal = new StringField(str, str.length());
                    }
                    Field aggregateVal = new IntField((Integer) e.getValue());
                    t.setField(0, groupVal);
                    t.setField(1, aggregateVal);
                    res.add(t);
                }
            }
        }

        @Override
        public void open() throws DbException, TransactionAbortedException {
            it = res.iterator();
        }

        @Override
        public boolean hasNext() throws DbException, TransactionAbortedException {
            if (it == null) {
                throw new IllegalArgumentException("IntegerAggregator not open");
            }
            return it.hasNext();
        }

        @Override
        public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
            if (it == null) {
                throw new IllegalArgumentException("IntegerAggregator not open");
            }
            return it.next();
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException {
            if (it == null) {
                throw new IllegalArgumentException("IntegerAggregator not open");
            }
            it = res.iterator();
        }

        @Override
        public TupleDesc getTupleDesc() {
            if (gbfield == Aggregator.NO_GROUPING) {
                return new TupleDesc(new Type[]{Type.INT_TYPE});
            } else {
                return new TupleDesc(new Type[]{gbfieldtype, Type.INT_TYPE});
            }
        }

        @Override
        public void close() {
            it = null;
        }
    }


}
