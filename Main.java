import java.io.IOException;


//Main - this file in charge of testing the compiler abilities on different text(code) files

//Java verifier gets a path to file as input, and prints the file name and an informative massage about the code

public class Main {
    public static void main(String[] args) {

        for (int i = 29; i < 105; i ++) {
            try {
//                String number = i + 1 < 10 ? "0" + String.valueOf(i + 1) : String.valueOf(i + 1);
                String number = String.valueOf(i);
                System.out.println("file" + number);

                JavaVerifier verifier = new JavaVerifier("/Users/user/Desktop/Java Verifier/tests/rejavaverifier/"+ number +".sjava");
                verifier.verify();
                System.out.println("text in file no." + String.valueOf(number) + " is Valid");
            } catch (JavaVerifierException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("couldn't open file!");
            }
        }
    }

}

//^[A-Z]{1,10}$