package simpledb.transaction;

import simpledb.storage.BufferPool;
import simpledb.storage.PageId;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class PageLockManager {
    private class Lock{
        TransactionId tid;
        int lockType;   // 0 for shared lock and 1 for exclusive lock

        public Lock(TransactionId tid,int lockType){
            this.tid = tid;
            this.lockType = lockType;
        }
    }

    ConcurrentHashMap<PageId, Vector<Lock>> lockMap;

    public PageLockManager(){
        lockMap = new ConcurrentHashMap<PageId,Vector<Lock>>();
    }

    public synchronized boolean acquireLock(PageId pid,TransactionId tid,int lockType){
        // if no lock held on pid
        if(lockMap.get(pid) == null){
            Lock lock = new Lock(tid,lockType);
            Vector<Lock> locks = new Vector<>();
            locks.add(lock);
            lockMap.put(pid,locks);

            return true;
        }

        // if some Tx holds lock on pid
        // locks.size() won't be 0 because releaseLock will remove 0 size locks from lockMap
        Vector<Lock> locks = lockMap.get(pid);

        // if tid already holds lock on pid
        for(Lock lock:locks){
            if(lock.tid == tid){
                // already hold that lock
                if(lock.lockType == lockType)
                    return true;
                // already hold exclusive lock when acquire shared lock
                if(lock.lockType == 1)
                    return true;
                // already hold shared lock,upgrade to exclusive lock
                if(locks.size()==1){
                    lock.lockType = 1;
                    return true;
                }else{
                    return false;
                }
            }
        }

        // if the lock is a exclusive lock
        if (locks.get(0).lockType ==1){
            assert locks.size() == 1 : "exclusive lock can't coexist with other locks";
            return false;
        }

        // if no exclusive lock is held, there could be multiple shared locks
        if(lockType == 0){
            Lock lock = new Lock(tid,0);
            locks.add(lock);
            lockMap.put(pid,locks);

            return true;
        }
        // can not acquire a exclusive lock when there are shard locks on pid
        return false;
    }


    public synchronized boolean releaseLock(PageId pid,TransactionId tid){
        // if not a single lock is held on pid
        assert lockMap.get(pid) != null : "page not locked!";
        Vector<Lock> locks = lockMap.get(pid);

        for(int i=0;i<locks.size();++i){
            Lock lock = locks.get(i);

            // release lock
            if(lock.tid == tid){
                locks.remove(lock);

                // if the last lock is released
                // remove 0 size locks from lockMap
                if(locks.size() == 0)
                    lockMap.remove(pid);
                return true;
            }
        }
        // not found tid in tids which lock on pid
        return false;
    }


    public synchronized boolean holdsLock(PageId pid,TransactionId tid){
        // if not a single lock is held on pid
        if(lockMap.get(pid) == null)
            return false;
        Vector<Lock> locks = lockMap.get(pid);

        // check if a tid exist in pid's vector of locks
        for(Lock lock:locks){
            if(lock.tid == tid){
                return true;
            }
        }
        return false;

    }


//    public synchronized void completeTransaction(TransactionId tid) {
////        Set<PageId> ids = lockMap.keySet();
//        for (PageId pageId : lockMap.keySet()) {
//            releaseLock(pageId, tid);
//        }
//    }



}
