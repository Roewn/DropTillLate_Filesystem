package ch.droptilllate.filesystem.commons;

public class Timer
{
	private static long startTime;
	
	public static void start() {
		startTime = System.currentTimeMillis();
	}
	
	public static long stop(boolean consoleOutput) {
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		if (consoleOutput) {
			System.out.println(Constants.TIMER_MESSAGE + elapsedTime);
		}
		return elapsedTime;
	}
}
