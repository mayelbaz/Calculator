import java.io.*;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the Calculator.\n" + "Please enter a series of assignments for the calculator to process.\n"
        + "The supported assignments are = ,-= , +=, *= and /=. \n" + "The supported operators are +, -, /, *, --  and ++ (pre/post).\n"
        + "The calculator only operate on whole numbers (integers), so mind your division!.\n"
        + "A legal variable is any combination of letters, numbers, or _ sign, that begins with a letter only.\n"
        + "Each action must be separated by exactly one space.\n"
        + "To finish the calculation and present all variables' results, please enter an empty line (press enter).");
        Calculator calculator = new Calculator();
        do{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please enter an assignment: ");
            String line = reader.readLine();
            if(line.equals(""))
                break;
            if (!calculator.validityCheck(line)) {
                System.out.println("Illegal input. Assignment was not processed. Please try again.\n");
                continue;
            }
          try {
              calculator.process(line);
              } catch (ArithmeticException e) {
                  System.out.println("You tried to divide by 0! Assignment was not processed. Please try again.\n");
              } catch (ArrayStoreException e) {
                  System.out.println("You tried to use an undeclared variable. Please try again. Assignment was not processed. Please try again.\n");
              } catch (UnsupportedOperationException e) {
              System.out.println(" Illegal operator was used. Assignment was not processed. Please try again.\n");
          }
        }while(true);
      calculator.printValues();
    }
}
