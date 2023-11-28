public class PrintRuntimeMemory {

  public static void main(String[] args) {
    Runtime runtime = Runtime.getRuntime();
    long allocatedMemory = runtime.totalMemory();
    long maxMemory = runtime.maxMemory();
    long freeMemory = runtime.freeMemory();
    long usedMemory = allocatedMemory - freeMemory;
    System.out.println(" ");
    System.out.println("***********************************************");
    System.out.println("*************** JVM Memory Info ***************");
    System.out.println("***********************************************");
    System.out.println("Allocated memory : " + (allocatedMemory / 1048576) + " MB");
    System.out.println("Max memory       : " + (maxMemory / 1048576) + " MB");
    System.out.println("Free memory      : " + (freeMemory / 1048576) + " MB");
    System.out.println("Used memory      : " + (usedMemory / 1048576) + " MB");
    System.out.println("Free memory: " + ((freeMemory + (maxMemory - allocatedMemory)) / 1048576) + " MB");
    System.out.println(" ");
  }
}
