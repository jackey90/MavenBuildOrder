package org.jackey.MavenBuildOrder;

public class PrintUtil {
	private static boolean isCounting;

	public static void warning(String str) {
		System.out.println("Warning ***********************");
		System.out.println(str);
		System.out.println();
	}

	public static void error(String str) {
		System.out.println("Error xxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println(str);
		System.out.println();
		System.exit(0);
	}

	public static void info(String str) {
		System.out.println("Info xxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println(str);
		System.out.println();
	}

	public static void countDown(String str, int count) {
		System.out.println();
		System.out.print(str);
		for (int i = count; i >= 0; i--) {
			System.out.println("***********************  " + i
					+ "   ***********************");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void startCount() {
		isCounting = true;
		while (isCounting()) {
			for (int i = 0; i < 5; i++) {
				if (!isCounting()) {
					break;
				}
				System.out.print(".");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.print("\r");
		}
		System.out.print("\r");

	}
	
	
	public static boolean isCounting() {
		return isCounting;
	}


	public static void endCount() {
		isCounting = false;
	}
}
