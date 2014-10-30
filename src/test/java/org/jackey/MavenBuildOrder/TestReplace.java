package org.jackey.MavenBuildOrder;

public class TestReplace {
	public static void main(String[] args) {
		String b = "a.b.c";
		
		String a = "${a.b.c}";
		a = a.replace("${"+b+"}", "abc");
		System.out.println(a);
	}
}
