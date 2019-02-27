package gr.spinellis.ckjm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import gr.spinellis.ckjm.CkjmOutputHandler;
import gr.spinellis.ckjm.ClassMetrics;
import gr.spinellis.ckjm.MetricsFilter;

public class MetricsFilterTest {

	@Test
	public void test() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<ClassMetrics> ref = new AtomicReference<>();
		CkjmOutputHandler outputHandler = new CkjmOutputHandler() {
			@Override
			public void handleClass(String name, ClassMetrics c) {
				System.out.println("name: " + name + ", WMC: " + c.getWmc());
				ref.set(c);
				latch.countDown();
			}
		};
		File f = new File("target/classes/gr/spinellis/ckjm/MetricsFilter.class");
		assertTrue("File " + f.getAbsolutePath() + " not present", f.exists());
		MetricsFilter.runMetrics(new String[] { f.getAbsolutePath() }, outputHandler);
		latch.await(1, TimeUnit.SECONDS);
		assertEquals(7, ref.get().getWmc());
	}
}
