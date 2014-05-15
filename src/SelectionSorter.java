import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JComponent;

// This class sorts an array, using the selection sort algorithm.

public class SelectionSorter {
	// This array is being sorted
	private int[] a;
	// These instance variables are needed for drawing
	private int markedPosition = -1;
	private int alreadySorted = -1;
	private Lock sortStateLock;

	// The component is repainted when the animation is paused
	private JComponent component;
	private static final int DELAY = 100;

	// Constructs a selection sorter.
	public SelectionSorter(int[] anArray, JComponent aComponent) {
		a = anArray;
		sortStateLock = new ReentrantLock();
		component = aComponent;
	}

	// Sorts the array managed by this selection sorter.
	public void sort() throws InterruptedException {
		for (int i = 0; i < a.length - 1; i++) {
			int minPos = minimumPosition(i);
			sortStateLock.lock();
			try {
				ArrayUtil.swap(a, minPos, i);
				// For animation
				alreadySorted = i;
			} finally {
				sortStateLock.unlock();
			}
			pause(2);
		}
	}

	// Finds the smallest element in a tail range of the array.
	private int minimumPosition(int from) throws InterruptedException {
		int minPos = from;
		for (int i = from + 1; i < a.length; i++) {
			sortStateLock.lock();
			try {
				if (a[i] < a[minPos]) {
					minPos = i;
				}
				// For animation
				markedPosition = i;
			} finally {
				sortStateLock.unlock();
			}
			pause(2);
		}
		return minPos;
	}

	// Draws the current state of the sorting algorithm
	public void draw(Graphics g) {
		sortStateLock.lock();
		try {
			int deltaX = component.getWidth() / a.length;
			for (int i = 0; i < a.length; i++) {
				if (i == markedPosition) {
					g.setColor(Color.RED);
				} else if (i <= alreadySorted) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.BLACK);
				}
				g.drawLine(i * deltaX, 0, i * deltaX, a[i]);
			}
		} finally {
			sortStateLock.unlock();
		}
	}

	// Pauses the animation.
	public void pause(int steps) throws InterruptedException {
		component.repaint();
		Thread.sleep(steps * DELAY);
	}
}
