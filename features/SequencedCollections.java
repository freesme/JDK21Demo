import java.util.SequencedCollection;
import java.util.SequencedSet;

/**
 * <a href="https://openjdk.org/jeps/431">JEP 431: Sequenced Collections 序列集合</a><br><br>
 * 引入新的接口来表示具有定义的相遇顺序的集合。每个这样的集合都有定义良好的第一个元素、第二个元素，依此类推，
 * 直到最后一个元素。它还提供了统一的api来访问它的第一个和最后一个元素，并以相反的顺序处理它的元素。
 * <br>
 * Java的集合框架缺少一种集合类型来表示具有定义的相遇顺序的元素序列。它还缺乏适用于这些集合的统一操作集
 *
 * <br>First            element     	                Last element
 * <br>List    	        list.get(0)	                    list.get(list.size() - 1)
 * <br>Deque	        deque.getFirst()	            deque.getLast()
 * <br>SortedSet	    sortedSet.first()	            sortedSet.last()
 * <br>LinkedHashSet	linkedHashSet.iterator().next()	// missing
 *
 * <br>
 * {@link java.util.SequencedCollection}
 *
 *    <br>// methods promoted from Deque
 *    <br>void addFirst(E);
 *    <br>void addLast(E);
 *    <br>E getFirst();
 *    <br>E getLast();
 *    <br>E removeFirst();
 *    <br>E removeLast();
 */
public class SequencedCollections {

    /**
     * <br> {@link SequencedCollection}
     *
     * <br> {@link SequencedSet}
     * <br> {@link SequencedCollection}
     * <br> {@link java.util.SequencedMap}
     *
     * <br> List现在有SequencedCollection作为它的直接超接口，
     * <br> Deque现在有SequencedCollection作为它的直接超接口，
     * <br> LinkedHashSet还实现了SequencedSet，
     * <br> SortedSet现在有SequencedSet作为它的直接超接口，
     */
    public void sequenced(){

    }


}
