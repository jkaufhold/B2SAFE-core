package eu.eudat.b2safe.graphmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.junit.Test;

public class StreamDemo {

	@Test
	public void showcasestream() {
		List<Integer> numbers = new ArrayList<>();
		for(int i = 0 ; i < 100 ; i ++)
			numbers.add(i);
		
		OptionalDouble averageAbove50 = numbers.stream()
				.filter(i -> i > 50)
				.mapToInt(Integer::valueOf).average();
		
		averageAbove50.ifPresent(System.out::println);
		
		String numbersBetween10and20 = numbers.stream()
				.skip(10).limit(10).map(String::valueOf).collect(Collectors.joining(", "));
		
		System.out.println(numbersBetween10and20);
	}
}
