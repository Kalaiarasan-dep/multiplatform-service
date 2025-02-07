package in.hashconnect.controller.code;

import in.hashconnect.controller.code.CustomException.Singleton;
import org.apache.commons.collections.map.ListOrderedMap;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class interview {


    public static void main(String[] args) {

        String str = "kalaiarasan";
        noRepeative(str);
    }


  static void   noRepeative(String str){
      var result =  str.chars().mapToObj(c -> Character.valueOf((char)c))
                .collect(Collectors.groupingBy(t -> t, LinkedHashMap::new, Collectors.counting()))
              .entrySet().stream()
              .filter(c -> c.getValue() == 1).map( c -> c.getKey()).findFirst().get();

        System.out.println(result);
  }

}
