import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * AsynchronousTests are test cases for AsynchronousSetup.
 * 
 * @author wayneng
 *
 */
public class AsynchronousTests extends AsynchronousSetup {
	public void t00() {
		TestPermutation t = new TestPermutation();
		t.test();
	}
	
	public void t01() {
		List<String> listString = new ArrayList<>();
		listString.add("abcdefghijklmnop");
		listString.add("lmnopqrstuvwxyx");
		listString.add("01234567890");
		listString.add("012345");
		listString.add("67890");
		listString.add("6ab7df8eh90xyz");
		listString.add("abcccdeeefffghhh");
		listString.add("abcdefghijk");
		listString.add("abcdefg0123456789");
		int szList = listString.size();
		CountDownLatch countDownLatch = new CountDownLatch(szList);

		List<Future> listFuture = new ArrayList<>();
		for(String s: listString) {
			CallablePermute callable = new CallablePermute(s, callbackHashmap, countDownLatch);
			Future<List<String>> future = executorService.submit(callable);
			listFuture.add(future);
		}
		
		try {
			// use either countdownlatch or block on all futures
			countDownLatch.await();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		Enumeration<String> keys = callbackHashmap.getKeys();
		while(keys.hasMoreElements()) {
			String k = keys.nextElement();
			List<String> values = callbackHashmap.getList(k);
			int i = 1;
			for(String v: values) {
				print("%05d %s\n", i++, v);
			}
 		}
	}

	public void test() {
		try {
			t01();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
}

/**
 * Testbench for permutation calculations by splitting up work. 
 * 
 * Set up threadpool.
 * 
 * 
 * @author wayneng
 *
 */
class AsynchronousSetup {
	protected final int szThreadPool;
	protected ExecutorService executorService;
	protected final THREADPOOLTYPE typeThreadPool;
	protected final CallbackHashmap callbackHashmap;
	protected final ConcurrentLinkedDeque<String> queueRequest = new ConcurrentLinkedDeque<>();
	protected final ConcurrentLinkedQueue<List<String>> queueResponse = new ConcurrentLinkedQueue<>();
	protected final List<String> listStrings = null;
	public enum THREADPOOLTYPE {
		DEFAULT,
		FIXED,
		CACHED,
		SCHEDULED,
		WORK_STEALING
	}
	
	public AsynchronousSetup() {
		this(4, THREADPOOLTYPE.FIXED);
	}

	public AsynchronousSetup(final int sizeThreadPool) {
		this(sizeThreadPool, THREADPOOLTYPE.FIXED);
	}

	public AsynchronousSetup(
			final int sizeThreadPool, 
			final THREADPOOLTYPE eThreadPool) {
		final int sizeAdjusted;
		{
			if(sizeThreadPool < (1 + 3 + 3)) {
				sizeAdjusted = 10;
			} else {
				sizeAdjusted = sizeThreadPool;
			}
		}
		// do thread pool config
		{
			szThreadPool = sizeAdjusted;
			typeThreadPool = eThreadPool;
			switch(eThreadPool) {
			case CACHED: 
				final ThreadFactory threadFactory = Executors.defaultThreadFactory();
				executorService = Executors.newCachedThreadPool(threadFactory);
				break;
			case SCHEDULED:
				executorService = Executors.newScheduledThreadPool(szThreadPool);
			default:
				executorService = Executors.newFixedThreadPool(szThreadPool);
				break;
			}
		}
		callbackHashmap = new CallbackHashmap();
	}
	
	public void close() {
		if(executorService != null) {
			executorService.shutdown();
			while(!executorService.isTerminated());
		}
	}
	
	public void start() {
		final Callable producer  = new CallableProducer(listStrings, queueRequest, 100);
		final Callable consumer0 = new CallableConsumer(queueRequest, queueResponse, executorService);
		final Callable consumer1 = new CallableConsumer(queueRequest, queueResponse, executorService);
		executorService.submit(producer);
		executorService.submit(consumer0);
		executorService.submit(consumer1);
	}
	
	public void printInfo() {
	}

	public static void print(String format, Object ... args) {
        System.out.printf(format, args);
    }
}

class CallableProducer implements Callable<List<String>> {
	private ConcurrentLinkedDeque<String> queueRequest;
	private List<String> listString;
	private int max;
	private Random r;
	public CallableProducer(List<String> listString, ConcurrentLinkedDeque<String> queueRequest, int max) {
		this.listString = listString;
		this.queueRequest = queueRequest;
		this.max = max;
		r = new Random();
	}
	@Override
	public List<String> call() throws InterruptedException {
		boolean stop = false;
		int tot = 0;
		int rMin = max / 10;
		int rMax = max / 5;
		PermutationTest permutation = new PermutationTest();
		List<String> l = permutation.getRandomStringList(max, 8);
		while(!stop) {
			int sz = r.nextInt(rMax);
			if(sz < rMin) {
				sz += rMin;
			}
			for(int i = 0; i < sz; i++) {
				String string = l.get(i + tot);
				queueRequest.add(string);
				listString.add(string);
			}
			tot += sz;
			int timeSleep = r.nextInt(1000) + 500;
			Thread.sleep(timeSleep);
		}
		return l;
	}
}

class CallableConsumer implements Callable<List<String>> {
	protected final List<String> l = new ArrayList<>();
	protected PermutationTest permutation = new PermutationTest();
	protected ConcurrentLinkedDeque<String> queueRequest;
	protected ConcurrentLinkedQueue<List<String>> queueResponse;
	protected final ExecutorService executorService;
	public CallableConsumer(ConcurrentLinkedDeque<String> queueRequest, ConcurrentLinkedQueue<List<String>> queueResponse, ExecutorService executorService) {
		this.queueRequest = queueRequest;
		this.queueResponse = queueResponse;
		this.executorService = executorService;
	}
	@Override
	public List<String> call() throws InterruptedException {
		permutation.permute(l);
		return l;
	}
}

/**
 * CallbackHashmap contains ConcurrentHashmap and is a callback object
 * for CallablePermute to use. The main setup uses this object to retrieve
 * the contents. 
 * 
 * @author wayneng
 *
 */
class CallbackHashmap {
	private ConcurrentHashMap<String, List<String>> concurrentHashMap = 
		new ConcurrentHashMap<>();

	public void addList(String k, List<String> v) {
		concurrentHashMap.put(k, v);
	}

	public List<String> getList(String k) {
		return concurrentHashMap.get(k);
	}
	
	public Enumeration<String> getKeys() {
		return concurrentHashMap.keys();
	}
}

/**
 * CallablePermute used to call permutate, which adds the result to
 * callback object.
 * 
 * @author wayneng
 *
 */
class CallablePermute implements Callable<List<String>> {
	private final CallbackHashmap callback;
	private final String s;
	private final CountDownLatch countDownLatch;
	public CallablePermute(String s, CallbackHashmap callback, CountDownLatch countDownLatch) {
		this.s = s;
		this.callback = callback;
		this.countDownLatch = countDownLatch;
	}
	@Override
	public List<String> call() {
		PermutationTest t = new PermutationTest(s);
		List<String> list = new ArrayList<>();
		t.permute(list);
		callback.addList(s, list);
		countDownLatch.countDown();
		return list;
	}
}

/**
 * TestPermutation are testcases of PermutationTest.
 * 
 * @author wayneng
 *
 */
class TestPermutation {
    public static void print(String format, Object ... args) {
        System.out.printf(format, args);
    }
	public void t00() {
		String s = "abcde";
		PermutationTest t = new PermutationTest(s, 10);
		List<String> list = new ArrayList<>();
		t.permute(list);
		int i = 1;
		for(String v: list) {
			print("%04d %s\n", i++, v);
		}
	}
	public void t01() {
		List<String> listString = new ArrayList<>();
		listString.add("abcdefghijklmnop");
		listString.add("lmnopqrstuvwxyx");
		listString.add("01234567890");
		listString.add("012345");
		listString.add("67890");
		listString.add("6ab7df8eh90xyz");
		listString.add("abcccdeeefffghhh");
		listString.add("abcdefghijklmnopqrstuvwxyz");
		listString.add("abcdefghijklmnopqrstuvwxyz0123456789");
		PermutationTest t = new PermutationTest();
		List<String> list = new ArrayList<>();
		t.permute(list);
		int i = 1;
		for(String curString: listString) {
			list.clear();
			t.setString(curString);
			t.permute(list);
			for(String v: list) {
				print("%04d %s\n", i++, v);
			}
		}
	}
	public void test() {
		t00();
	}
}

/**
 * PermutationTest calculates a list of permutations of input string.
 * 
 * @author wayneng
 *
 */
class PermutationTest {
	private String s = "abcdefghijklmnopqrstuvwxyz";
	private char [] a;
	private int limit;
	private Random r = new Random();

	public static void print(String format, Object ... args) {
        System.out.printf(format, args);
    }

	public PermutationTest() {
		this("abcdefghijklmnopqrstuvwxyz", 10);
	}

	public PermutationTest(final String s) {
		this(s, 10);
	}

	public PermutationTest(final String s, final int limit) {
		this.s = s;
		this.a = s.toCharArray();
		this.limit = limit;
	}
	
	public List<String> getRandomStringList(int szList, int szString) {
		List<String> l = new ArrayList<>();
		boolean [] ab = new boolean[szString];
		char [] ac = new char[szString];
		for(int i = 0; i < szList; i++) {
			int remaining = szString;
			char [] acref = s.toCharArray();
			for(int j = 0; j < szString; j++) {
				int idx = r.nextInt(remaining);
				remaining--;
				ac[j] = acref[idx];
				// move last char into idx to simulate left shift
				acref[idx] = acref[remaining];
			}
			String newString = new String(ac);
			l.add(newString);
		}
		return l;
	}
	public void setString(String s) {
		this.s = s;
		a = s.toCharArray();
	}
	
	public String getString() {
		return s;
	}
	
	public void permute(String s, List<String> l) {
		if(l == null) {
			return;
		}
		final char [] aLocal = s.toCharArray();
		permute(aLocal, l);
	}
	
	public void permute(List<String> l) {
		permute(a, l);
	}
	
	public void sort(char [] a) {
		
	}
	
	public void permute(char [] a, List<String> l) {
		int length = a.length;
		char [] abuild = new char[length];
		boolean [] used = new boolean[length];
		for(int i = 0; i < length; i++) {
			used[i] = false;
		}
		sort(a);
		permute(a, abuild, 0, used, l);
	}
	
	/*
	 * 1234
	 * 1
	 *  12
	 *    123
	 *       1234
	 *    124
	 *       1243
	 *  13
	 *    132
	 *       1324
	 *    134
	 *       1342
	 *  14
	 *    142
	 *       1423
	 *    143
	 *       1432
	 * 2
	 * 3
	 * 4
	 */
	public void permute(
			char [] a, 
			char [] abuild,
			int j,
			boolean [] used, 
			List<String> l) {
		if(j == a.length) {
			String s = new String(abuild);
			l.add(s);
			return;
		}
		try {
			int sleepTime = r.nextInt(100);
			Thread.sleep(sleepTime);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < a.length; i++) {
			if(!used[i]) {
				used[i] = true;
				abuild[j] = a[i];
				permute(a, abuild, j+1, used, l);
				used[i] = false;
			}
		}
	}
	public void permuteSubset(
			char [] a,
			char [] abuild,
			int idxStart,
			int j,
			boolean [] used,
			List<String> l) {
		
	}
}


