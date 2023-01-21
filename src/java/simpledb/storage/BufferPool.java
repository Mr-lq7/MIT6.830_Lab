package simpledb.storage;

import simpledb.common.Database;
import simpledb.common.Permissions;
import simpledb.common.DbException;
import simpledb.common.DeadlockException;
import simpledb.transaction.PageLockManager;
import simpledb.transaction.TransactionAbortedException;
import simpledb.transaction.TransactionId;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool checks that the transaction has the appropriate
 * locks to read/write the page.
 *
 * @Threadsafe, all fields are final
 */




public class BufferPool {
    /** Bytes per page, including header. */
    private static final int DEFAULT_PAGE_SIZE = 4096;

    private static int pageSize = DEFAULT_PAGE_SIZE;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;


    // add
    private final int numPages;
//    private final ConcurrentHashMap<Integer, PageId> pageStore;

    private final ConcurrentHashMap<PageId, Page> pageStore;
    // 页面的访问顺序
    private static class LinkedNode{
        PageId pageId;
        Page page;
        LinkedNode prev;
        LinkedNode next;
        public LinkedNode(PageId pageId, Page page){
            this.pageId = pageId;
            this.page = page;
        }
    }

    // 头节点和尾节点都是虚空节点
    // 头节点
    LinkedNode head;
    // 尾节点
    LinkedNode tail;

    private void addToHead(LinkedNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void remove(LinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    private void moveToHead(LinkedNode node) {
        remove(node);
        addToHead(node);
    }
    private LinkedNode removeTail() {
        LinkedNode node = tail.prev;
        remove(node);
        return node;

    }


//    private class Lock{
//        TransactionId tid;
//        int lockType;   // 0 for shared lock and 1 for exclusive lock
//
//        public Lock(TransactionId tid,int lockType){
//            this.tid = tid;
//            this.lockType = lockType;
//        }
//    }
//
//    private class PageLockManager{
//        ConcurrentHashMap<PageId, Vector<Lock>> lockMap;
//
//        public PageLockManager(){
//            lockMap = new ConcurrentHashMap<PageId,Vector<Lock>>();
//        }
//
//        public synchronized boolean acquireLock(PageId pid,TransactionId tid,int lockType){
//            // if no lock held on pid
//            if(lockMap.get(pid) == null){
//                Lock lock = new Lock(tid,lockType);
//                Vector<Lock> locks = new Vector<>();
//                locks.add(lock);
//                lockMap.put(pid,locks);
//
//                return true;
//            }
//
//            // if some Tx holds lock on pid
//            // locks.size() won't be 0 because releaseLock will remove 0 size locks from lockMap
//            Vector<Lock> locks = lockMap.get(pid);
//
//            // if tid already holds lock on pid
//            for(Lock lock:locks){
//                if(lock.tid == tid){
//                    // already hold that lock
//                    if(lock.lockType == lockType)
//                        return true;
//                    // already hold exclusive lock when acquire shared lock
//                    if(lock.lockType == 1)
//                        return true;
//                    // already hold shared lock,upgrade to exclusive lock
//                    if(locks.size()==1){
//                        lock.lockType = 1;
//                        return true;
//                    }else{
//                        return false;
//                    }
//                }
//            }
//
//            // if the lock is a exclusive lock
//            if (locks.get(0).lockType ==1){
//                assert locks.size() == 1 : "exclusive lock can't coexist with other locks";
//                return false;
//            }
//
//            // if no exclusive lock is held, there could be multiple shared locks
//            if(lockType == 0){
//                Lock lock = new Lock(tid,0);
//                locks.add(lock);
//                lockMap.put(pid,locks);
//
//                return true;
//            }
//            // can not acquire a exclusive lock when there are shard locks on pid
//            return false;
//        }
//
//
//        public synchronized boolean releaseLock(PageId pid,TransactionId tid){
//            // if not a single lock is held on pid
//            assert lockMap.get(pid) != null : "page not locked!";
//            Vector<Lock> locks = lockMap.get(pid);
//
//            for(int i=0;i<locks.size();++i){
//                Lock lock = locks.get(i);
//
//                // release lock
//                if(lock.tid == tid){
//                    locks.remove(lock);
//
//                    // if the last lock is released
//                    // remove 0 size locks from lockMap
//                    if(locks.size() == 0)
//                        lockMap.remove(pid);
//                    return true;
//                }
//            }
//            // not found tid in tids which lock on pid
//            return false;
//        }
//
//
//        public synchronized boolean holdsLock(PageId pid,TransactionId tid){
//            // if not a single lock is held on pid
//            if(lockMap.get(pid) == null)
//                return false;
//            Vector<Lock> locks = lockMap.get(pid);
//
//            // check if a tid exist in pid's vector of locks
//            for(Lock lock:locks){
//                if(lock.tid == tid){
//                    return true;
//                }
//            }
//            return false;
//        }
//    }

    private PageLockManager lockManager;

    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // some code goes here

        this.numPages =  numPages;
        this.pageStore = new ConcurrentHashMap<>();
        head = new LinkedNode(new HeapPageId(-1, -1), null);
        tail = new LinkedNode(new HeapPageId(-1, -1), null);
        head.next = tail;
        tail.prev = head;
        lockManager = new PageLockManager();
    }

    public static int getPageSize() {
      return pageSize;
    }

    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
    public static void setPageSize(int pageSize) {
    	BufferPool.pageSize = pageSize;
    }

    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
    public static void resetPageSize() {
    	BufferPool.pageSize = DEFAULT_PAGE_SIZE;
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, a page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public  Page getPage(TransactionId tid, PageId pid, Permissions perm)
        throws TransactionAbortedException, DbException {
        // some code goes here

        int lockType;
        if(perm == Permissions.READ_ONLY){
            lockType = 0;
        }else{
            lockType = 1;
        }
        boolean lockAcquired = false;


        // 加了死锁模块

        long start = System.currentTimeMillis();
        long timeout = new Random().nextInt(2000) + 1000;
        while(!lockAcquired){
            long now = System.currentTimeMillis();
            if(now-start > timeout){
                // TransactionAbortedException means detect a deadlock
                // after upper caller catch TransactionAbortedException
                // will call transactionComplete to abort this transition
                // give someone else a chance: abort the transaction
                throw new TransactionAbortedException();
            }
            lockAcquired = lockManager.acquireLock(pid,tid,lockType);
        }

        // 如果缓存池中没有
        if(!pageStore.containsKey(pid)){
            // 获取
            DbFile dbFile = Database.getCatalog().getDatabaseFile(pid.getTableId());
            Page page = dbFile.readPage(pid);
            // 是否超过大小
            if(pageStore.size() >= numPages){
                // 使用 LRU 算法进行淘汰最近最久未使用
                evictPage();
            }

            // 放入缓存
            pageStore.put(pid, page);

        }

        // 从 缓存池 中获取
        return pageStore.get(pid);

//        return null;
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public  void unsafeReleasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for lab1|lab2

        lockManager.releaseLock(pid,tid);
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public void transactionComplete(TransactionId tid) {
        // some code goes here
        // not necessary for lab1|lab2

        transactionComplete(tid,true);
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for lab1|lab2
//        return false;
        return lockManager.holdsLock(p,tid);
    }

    private synchronized void restorePages(TransactionId tid) {

        for (PageId pid : pageStore.keySet()) {
            Page page = pageStore.get(pid);

            if (page.isDirty() == tid) {
                int tabId = pid.getTableId();
                DbFile file =  Database.getCatalog().getDatabaseFile(tabId);
                Page pageFromDisk = file.readPage(pid);

                pageStore.put(pid, pageFromDisk);
            }
        }
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(TransactionId tid, boolean commit) {
        // some code goes here
        // not necessary for lab1|lab2
        if(commit){
            try {
                flushPages(tid);
            } catch (IOException e) {
                System.out.println("异常");
            }

        }else{
            restorePages(tid);
        }

        // 释放锁
        for(PageId pid:pageStore.keySet()){
            if(holdsLock(tid,pid))
                unsafeReleasePage(tid,pid);
        }

    }

    /**
     * Add a tuple to the specified table on behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to and any other
     * pages that are updated (Lock acquisition is not needed for lab2).
     * May block if the lock(s) cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and adds versions of any pages that have
     * been dirtied to the cache (replacing any existing versions of those pages) so
     * that future requests see up-to-date pages.
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public void insertTuple(TransactionId tid, int tableId, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1

        // 获取 数据库文件 Dbfile

        DbFile f = Database.getCatalog().getDatabaseFile(tableId);
        updateBufferPool(f.insertTuple(tid,t),tid);

    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from and any
     * other pages that are updated. May block if the lock(s) cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and adds versions of any pages that have
     * been dirtied to the cache (replacing any existing versions of those pages) so
     * that future requests see up-to-date pages.
     *
     * @param tid the transaction deleting the tuple.
     * @param t the tuple to delete
     */
    public  void deleteTuple(TransactionId tid, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
        // 查询所属表对应的文件

        DbFile f = Database.getCatalog().getDatabaseFile(t.getRecordId().getPageId().getTableId());
        updateBufferPool(f.deleteTuple(tid,t),tid);
    }


    /**
     * 更新缓存:新加的
     * @param pageList 需要更新的页面
     * @param tid 事务id
     * */
    private void updateBufferPool(List<Page> pageList, TransactionId tid) throws DbException {


        for(Page p:pageList){
            p.markDirty(true,tid);
            // update bufferpool
            if(pageStore.size() > numPages)
                evictPage();
            pageStore.put(p.getId(),p);
        }
    }




    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        // some code goes here
        // not necessary for lab1

        // add
        for (PageId pageId: pageStore.keySet()) {
            flushPage(pageId);
        }

    }



    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.

        Also used by B+ tree files to ensure that deleted pages
        are removed from the cache so they can be reused safely
    */
    public synchronized void discardPage(PageId pid) {
        // some code goes here
        // not necessary for lab1


        // 删除缓存
        pageStore.remove(pid);
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private synchronized  void flushPage(PageId pid) throws IOException {
        // some code goes here
        // not necessary for lab1




        Page p = pageStore.get(pid);
        TransactionId tid = null;
        // flush it if it is dirty
        if((tid = p.isDirty())!= null){
//            Database.getLogFile().logWrite(tid,p.getBeforeImage(),p);
//            Database.getLogFile().force();
            // write to disk
            Database.getCatalog().getDatabaseFile(pid.getTableId()).writePage(p);
            p.markDirty(false,null);
        }
    }

    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
        for (PageId pid : pageStore.keySet()) {
            Page page = pageStore.get(pid);
            if (page.isDirty() == tid) {
                flushPage(pid);
            }
        }
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws DbException {
        // some code goes here
        // not necessary for lab1



        int l = new ArrayList<>(pageStore.keySet()).size();
        for (int i = 0; i < l; i++) {
            PageId pid = new ArrayList<>(pageStore.keySet()).get(i);
            Page p = pageStore.get(pid);
            if (p.isDirty() == null) {

                discardPage(pid);
                return;
            }
        }

        // 这个抛出异常十分重要
        throw  new DbException("failed to evict page: all pages are either dirty");





    }

}
