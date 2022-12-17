package simpledb.execution;

import simpledb.transaction.TransactionAbortedException;
import simpledb.common.DbException;
import simpledb.storage.Tuple;
import simpledb.storage.TupleDesc;

import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor. Accepts two children to join and the predicate to join them
     * on
     *
     * @param p
     *            The predicate to use to join the children
     * @param child1
     *            Iterator for the left(outer) relation to join
     * @param child2
     *            Iterator for the right(inner) relation to join
     */

//    private final JoinPredicate p;
//    private OpIterator child1;
//    private OpIterator child2;

    // add code
    private final JoinPredicate joinPredicate;
    private OpIterator child1;
    private OpIterator child2;
    private Tuple t;
    // 自然连接,无去重
    public Join(JoinPredicate p, OpIterator child1, OpIterator child2) {
        // some code goes here

        this.joinPredicate = p;
        this.child1 = child1;
        this.child2 = child2;
    }

    public JoinPredicate getJoinPredicate() {
        // some code goes here
//        return null;
//        return p;
        return joinPredicate;
    }

    /**
     * @return
     *       the field name of join field1. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField1Name() {
        // some code goes here
//        return null;
//        return child1.getTupleDesc().getFieldName(p.getField1());
        return child1.getTupleDesc().getFieldName(joinPredicate.getField1());
    }

    /**
     * @return
     *       the field name of join field2. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField2Name() {
        // some code goes here
//        return null;
//        return child2.getTupleDesc().getFieldName(p.getField2());
        return child2.getTupleDesc().getFieldName(joinPredicate.getField2());
    }

    /**
     * @see TupleDesc#merge(TupleDesc, TupleDesc) for possible
     *      implementation logic.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
//        return null;
        return TupleDesc.merge(child1.getTupleDesc(), child2.getTupleDesc());
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here

        child1.open();
        child2.open();
        super.open();

    }

    public void close() {
        // some code goes here


        super.close();
        child2.close();
        child1.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
//        this.close();
//        this.open();
        child1.rewind();
        child2.rewind();
        t = null;
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no
     * more tuples. Logically, this is the next tuple in r1 cross r2 that
     * satisfies the join predicate. There are many possible implementations;
     * the simplest is a nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of Join
     * are simply the concatenation of joining tuples from the left and right
     * relation. Therefore, if an equality predicate is used there will be two
     * copies of the join attribute in the results. (Removing such duplicate
     * columns can be done with an additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     *
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */

    // 元组t是临时变量

    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        // t 的意义就是 笛卡尔积, 一个可能对多个相等
        while (child1.hasNext() || t != null){
            if(child1.hasNext() && t == null){
                t = child1.next();
            }
            while(child2.hasNext()){
                Tuple t2 = child2.next();
                if(joinPredicate.filter(t, t2)){
                    TupleDesc td1 = t.getTupleDesc();
                    TupleDesc td2 = t2.getTupleDesc();
                    // 合并
                    TupleDesc tupleDesc = TupleDesc.merge(td1, td2);
                    // 创建新的行
                    Tuple newTuple = new Tuple(tupleDesc);
                    // 设置路径
                    newTuple.setRecordId(t.getRecordId());
                    // 合并
                    int i = 0;
                    for (; i < td1.numFields(); i++) {
                        newTuple.setField(i, t.getField(i));
                    }
                    for (int j = 0; j < td2.numFields(); j++) {
                        newTuple.setField(i + j, t2.getField(j));
                    }
                    // 遍历完t2后重置，t置空，准备遍历下一个
                    if(!child2.hasNext()){
                        child2.rewind();
                        t = null;
                    }
                    return newTuple;
                }
            }
            // 重置 child2
            child2.rewind();
            t = null;
        }
        return null;
    }

    @Override
    public OpIterator[] getChildren() {
        // some code goes here
//        return null;
        return new OpIterator[]{child1, child2};
    }

    @Override
    public void setChildren(OpIterator[] children) {
        // some code goes here
        this.child1 = children[0];
        this.child2 = children[1];
    }

}
















