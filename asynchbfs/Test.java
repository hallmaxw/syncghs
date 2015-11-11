import java.util.function.*;
import java.util.*;

public class Test {
	private List<Predicate<Test>> pendingExecutions;

	public Test() {
		pendingExecutions = new ArrayList<>();
	}

	public void run() {
		setupExecutions();
		while(pendingExecutions.size() > 0) {
			Iterator<Predicate<Test>> iter  = pendingExecutions.iterator();
			while(iter.hasNext()) {
				if(iter.next().test(this)) {
					iter.remove();
				}
			}
		}
	}

	private void setupExecutions() {
		Number val = new Number(0);

		Predicate<Test> increment = (Test test) -> {
			val.num++;
			System.out.println(val.num);
			return val.num > 10;
		};

		pendingExecutions.add(increment);
	}

	class Number {
		public int num;

		public Number(int value) {
			num = value;
		}
	}

	public static void main(String[] args) {
		Test test = new Test();
		test.run();
	}

}