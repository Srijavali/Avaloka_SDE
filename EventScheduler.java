import java.util.*;

class EventScheduler{
      public static boolean canAttendAll(int[][] events){
            Arrays.sort(events, (a, b) -> Integer.compare(a[0], b[0]));
            for (int i = 1; i < events.length; i++){
                if (events[i][0] < events[i - 1][1]){
                   return false;
                 }
            }
            return true;
      }
      public static int minRoomsRequired(int[][] events){
          if (events.length == 0){
             return 0;
          }
          Arrays.sort(events, (a, b) -> Integer.compare(a[0], b[0]));
          PriorityQueue<Integer> minHeap = new PriorityQueue<>();
          for (int[] event : events){
             int start = event[0];
             int end = event[1];
             if (!minHeap.isEmpty() && minHeap.peek() <= start){
                 minHeap.poll();
             }
             minHeap.offer(end);
          }
          return minHeap.size();
         }
      public static void assignRooms(int[][] events) {
    if (events.length == 0) {
        return;
    }

    int[][] sortedEvents = new int[events.length][3];

    for (int i = 0; i < events.length; i++) {
        sortedEvents[i][0] = events[i][0];
        sortedEvents[i][1] = events[i][1];
        sortedEvents[i][2] = i + 1;
    }

    Arrays.sort(sortedEvents, (a, b) -> Integer.compare(a[0], b[0]));

    PriorityQueue<int[]> minHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(a[0], b[0])
    );

    int[] roomAssignments = new int[events.length];
    int roomCount = 0;

    for (int[] event : sortedEvents) {
        int start = event[0];
        int originalIndex = event[2] - 1;
        int roomNumber;

        if (!minHeap.isEmpty() && minHeap.peek()[0] <= start) {
            roomNumber = minHeap.poll()[1];
        } else {
            roomNumber = ++roomCount;
        }

        roomAssignments[originalIndex] = roomNumber;
        minHeap.offer(new int[]{event[1], roomNumber});
    }

    for (int i = 0; i < events.length; i++) {
        System.out.println(
            "Event " + (i + 1) + " -> Room " + (char)('A' + roomAssignments[i] - 1)
        );
    }
}
      public static void main(String[] args){
          Scanner sc = new Scanner(System.in);
          int t = sc.nextInt();
          while (t-- > 0){
             int n = sc.nextInt();
             int[][] events = new int[n][2];
             for (int i = 0; i < n; i++){
                 events[i][0] = sc.nextInt();
                 events[i][1] = sc.nextInt();
             }
             System.out.println(canAttendAll(events));
             System.out.println(minRoomsRequired(events));
             System.out.println("Minimum rooms required: " + minRoomsRequired(events));
             assignRooms(events);
          }
          sc.close();
      }
}
